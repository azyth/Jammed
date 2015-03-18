import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
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

    try {
      Communication server = new Communication(Communication.Type.CLIENT);

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
      server.send(new UserDataReq());							//send a download request
      UserDataReq datareq = (UserDataReq) server.receive(); 	//receive a download response with success and userdata or an error message
      if (datareq.getSuccess()){
    	  //proceeed
      }else{
    	  //no data will be there
    	  Request.ErrorMessage err = datareq.getError();
    	  //find out why, retry?
      }
      // (6) display the data
      
      
      // (7) track any changes that were made
      ArrayList<LoginInfo> plaindata = data.decData(datareq.getData());
      ArrayList<LoginInfo> newdata = ui.getChanges(plaindata);

      if (newdata != null) {
        // (8) if changes were made, send updated data to server for storage
        byte[] encdata = data.encData(newdata);
        server.send(new UserDataReq(encdata));					//send upload request

        UserDataReq uploadresp = (UserDataReq) server.receive();//receive upload resonse with success of error message
        if (uploadresp.getSuccess()){
        	//break out, everythign was good.
        }else{
        	//problem here, retry?
        	 
        }
        
      }
      server.close();

      // (9) close the connection with the server
      
     //TODO TerminationReq is there if we want to use it for this step.

    } catch (FileNotFoundException e) {
      ui.error("No key files found - have you enrolled yet?");
    } catch (GeneralSecurityException e) {
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
