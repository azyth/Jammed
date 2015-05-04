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

    if (!DB.initialize()) {
      System.err.println("DB Initialization Failure");
      System.exit(1);
    }

    while (true) {
      Communication comm = new Communication();
      System.out.println("Accepting client...");
      comm.accept();

      handle_connection(comm);

      comm.close();
    }
  }

  /* Handles a single connection */
  private static void handle_connection(Communication comm)
      throws SocketException {
    // we have got a connection now, because we have called this function...
    System.out.println("Handling connection...");

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

          if (!login.getUsername().matches("[a-zA-Z0-9]+")) {					// Add user name checks for prohibited names or chars

        	  loginResponse = new LoginReq(false, ErrorMessage.BAD_USERNAME);

          } else if (login.getEnrolling()) {										//enrolling == true

				// try to enroll the new user...
				if (!DB.searchUser(login.getUsername())) { 							// user does not exist
					  if (DB.newUser(login.getUsername(), login.getPassword())) {	 //enroll the user
						    loginResponse = new LoginReq(true, ErrorMessage.NONE);
						    username = login.getUsername();
						    //toLog = username + " enrolled";
						    loginsuccess = true;
					  } else {														//failure to enroll
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
        	LoginReq sessionloginresp;
        	if (((LoginReq) req).getUpdating()) {									
        		boolean sucess = DB.storeUserPWD(((LoginReq) req).getUsername(), ((LoginReq) req).getPassword());
        		System.out.println(sucess);
        		sessionloginresp = new LoginReq(sucess, ErrorMessage.NONE);
			}else{
				sessionloginresp =
						new LoginReq(false, ErrorMessage.BAD_REQUEST);}
        	comm.send(sessionloginresp);
        	break;

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
              System.out.println("Ciphertext: " + cyphertext);
              System.out.println("IV: " + iv);
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
