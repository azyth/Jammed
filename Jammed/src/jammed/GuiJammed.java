package jammed;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.net.SocketException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;

public class GuiJammed {

    public static final String LOGFILE = "log.txt";
    public static final String DEFAULT_KEY_LOCATION = "defaultKeyLocation.txt";
    private static String catchallLocation = "keys/";
    // server for connection duration
    private static ClientCommunication server = new ClientCommunication();

    // userdata object that we can use to encrypt/decrypt the data
    private static UserData data;

    // GUI where passwords are stored
    private static MainGUI MG;

    public static void main(String[] args) {

        Runtime.getRuntime().addShutdownHook(new Shutdown());

        File defaultKeyDirLocation = new File(DEFAULT_KEY_LOCATION);
        try{
            if(!defaultKeyDirLocation.exists()) {
                defaultKeyDirLocation.createNewFile();
                FileOutputStream dkdlFos = new FileOutputStream(DEFAULT_KEY_LOCATION);
                dkdlFos.write("keys/".getBytes());
                dkdlFos.close();
            }
        } catch(IOException e) {
            catchallLocation = "keys/";
        }

        LoginGUI LIG = new LoginGUI();

        try {
            server.connect();
            LoginInfo login;
            ArrayList<LoginInfo> plaindata = new ArrayList<LoginInfo>();
            boolean enroll;

            while (true) {

                login = LIG.getLogin();

                enroll = login.website.equals("enroll");
                Request req = new LoginReq(login, enroll);
                if(req == null) {
                    continue;
                } else {
                    server.send(req);
                }
                LoginReq verif = (LoginReq) server.receive();

                if (verif == null) {
                    // the server gave a null response--this probably means we should exit...
                    LIG.error("Server gave a null response ;(");
                } else if (verif.getSuccess()) {
                    break; // break and finish logging in
                } else {
                    // display the appropriate error message and try again
                    LIG.error(Request.errToString(verif.getError()));
                    continue;
                }
            } // END WHILE

            String dirForKeys = LIG.getDirChosenToStoreKeys();
            try {
                List<String> defaultPathList = Files.readAllLines(Paths.get(DEFAULT_KEY_LOCATION), Charset.defaultCharset());
                dirForKeys = defaultPathList.get(0);
            } catch(Exception e) {
                dirForKeys = LIG.getDirChosenToStoreKeys(); // if something goes wrong use default "keys/" dir
            }
            if (enroll) {
                // initialize the files on this machine and an empty place to store data
                UserData.enroll(login.username, login.password, dirForKeys);
                data = new UserData(login.username, login.password, dirForKeys);
            } else {
                // get the existing data
                server.send(new UserDataReq());
                UserDataReq serverdata = (UserDataReq) server.receive();

                if (serverdata.getSuccess()) {
                    data = new UserData(login.username, login.password, dirForKeys);
                    // get the data in a usable form
                    plaindata = data.decData(serverdata.getData(), serverdata.getIV());

                } else {

                    LIG.error(serverdata.errToString(serverdata.getError()));
                    server.close();
                    return; // at this point restart server and client.
                }
            }

            LIG.disableGui();
            MG = new MainGUI(plaindata);

            while(true) {
                MainGUI.ActionClass action = MG.getAction();

                switch( action.type) {
                    case SAVE:
                        ArrayList<LoginInfo> ud = action.userData;

                        byte[] iv = UserData.generateIV();
                        byte[] encdata = data.encData(ud, iv);
                        server.send(new UserDataReq(encdata,iv)); //send upload request

                        //receive upload resonse with success of error message
                        UserDataReq uploadresp = (UserDataReq) server.receive();
                        if (!uploadresp.getSuccess()){
                            // something terrible happened
                            MG.setServerInfoLabel("Your data could not be saved");
                            throw new UserDataException(uploadresp.getError());
                        }
                        MG.setServerInfoLabel("Data Saved!");
                        MG.resetAction();
                        break;
                    case CHANGE_PWD:
                        login.password = action.pwdChange.password;
                        Request req = new LoginReq(login, true, true); //enroll , update
                        server.send(req);
                        LoginReq verif = (LoginReq) server.receive();
                        if (verif == null) {
                            // the server gave a null response--this probably means we should exit...
                            MG.setServerInfoLabel("Your password could not be changed");
                            continue;
                        } else if (verif.getSuccess()) {
                            UserData.enroll(login.username, login.password, dirForKeys);
                            data = new UserData(login.username, login.password, dirForKeys);
                            MG.setServerInfoLabel("Password changed!");
                            ArrayList<LoginInfo> udpwd = action.userData;

                            byte[] ivpwd = UserData.generateIV();
                            byte[] encdatapwd = data.encData(udpwd, ivpwd);
                            server.send(new UserDataReq(encdatapwd,ivpwd)); //send upload request

                            //receive upload resonse with success of error message
                            UserDataReq uploadresppwd = (UserDataReq) server.receive();
                            if (!uploadresppwd.getSuccess()){
                                // something terrible happened
                                MG.setServerInfoLabel("Your data could not be saved");
                                throw new UserDataException(uploadresppwd.getError());
                            }
                            MG.setServerInfoLabel("Data Saved!");
                            MG.resetAction();
                        }
                        MG.resetAction();
                        break;
                    case LOG:
                        server.send(new LogReq());
                        LogReq log = (LogReq) server.receive();

                        if (log.getSuccess()) {
                            Files.write(Paths.get(LOGFILE), log.getLog().getBytes("UTF-8"));
                            MG.setServerInfoLabel("Log written to: " + LOGFILE);
                        } else {
                            MG.setServerInfoLabel("Could not get log!");
                        }
                        MG.resetAction();
                        break;
                    case EXIT:
                        ArrayList<LoginInfo> ude = action.userData;

                        MG.setServerInfoLabel("Saving Changes...");
                        byte[] ive = UserData.generateIV();
                        byte[] encdatae = data.encData(ude, ive);
                        server.send(new UserDataReq(encdatae, ive)); //send upload request

                        //receive upload resonse with success of error message
                        UserDataReq uploadrespe = (UserDataReq) server.receive();
                        if (!uploadrespe.getSuccess()) {
                            // something terrible happened
                            MG.setServerInfoLabel("Could not save changes");
                            throw new UserDataException(uploadrespe.getError());
                        }
                        // close the connection...
                        MG.resetAction();
                        server.send(new TerminationReq(TerminationReq.Term.USER_REQUEST));
                        server.close();
                        System.exit(0);
                        break;
                    case DEL_ACCOUNT:
                        if(action.DELETE_ACCOUNT) {
                            LoginInfo userToDelete = action.pwdChange;
                            // userToDeleteContains the username of the user who requested that change
                            // and the website field says "DELETE"
                            // TODO send delete request to server
                            System.out.println(userToDelete.username);
                        }
                        MG.resetAction();
                        //server.send(new TerminationReq(TerminationReq.Term.USER_REQUEST));
                        //server.close();
                        //System.exit(0);
                        break;
                    case NULL:
                        // do nothing
                        MG.setServerInfoLabel("Not a valid action");
                        MG.resetAction();
                        break;
                    default:
                        // do nothing
                        MG.setServerInfoLabel("Default");
                        MG.resetAction();
                        break;
                }// end switch
            } // end while
        } catch (FileNotFoundException e) {
            System.out.println("No key files found - have you enrolled yet?");
            e.printStackTrace();
        } catch (GeneralSecurityException e) {
            System.out.println("Something went wrong with the cryptography - exiting...");
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            System.out.println("Does your system not support UTF8? That's dumb.");
            e.printStackTrace();
        } catch (ClassCastException e) {
            System.out.println("Bad response from server - exiting...");
            e.printStackTrace();
        } catch (SocketException e) {
            System.out.println("Server suffered fatal DNE error - exiting. Any changes you made to your data may be unsaved.");
            e.printStackTrace();
        } catch (UserDataException e) {
            System.out.println(Request.errToString(e.error));
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("Something bad happened with IO. Exiting.");
            e.printStackTrace();
        } catch(InterruptedException e) {
            System.out.println("Something got interrupted.");
            e.printStackTrace();
        } catch(Exception e) {
            System.out.println("Something unplanned happened.");
            e.printStackTrace();
        }

        server.close();
    }

    // shuts down the connection so that exiting unexpectedly won't hang the
    // server
    public static class Shutdown extends Thread {
      @Override
      public void run() {
        if (server.connected() && MG != null) {
          try {
            ArrayList<LoginInfo> plaindata = MG.getUserDataArray();
            boolean changes = MG.getChangesMade();

            //Blank String Crypto check 
            if (UserData.listToString(plaindata).equals("")){
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
} // END CLASS
