package jammed;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Paths;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

public class Jelly {
	private enum ServerState{INIT, ACCEPTING, AUTHENTICATING, AUTHORIZING, SESSION}
    //private int port;
    //private String addr;
	private static ServerState state;
	
	// TODO: Add multi threading class Toast(Server)/Crumb(Connection Handler)
	// TODO: Add actual authentication
	// TODO: Add system logging

	public static void main(String[] args){
		if(DB.initialize() == false){
			System.err.println("DB Initialization Failure");
			System.exit(1);
			}
		
        try{
        	String SESSION_USERNAME = null;
        	Request req = null;
        	Request.EventType event;
        	Request response = null;
        	state = ServerState.INIT;
            Communication comm = new Communication(Communication.Type.SERVER);
            System.out.println("Jelly Server Created on port "+comm.getPort());
            
            /*
             * Currently, server will accept any and all LoginReqs as valid
             */
            
            while(true){
            	if(state == ServerState.INIT){
            		state = ServerState.ACCEPTING;
            		comm.accept();
            		continue;
            	}
            	
            	if(state == ServerState.ACCEPTING){
            		// Connection established, since accept() blocks
            		req = comm.receive();
            		event = req.getEvent();
            		switch(event){
        			case login:
                		// state = ServerState.AUTHENTICATING; // TO BE USED when we add this
                		// Send back success or failed
                		state = ServerState.SESSION;
                		LoginReq login = (LoginReq)req;
                		response = new LoginReq(login.getUsername(), true, Request.ErrorMessage.none);
                		comm.send(response);
                		SESSION_USERNAME = login.getUsername();
        				break;
        			case log:
        			case userDataDownload:
        			case userDataUpload:
        			case termination:
        				// Do nothing for all other types of request
        				break;
            		}
            		continue;
            	}
            	
            	if(state == ServerState.SESSION){
            		req = comm.receive();
            		event = req.getEvent();
            		switch(event){
            			case login:
            				// Ignore
            				break;
            			case log:
            				LogReq logreq = (LogReq)req;
            				String userLog = DB.readUserLog(logreq.getUsername(), DB.DBFileTypes.USER_LOG);
            				LogReq logResponse;
            				if(userLog != null){
            					logResponse = new LogReq(logreq.getUsername(), userLog);
            				}
            				else{
            					 logResponse = new LogReq(logreq.getUsername(), Request.ErrorMessage.badArgument); //TODO: Adjust error messages
            				}
            				comm.send(logResponse);
            				break;
            			case userDataDownload:
            				UserDataReq downloadResponse = null;
            				byte[] userData = DB.readEncodedFile(SESSION_USERNAME, DB.DBFileTypes.USER_DATA);
            				if(userData != null){
            					downloadResponse = new UserDataReq(true, UserDataReq.ReqType.download, Request.ErrorMessage.none, userData, null); //TODO: !!
            				}
            				else{
            					downloadResponse = new UserDataReq(false, UserDataReq.ReqType.download, Request.ErrorMessage.somethingBad);
            				}
            				comm.send(downloadResponse);
            				break;
            			case userDataUpload:
            				UserDataReq uploadReq = (UserDataReq)req;
            				UserDataReq uploadResponse = null;
            				if(DB.writeEncodedFile(SESSION_USERNAME, DB.DBFileTypes.USER_DATA, uploadReq.getData())){
            					uploadResponse = new UserDataReq(true, UserDataReq.ReqType.upload, Request.ErrorMessage.none);
            				}
            				else{
            					uploadResponse = new UserDataReq(true, UserDataReq.ReqType.upload, Request.ErrorMessage.somethingBad);
            				}
            				comm.send(uploadResponse);
            				break;
            			case termination:
            				TerminationReq terminate = new TerminationReq(true, Request.ErrorMessage.none);
            				comm.send(terminate);
            				comm.close();
            				break;
            			}
            			continue;
            	}
            }
        }
        catch(java.net.SocketException se){
        	// Socket closed
        	// Handle me
        	se.printStackTrace();
        }
        catch(Exception e){
            e.printStackTrace();
        }
	}
}
