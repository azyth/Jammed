package jammed;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.SocketException;
import java.nio.file.Paths;
import java.security.GeneralSecurityException;
import java.util.ArrayList;


public class Jammed {

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

    Communication server = null;
    try {
      server = new Communication(Communication.Type.CLIENT);
    } catch (SocketException e) {
      // should never happen: does not throw exception in client case
      return;
    }

    try {
      server.connect();

      LoginReq verified = null;
      UserData data = null;
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
          data = new UserData(login.username);
        }
      }

      // (5) get the data
      // send a download request
      server.send(new UserDataReq());
      // receive a download response with success and userdata or an error
      // message
      UserDataReq datareq = (UserDataReq) server.receive();

      if (!datareq.getSuccess()){
        throw new UserDataException(datareq.getError());
      }

      // (6) display the data
      // (7) track any changes that were made
      ArrayList<LoginInfo> plaindata = data.decData(datareq.getData(),
                                                    datareq.getIV());
      ArrayList<LoginInfo> newdata = ui.getChanges(plaindata);

      if (newdata != null) {
        // (8) if changes were made, send updated data to server for storage
        byte[] iv = data.generateIV();
        byte[] encdata = data.encData(newdata, iv); 
        server.send(new UserDataReq(encdata,iv));					//send upload request

        //receive upload resonse with success of error message
        UserDataReq uploadresp = (UserDataReq) server.receive();
        if (!uploadresp.getSuccess()){
          // something terrible happened
          throw new UserDataException(uploadresp.getError());
        }
      }

    } catch (FileNotFoundException e) {
      ui.error("No key files found - have you enrolled yet?");
    } catch (GeneralSecurityException e) {
    	e.printStackTrace();
      ui.error("Something went wrong with the cryptography - exiting...");
    } catch (UnsupportedEncodingException e) {
      ui.error("Does your system not support UTF8? That's dumb.");
    } catch (ClassCastException e) {
      ui.error("Bad response from server - exiting...");
    } catch (SocketException e) {   // TODO make specific to communication
      ui.error("Server suffered fatal DNE error - exiting. Any changes you " +
               "made to your data may be unsaved.");
    } catch (UserDataException e) {
      ui.error(Request.errToString(e.error));
    } catch (IOException e) {
    	e.printStackTrace();
      ui.error("Something bad happened with IO. Exiting.");
    }

    // (9) close the connection with the server
    //TODO TerminationReq is there if we want to use it for this step.
    server.close();
    ui.close();
  }

  private static class UserDataException extends Exception {
    Request.ErrorMessage error;

    UserDataException(Request.ErrorMessage e) {
      error = e;
    }
  }

}
