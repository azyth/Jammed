package jammed;


// TODO: Add multi-threading class Toast(Server)/Crumb(Connection Handler)
// TODO: Add actual authentication
// TODO: Add proper system logging
// TODO: Add more verbose error handling and reporting
// TODO: Figure out System.SetProperty issues, fix KeyStore/Trusted Store

/**
 * The server class of the <code>Jammed</code> password management system.
 * 
 * @Author Daniel Etter (dje67)
 * @version Alpha (1.0) 3.20.15
 */
public class Jelly {
	private enum ServerState{INIT, ACCEPTING, AUTHENTICATING, AUTHORIZING, SESSION}
	private static ServerState state;

	public static void main(String[] args){
		//System.setProperty("javax.net.ssl.keyStore","\\serverkeystore.jks");
    	//System.setProperty("java.net.ssl.keyStorePassword", "cs5430");
    	
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
            
            while(true){
            	if(state == ServerState.INIT){
            		state = ServerState.ACCEPTING;
            		comm.accept();
            		continue;
            	}
            	
            	if(state == ServerState.ACCEPTING){
            		// Connection established, since accept() blocks until a connection is made
            		req = comm.receive();
            		event = req.getEvent();
            		switch(event){
        			case login:
                		// state = ServerState.AUTHENTICATING; //FIXME: Use when implementing authentication
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
            		DB.writeServerLog(SESSION_USERNAME+" logged in");
            		continue;
            	}
            	
            	if(state == ServerState.SESSION){
            		req = comm.receive();
            		event = req.getEvent();
            		switch(event){
            			case login:
            				// Ignore during Session server state
            				break;
            			case log:
            				LogReq logreq = (LogReq)req;
            				String userLog = DB.readUserLog(SESSION_USERNAME, DB.DBFileTypes.USER_LOG);
            				LogReq logResponse;
            				if(userLog != null){
            					logResponse = new LogReq(userLog);
            				}
            				else{
            					 logResponse = new LogReq(Request.ErrorMessage.badArgument);
            				}
            				comm.send(logResponse);
            				DB.writeServerLog(SESSION_USERNAME+" log request");
            				break;
            			case userDataDownload:
            				UserDataReq downloadResponse = null;
            				byte[] userData = DB.readEncodedFile(SESSION_USERNAME, DB.DBFileTypes.USER_DATA);
            				byte[] iv = DB.readEncodedFile(SESSION_USERNAME, DB.DBFileTypes.USER_IV);
            				if(userData != null){
            					downloadResponse = new UserDataReq(true, UserDataReq.ReqType.download, Request.ErrorMessage.none, userData, iv);
            				}
            				else{
            					downloadResponse = new UserDataReq(false, UserDataReq.ReqType.download, Request.ErrorMessage.somethingBad);
            				}
            				comm.send(downloadResponse);
            				DB.writeServerLog(SESSION_USERNAME+" data downloaded: "+downloadResponse.getSuccess());
            				break;
            			case userDataUpload:
            				UserDataReq uploadReq = (UserDataReq)req;
            				UserDataReq uploadResponse = null;
            				boolean write_succ = DB.writeEncodedFile(SESSION_USERNAME, DB.DBFileTypes.USER_DATA, uploadReq.getData());
            				boolean iv_succ = DB.writeEncodedFile(SESSION_USERNAME, DB.DBFileTypes.USER_IV, uploadReq.getIV());
            				if(write_succ && iv_succ){
            					uploadResponse = new UserDataReq(true, UserDataReq.ReqType.upload, Request.ErrorMessage.none);
            				}
            				else{
            					uploadResponse = new UserDataReq(false, UserDataReq.ReqType.upload, Request.ErrorMessage.somethingBad);
            				}
            				comm.send(uploadResponse);
            				DB.writeServerLog(SESSION_USERNAME+" data uploaded: "+uploadResponse.getSuccess());
            				break;
            			case termination:
            				TerminationReq terminate = new TerminationReq(true, Request.ErrorMessage.none);
            				comm.send(terminate);
            				comm.close();
            				DB.writeServerLog(SESSION_USERNAME+" session terminated");
            				break;
            			}
            			continue;
            	}
            }
        }
        catch(java.net.SocketException se){
        	System.out.println("Socket closed!");
        	se.printStackTrace();
        }
        catch(Exception e){
            e.printStackTrace();
        }
	}
}
