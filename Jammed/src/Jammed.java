import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Paths;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public class Jammed {

  // the folder wherein client keys are stored on the local machine
  private static final String folder = "JammedKeys";
  // TODO the filename of the client's RSA private key
  private static final String priv = "private.key";
  // TODO the filename of the client's RSA public key
  private static final String pub = "public.key";

  /**
   * Main client application: This should do the following:
   *  (1) Display a user interface
   *  (2) Get the user's login information
   *  (3) Send the login information to the server for verification
   *  (4) GOTO (2) if login information was not verified to try again
   *  (5) Get the data
   *  (6) Display the data
   *  (7) Track any changes that may have been made to the data
   *  (8) If changes were made, send updated data to the server for storage
   *  (9) Close the connection with the server
   */
  public static void main(String[] args) {
    // (1) display a user interface
    UserInterface ui = new UserInterface();

    try {
      Communication server = new Communication();

      LoginReq verified = null;
      String username = "";
      while (verified == null || !verified.getSuccess()) {
        // (2) get the user's login information
        LoginInfo login = ui.getLoginInfo();
        Request loginrequest = new LoginReq(login);

        // (3) send the login information to the server for verification
        server.send(loginrequest);

        verified = (LoginReq) server.receive();

        if (!verified.getSuccess()) {
          ui.error("Could not verifiy those credentials - please try again.");
        } else {
          username = login.username;
        }
      }

      // (5) get the data
      // TODO send a request of some sort?
      UserDataReq datareq = (UserDataReq) server.receive();
      UserData data = new UserData(datareq.getData(), username);

      // (6) display the data
      // (7) track any changes that were made
      ArrayList<LoginInfo> plaindata = data.decData(datareq.getData());
      ArrayList<LoginInfo> newdata = ui.getChanges(plaindata);

      if (newdata != null) {
        // (8) if changes were made, send updated data to server for storage
        byte[] encdata = data.encData(newdata);
        Request uploadreq = new UserDataReq(encdata,
                                            UserDataReq.ReqType.upload);

        server.send(uploadreq);

        UserDataReq uploadresp = (UserDataReq) server.receive();
        // TODO verify this somehow ????? this is bad bad bad how to fix help
      }

      // (9) close the connection with the server
      server.close();

    } catch (FileNotFoundException e) {
      ui.error("No key files found - have you enrolled yet?");
    } catch (NoSuchAlgorithmException e) {
      ui.error("Necessary cryptographic algorithms are not supported on your " +
               "machine - exiting...");
    } catch (InvalidAlgorithmParameterException | InvalidKeyException |
             InvalidKeySpecException | BadPaddingException |
             IllegalBlockSizeException | NoSuchPaddingException e) {
      ui.error("Something went wrong with the cryptography - exiting...");
    } catch (UnsupportedEncodingException e) {
      ui.error("Does your system not support UTF8? That's dumb.");
    } catch (ClassCastException e) {
      ui.error("Bad response from server - exiting...");
    } catch (Exception e) {   // TODO make specific to communication
      ui.error("Server suffered fatal DNE error - exiting. Any changes you " +
               "made to your data may be unsaved.");
    }

    ui.close();
  }

}
