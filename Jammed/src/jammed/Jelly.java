package jammed;

import java.io.UnsupportedEncodingException;
import java.net.SocketException;

import jammed.Request.ErrorMessage;
import jammed.Request.EventType;
import jammed.UserDataReq.ReqType;


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

  public static void main(String[] args) throws SocketException {
    //System.setProperty("javax.net.ssl.keyStore","\\serverkeystore.jks");
    //System.setProperty("java.net.ssl.keyStorePassword", "cs5430");

    if (!DB.initialize()) {
      System.err.println("DB Initialization Failure");
      System.exit(1);
    }

    while (true) {
      Communication comm = new Communication(Communication.Type.SERVER);
      comm.accept();

      handle_connection(comm);

      comm.close();
    }
  }

  /*
  private enum ServerState{INIT, ACCEPTING, AUTHENTICATING, AUTHORIZING,
    SESSION}
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
          // Connection established, since accept() blocks until a connection is
          // made
          req = comm.receive();
          event = req.getEvent();
          switch(event){
            case login:
              // state = ServerState.AUTHENTICATING;
              //FIXME: Use when implementing authentication
              state = ServerState.SESSION;
              LoginReq login = (LoginReq)req;
              response =new LoginReq(login.getUsername(), true,
                  Request.ErrorMessage.none);
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
              String userLog =
                DB.readUserLog(SESSION_USERNAME, DB.DBFileTypes.USER_LOG);
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
  */

  /* Handles a single connection */
  private static void handle_connection(Communication comm)
      throws SocketException {
    // we have got a connection now, because we have called this function...

    Request req;
    boolean loginsuccess = false;
    String username = null;
    //String toLog = "";

    while (!loginsuccess) {
      req = comm.receive();

      switch (req.getEvent()) {
        case LOGIN:
          // this is the only one we want to handle at this time...
          // TODO check logging throughout
          LoginReq login = (LoginReq) req;
          LoginReq loginResponse;

          if (!login.getUsername().matches("[a-zA-Z0-9]+")) {

            loginResponse = new LoginReq(false, ErrorMessage.BAD_USERNAME);

          } else if (login.getEnrolling()) {

            // try to enroll the new user...
            if (!DB.searchUser(login.getUsername())) {
              if (DB.newUser(login.getUsername(), login.getPassword())) {
                loginResponse = new LoginReq(true, ErrorMessage.NONE);
                username = login.getUsername();
                //toLog = username + " enrolled";
                loginsuccess = true;
              } else {
                loginResponse =
                  new LoginReq(false, ErrorMessage.DATABASE_FAILURE);
              }
            } else {
              loginResponse =
                new LoginReq(false, ErrorMessage.DUPLICATE_USERNAME);
              // TODO anything to log here?
            }

          } else {

            // try to authenticate the new user...
            if (DB.searchUser(login.getUsername())) {
              if (DB.checkUserPWD(login.getUsername(), login.getPassword())) {
                loginResponse = new LoginReq(true, ErrorMessage.NONE);
                username = login.getUsername();
                //toLog = username + " logged in";
                loginsuccess = true;
              } else {

                // have to make sure that logging is ok...
                //writeLogs(login.getUsername(), "Attempt made on user " +
                //    username + "'s account");

                loginResponse =
                  new LoginReq(false, ErrorMessage.BAD_CREDENTIALS);
              }
            } else {
              loginResponse = new LoginReq(false, ErrorMessage.NO_SUCH_USER);
            }

          }

          comm.send(loginResponse);
          break;

        // TODO is there anything to log in these bad cases?
        case LOG:
          LogReq loginLogResponse = new LogReq(ErrorMessage.BAD_REQUEST);
          comm.send(loginLogResponse);
          return;

        case USER_DATA_OP:
          UserDataReq loginUDResponse = new UserDataReq(false,
              ErrorMessage.BAD_REQUEST);
          comm.send(loginUDResponse);
          return;

        case TERMINATION:
          return;

        default:
          System.out.println("Unknown request type received in login loop of " +
              "server.");
          return;
      }
    }

    // We have now successfully authenticated the user! The username parameter
    // should be set, and we know that this session has permission to access
    // that username's files

    //if (!toLog.equals("")) {
    //  writeLogs(username, toLog);
    //}

    boolean sessionover = false;

    while (!sessionover) {
      req = comm.receive();

      switch (req.getEvent()) {
        case LOGIN:
          // we don't support this anymore...
          LoginReq sessionloginresp =
            new LoginReq(false, ErrorMessage.BAD_REQUEST);
          comm.send(sessionloginresp);
          return;

        case LOG:
          String log = DB.readUserLog(username);
          LogReq logResponse = null;

          if (log == null) {
            logResponse = new LogReq(ErrorMessage.DATABASE_FAILURE);
            System.out.println("log is null");
          } else {
            logResponse = new LogReq(log);
          }

          comm.send(logResponse);
          break;

        case USER_DATA_OP:
          UserDataReq datareq = (UserDataReq) req;
          UserDataReq UDResponse = null;
          
          if (datareq.getDirection() == ReqType.DOWNLOAD) {
            // send the data to the user
            byte[] cyphertext = DB.readUserData(username);
            byte[] iv = DB.readUserIV(username);

            if (cyphertext == null || iv == null) {
              UDResponse =
                new UserDataReq(false, ErrorMessage.DATABASE_FAILURE);
              System.out.println("cypher/iv are null");
            } else {
              UDResponse =
                new UserDataReq(true, ErrorMessage.NONE, cyphertext, iv);
            }
          } else {
            // replace the data with what is in the request
            if (!DB.writeUserData(username, datareq.getData())) {
              UDResponse =
                new UserDataReq(false, ErrorMessage.DATABASE_FAILURE);
              System.out.println("could not write user data");
            } else {
              if (!DB.writeUserIV(username, datareq.getIV())) {
                UDResponse =
                  new UserDataReq(false, ErrorMessage.DATABASE_FAILURE);
                System.out.println("could not write user iv");
              } else {
                UDResponse = new UserDataReq(true, ErrorMessage.NONE);
              }
            }
          }

          comm.send(UDResponse);
          break;

        case TERMINATION:
          sessionover = true;
          writeLogs(username, "User " + username + " logged out.");
          break;

        default:
          System.out.println("Session loop got unexpected request type... " +
              "exiting.");
          return;
      }
    }

    return;
  }

  /* Writes the given string to the server log and the specified user's log.
   * Does NOT crash the program on failure... TODO is this OK? */
  private static void writeLogs(String username, String text) {
    if (!DB.writeServerLog(text)) {
      System.out.println("Could not write to server log...");
    }
    if (!DB.writeUserLog(username, text)) {
      System.out.println("Could not write to user " + username + "'s log...");
    }
  }

}
