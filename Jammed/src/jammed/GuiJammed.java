package jammed;

/*
 * Created by Marcos on 5/2/15.
 */

import java.net.SocketException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;


public class GuiJammed {

    public static final String LOGFILE = "log.txt";

    public static void main(String[] args) {
    	String dir;
        String ErrorMSG = " ";


        

        Communication server = null;
        try {
            server = new Communication(Communication.Type.CLIENT);
        } catch (SocketException e) {
            // should never happen: does not throw exception in client case
            return;
        }

        LoginGUI LIG = new LoginGUI();

        try {
            server.connect();

            LoginInfo login = null;
            while(login == null) {
                login = LIG.getLogin();
            }

            ArrayList<LoginInfo> plaindata = new ArrayList<LoginInfo>();
            UserData data = null;
            boolean success = false;

            while (!success) {
                boolean enroll = login.website.equals("enroll");
                Request req = new LoginReq(login, enroll);
                server.send(req);

                LoginReq verif = (LoginReq) server.receive();
                dir = LIG.getFileChosenByUserToStoreKeys(); //TODO replace keys/ with dir
                
                if (verif == null) {

                    // the server gave a null response--this probably means we should
                    // exit...
                    ErrorMSG = "Server gave a null response";

                } else if (verif.getSuccess()) {
                    success = true;
                    
                    if (enroll) {
                    	
                        // initialize the files on this machine and an empty place to store data
                        UserData.enroll(login.username, login.password, dir); //TODO get proper filepath location to store key and IV
                        data = new UserData(login.username, login.password, dir);
                    } else {
                        // get the existing data
                        server.send(new UserDataReq());
                        UserDataReq serverdata = (UserDataReq) server.receive();

                        if (!serverdata.getSuccess()) {
                            ErrorMSG = "Could not get your data from the server";
                            //System.out.println("here");
                            //throw new UserDataException(serverdata.getError());
                        }

                        // TODO make sure this throws FNFException instead of IOException
                        data = new UserData(login.username, login.password, dir);

                        // get the data in a usable form
                        plaindata = data.decData(serverdata.getData(), serverdata.getIV());  //TODO GUI add file directory string
                    }

                } else {
                    ErrorMSG = "There was an error logging in!";
                    // display the appropriate error message and try again
                    //ui.error(Request.errToString(verif.getError()));
                }
            }
            dir = LIG.getFileChosenByUserToStoreKeys();
            // Logging in was successful, create main display
            LIG.disable(); //LIG.setVisible(false);
            // (6) display the data
            // (7) track any changes that were made
            MainGUI.GActionType action = null;
            MainGUI MAG = new MainGUI(plaindata);
            MAG.CurrentUser = login.username;

            while (action == null || action != MainGUI.GActionType.EXIT) {

                action = MAG.getAction();
                switch (action) {
                    case SAVE:
                        if (MAG.changesMade) {																// OR empty!
                            if (plaindata.toString().equalsIgnoreCase("")) {  									//blank string crypto check
                                plaindata = UserData.stringToList("default \n value \n here");
                            }
                            // (8) if changes were made, send updated data to server for storage
                            byte[] iv = UserData.generateIV();
                            byte[] encdata = data.encData(plaindata, iv);
                            server.send(new UserDataReq(encdata,iv));					//send upload request

                            //receive upload resonse with success of error message
                            UserDataReq uploadresp = (UserDataReq) server.receive();
                            if (!uploadresp.getSuccess()){
                                // something terrible happened
                                ErrorMSG = "Could not save data";
                            }
                            MAG.changesMade = false;
                        }
                    case CHANGE_PWD:
                        login = MAG.pwdChange;
                        Request req = new LoginReq(login, true, true); //enroll , update
                        server.send(req);
                        LoginReq verif = (LoginReq) server.receive();

                        if (verif == null) {
                            // the server gave a null response--this probably means we should
                            // exit...
                            ErrorMSG = "Could not verify password change";
                        } else if (verif.getSuccess()) {
                            success = true;
                        }
                        UserData.enroll(login.username, login.password, dir);			//creates new local key files TODO get proper filepath location to store keys
                        data = new UserData(login.username, login.password, dir);		//updates teh current userdata instance
                        MAG.changesMade = true;
                    case LOG:
                        server.send(new LogReq());
                        LogReq log = (LogReq) server.receive();

                        if (log.getSuccess()) {
                            Files.write(Paths.get(LOGFILE), log.getLog().getBytes("UTF-8"));
                            // TODO this might need to change; if this is just a text display
                            // method this is OK though
                            ErrorMSG = "Wrote log to file " + LOGFILE;
                        } else {
                            ErrorMSG = "Could not get log: Error " + log.getError();
                        }
                    case EXIT:
                        if (plaindata.toString().equalsIgnoreCase("")){
                            plaindata = UserData.stringToList("default \n placeholder \n inserted");
                        }
                        if (MAG.changesMade) {                                                                // OR empty!
                            // (8) if changes were made, send updated data to server for storage
                            byte[] iv = UserData.generateIV();
                            byte[] encdata = data.encData(plaindata, iv);
                            server.send(new UserDataReq(encdata, iv));                    //send upload request

                            //receive upload resonse with success of error message
                            UserDataReq uploadresp = (UserDataReq) server.receive();
                            if (!uploadresp.getSuccess()) {
                                // something terrible happened
                                ErrorMSG = "Could not save data";
                            }
                        }
                }

            }

        } catch(Exception e) {
            ErrorMSG = "There was an error";
        }
    } // END MAIN







} // END CLASS
