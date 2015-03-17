import java.io.FileNotFoundException;
import java.nio.file.Paths;

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
   *  (5) Display the data
   *  (6) Track any changes that may have been made to the data
   *  (7) If changes were made, send updated data to the server for storage
   *  (8) Close the connection with the server
   */
  public static void main(String[] args) {
    // (1) display a user interface
    UserInterface ui = new UserInterface();

    NetworkCrypto netcrypt = new NetworkCrypto();
    UserData data = new UserData();
    try {
      netcrypt.load(Paths.get(folder, priv));
      data.init();
    } catch (FileNotFoundException e) {
      ui.error("No key files found - have you enrolled yet?");
      return;
    }

    Communication server;
    try {
      server = new Communication();
    } catch (Exception e) { // TODO make more specific to connection
      ui.error("Could not establish contact with server - exiting.");
      return;
    }

    boolean verified = false;
    Request verifiedrequest = null;
    while (!verified || verifiedrequest == null) {
      // (2) get the user's login information
      LoginInfo login = ui.getLoginInfo();
      Request loginrequest = new Request(login);
      String loginencrypt = netcrypt.encryptRequest(loginrequest.toString());

      // (3) send the login information to the server for verification
      String verifiedresp;
      try {
        server.send(loginencrypt);

        verifiedresp = server.receive();
      } catch (Exception e) { // TODO make this more specific to the connection
        ui.error("Lost connection with server - exiting.");
        return;
      }

      try {
        verifiedrequest = Request.fromJSON(verifiedresp);
        verified = verifiedrequest.getVerified();
      } catch (Exception e) { // TODO make this more specific to requests
        ui.error("Server did not respond as expected - exiting.");
        return;
      }
    }

    // (5) display the data
    String plaindata = data.decData(verifiedrequest.getData());
    ui.display(plaindata);

    // (6) track any changes that were made
    String newdata = ui.getChanges();
    if (newdata != null) {
      // (7) if changes were made, send updated data to server for storage
      String encdata = data.encData(newdata);
      Request uploadreq = new Request(encdata);
      String uploadenc = netcrypt.encryptRequest(uploadreq.toString());

      String uploadrespstr;
      try {
        server.send(uploadenc);

        uploadrespstr = server.receive();
      } catch (Exception e) { // TODO make this more specific to the connection
        ui.error("Lost connection to the server - exiting. Your changes are " +
                 "gone! Panic!");
        // TODO make this less faily
        return;
      }

      Request uploadresp = Request.fromJSON(uploadrespstr);
      // TODO verify this somehow ????? this is bad bad bad how to fix help
    }

    // (8) close the connection with the server
    server.close();
    ui.close();
  }

}
