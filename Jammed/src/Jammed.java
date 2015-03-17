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

    UserData data = new UserData();
    try {
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

    UserDataReq verifiedrequest = null;
    while (verifiedrequest == null || verifiedrequest.getData() == null) {
      // (2) get the user's login information
      LoginInfo login = ui.getLoginInfo();
      Request loginrequest = new LoginReq(login);

      // (3) send the login information to the server for verification
      try {
        server.send(loginencrypt);

        verifiedresp = server.receive();
        verifiedrequest = (UserDataReq) Request.fromJSON(verifiedresp);
      } catch (ClassCastException e) {
        ui.error("Got a bad response from the server - exiting.");
        return;
      } catch (Exception e) { // TODO make this more specific to the connection
        ui.error("Lost connection with server - exiting.");
        return;
      }

      if (verifiedrequest != null && verifiedrequest.getData == null) {
        ui.error("Could not verifiy those credentials - please try again.");
      }
    }

    // (5) display the data
    ArrayList<LoginInfo> plaindata = data.decData(verifiedrequest.getData());
    ui.display(plaindata);

    // (6) track any changes that were made
    ArrayList<LoginInfo> newdata = ui.getChanges();
    if (newdata != null) {
      // (7) if changes were made, send updated data to server for storage
      byte[] encdata = data.encData(newdata);
      Request uploadreq = new UserDataReq(encdata, UserDataReq.upload);

      String uploadrespstr;
      try {
        server.send(uploadreq.toString());

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
