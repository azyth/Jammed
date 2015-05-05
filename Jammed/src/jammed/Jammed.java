package jammed;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.SocketException;
import java.nio.file.Paths;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Collections;

import jammed.UserInterface.Action;
import jammed.UserInterface.ActionType;


public class Jammed {

  public static final String LOGFILE = "log.txt";

  // server for the connection duration
  private static ClientCommunication server = new ClientCommunication();

  // userdata object that we can use to encrypt/decrypt the data
  private static UserData data;

  // client's data
  private static ArrayList<LoginInfo> plaindata = new ArrayList<LoginInfo>();
  private static boolean changes = false;

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
    //System.setProperty("javax.net.ssl.trustStore","serverkeystore.jks");
    //System.setProperty("java.net.ssl.trustStorePassword", "cs5430");
    // (1) display a user interface
    UserInterface ui = new UserInterface();
    String dir = "keys/";

    // add shutdown hook to send the "close" command
    Runtime.getRuntime().addShutdownHook(new Shutdown());

    try {
      server.connect();
      System.out.println("Jammed connected sending login");

      // keep out here in case we need to re-send after some timeout???
      LoginInfo login = null;
      boolean success = false;

      while (!success) {
        login = ui.getLoginInfo();
        dir = login.directory;
        boolean enroll = login.website.equals("enroll");
        Request req = new LoginReq(login, enroll);
        server.send(req);

        LoginReq verif = (LoginReq) server.receive();

        if (verif == null) {

          // the server gave a null response--this probably means we should
          // exit...
          throw new UserDataException(Request.ErrorMessage.OTHER);

        } else if (verif.getSuccess()) {

          success = true;

          if (enroll) {
            // initialize the files on this machine and an empty place to store
            // data
            UserData.enroll(login.username, login.password, dir);
            data = new UserData(login.username, login.password, dir);
          } else {
            // get the existing data
            server.send(new UserDataReq());
            UserDataReq serverdata = (UserDataReq) server.receive();

            if (!serverdata.getSuccess()) {
              System.out.println("here");
              throw new UserDataException(serverdata.getError());
            }

            // TODO make sure this throws FNFException instead of IOException
            data = new UserData(login.username, login.password, dir);

            // get the data in a usable form
          
            
            
            plaindata = data.decData(serverdata.getData(), serverdata.getIV());
          }

        } else {

          // display the appropriate error message and try again
          ui.error(Request.errToString(verif.getError()));

        }
      }

      // (6) display the data
      // (7) track any changes that were made
      Action action = null;
      ui.display(plaindata);
      boolean sessionover = false;

      while (!sessionover) {

        action = ui.getAction();

        switch (action.type) {
          case ADD:

            // check if the added thing is already in the data or not...
            int index = plaindata.indexOf(action.info);

            // modify the data as such
            if (index == -1) {
              plaindata.add(action.info);
              Collections.sort(plaindata);
            } else {
              plaindata.set(index, action.info);
            }

            changes = true;
            ui.display(plaindata);
            break;

          case REMOVE:

            // remove the element from the list, updating changes if some
            // element was removed
            boolean changed = plaindata.remove(action.info);
            changes = changes || changed;
            ui.display(plaindata);
            break;

          case LOG:

            server.send(new LogReq());
            LogReq log = (LogReq) server.receive();

            if (log.getSuccess()) {
              Files.write(Paths.get(LOGFILE), log.getLog().getBytes("UTF-8"));
              // TODO this might need to change; if this is just a text display
              // method this is OK though
              ui.error("Wrote log to file " + LOGFILE);
            } else {
              ui.error("Could not get log: Error " +
                  Request.errToString(log.getError()));
            }
            break;

          case CHANGE:

            login.password = action.info.password;
            // update password?
            Request req = new LoginReq(login, true, true); //enroll , update
            server.send(req);

            LoginReq verif = (LoginReq) server.receive();

            if (verif == null) {

              // the server gave a null response--this probably means we should
              // exit...
              ui.error("verification was null");

            } else if (verif.getSuccess()) {

              success = true;
            }

            //creates new local key files
            UserData.enroll(login.username, login.password, dir);
            //updates the current userdata instance 
            data = new UserData(login.username, login.password, dir);
            changes = true;
            break;

          case DELETE:

            Request deletereq = new AccountDeletionReq();
            server.send(deletereq);

            AccountDeletionReq deletesuccess =
              (AccountDeletionReq) server.receive();
            if (deletesuccess.getSuccess()) {
              ui.error("Account successfully deleted.");
              // TODO delete keys?
              sessionover = true;
            } else {
              ui.error("Could not delete account: " +
                  Request.errToString(deletesuccess.getError()));
            }

            break;

          case EXIT:

            sessionover = true;
            break;

          default:

            ui.error("Unknown type type.");
        }
      }

      ui.close();

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
      e.printStackTrace();
    }
  }

  // shuts down server so that something like C^c won't hang the server
  public static class Shutdown extends Thread {
    @Override
    public void run() {
      System.out.println("In shutdown hook.");
      if (server.connected()) {
        System.out.println("conencted server");
        try {
          //Blank String Crypto check 
          if (UserData.listToString(plaindata).equals("")){
            System.out.println("blank string crypto here");
            plaindata =
              UserData.stringToList("default\nplaceholder\ninserted");
            changes = true;
          }

          if (changes && data != null) {
            // (8) if changes were made, send updated data to server for storage
        	  
            byte[] iv = UserData.generateIV();
            byte[] encdata = data.encData(plaindata, iv); 
          
            
            
            server.send(new UserDataReq(encdata,iv)); //send upload request

            //receive upload resonse with success of error message
            UserDataReq uploadresp = (UserDataReq) server.receive();
            if (!uploadresp.getSuccess()){
              // something terrible happened
              System.out.println("Passwords could not be stored!");
            }
          }

          // close the connection...
          server.send(new TerminationReq(TerminationReq.Term.USER_REQUEST));
          server.close();
        } catch (GeneralSecurityException | IOException e) {
          e.printStackTrace();
        }
      }
    }
  }

  private static class UserDataException extends Exception {
    Request.ErrorMessage error;

    UserDataException(Request.ErrorMessage e) {
      error = e;
    }
  }

}
