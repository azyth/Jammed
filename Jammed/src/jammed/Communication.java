package jammed;

// TODO: Fix all of the exceptions!

import jammed.Request.ErrorMessage;
import jammed.UserDataReq.ReqType;

import java.io.EOFException;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.ServerSocket;

import javax.net.ServerSocketFactory;

import java.net.SocketException;

import javax.net.ssl.*;

import java.security.KeyStore;
import java.util.HashSet;
import java.util.Set;
import java.io.FileReader;
import java.io.BufferedReader;

/**
 * Communication object is a runnable thread object handling one client connection
 * 
 * @version Final (3.0) 5.5.15
 */
public class Communication implements Runnable{	
  private Socket socket = null;
  private ObjectInputStream rx = null;
  private ObjectOutputStream tx = null;

  private String username;
  private Set<String> userHash = null;


  public Communication(Socket socket, Set<String> users) throws SocketException {
    this.socket = socket;
    this.userHash = users;

    try{
      this.tx = new ObjectOutputStream(this.socket.getOutputStream());
      this.rx = new ObjectInputStream(this.socket.getInputStream());
    }
    catch(IOException io){
      throw new SocketException("Error creating a new communication handler");
    }
  }

  public void send(Request thing) throws SocketException {
    try {
      this.tx.writeObject(thing);
      this.tx.flush();
    } catch (Exception e) {
      System.err.println(e.getClass());
      throw new SocketException("Error in Communication.send!");
    }
  }

  public Request receive() throws SocketException {
    while (true) {
      try {
        Request got = (Request) this.rx.readObject();
        return got;
      } catch (EOFException e) {
    	  // This is intentionally ignored, as we ensure one object is sent at a time
    	  // therefore an EOF corresponds to the end of this communication rather than
    	  // an error.
        continue;
      } catch (Exception e) {
        System.err.println(e.getClass());
        throw new SocketException("Error in Communication.receive!");
      }
    }
  }

  public void close() {
    System.out.println("Closing connection...");
    synchronized (this.userHash) {
      System.out.println(username+" Logged Out");
      this.userHash.remove(this.username);
    }
    try {
      if (this.tx != null) this.tx.close();
      if (this.rx != null) this.rx.close();
      if (this.socket != null) this.socket.close();
    } catch (Exception e) {
      //e.printStackTrace();
      // Ignore since we don't care about this connection anymore anyway
      System.err.println("Error in Communication.close!");
    }
  }

  @Override
  public void run() {
    try{

      System.out.println("Handling new connection from "+this.socket.getInetAddress().toString());

      Request req;
      boolean loginsuccess = false;
      String username = null;

      while (!loginsuccess) {
        req = receive();

        switch (req.getEvent()) {
          case LOGIN:
            LoginReq login = (LoginReq) req;
            LoginReq loginResponse;

            if (!login.getUsername().matches("[a-zA-Z0-9]+")) {					// Add user name checks for prohibited names or chars

              loginResponse = new LoginReq(false, ErrorMessage.BAD_USERNAME);

            } else if (login.getEnrolling()) {										//enrolling == true

              // try to enroll the new user...
              synchronized (this.userHash) {
                if (!DB.searchUser(login.getUsername())) { 							// user does not exist
                  if (DB.newUser(login.getUsername(), login.getPassword())) {	 //enroll the user
                    loginResponse = new LoginReq(true, ErrorMessage.NONE);
                    username = login.getUsername();
                    this.username = username;
                    loginsuccess = true;
                    // user won't be in the hash already if it didn't exist
                    this.userHash.add(username);
                    System.out.println(username+" Logged In");
                  } else {														//failure to enroll
                    loginResponse =
                      new LoginReq(false, ErrorMessage.DATABASE_FAILURE);
                  }
                } else {
                  loginResponse =
                    new LoginReq(false, ErrorMessage.DUPLICATE_USERNAME);
                }
              }

            } else {

              // try to authenticate the new user...
              synchronized (this.userHash) {
                if (DB.searchUser(login.getUsername())) {
                  if (DB.checkUserPWD(login.getUsername(), login.getPassword())) {
                    if (!this.userHash.contains(login.getUsername())) {
                      loginResponse = new LoginReq(true, ErrorMessage.NONE);
                      username = login.getUsername();
                      this.username = username;
                      loginsuccess = true;
                      this.userHash.add(username);
                      System.out.println(username+" Logged In");
                    } else {
                      loginResponse =
                        new LoginReq(false, ErrorMessage.DUPLICATE_LOGIN);
                    }
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

            }

            send(loginResponse);
            break;

          case LOG:
            LogReq loginLogResponse = new LogReq(ErrorMessage.BAD_REQUEST);
            send(loginLogResponse);
            close();
            return;

          case USER_DATA_OP:
            UserDataReq loginUDResponse = new UserDataReq(false,
                ErrorMessage.BAD_REQUEST);
            send(loginUDResponse);
            close();
            return;

          case DELETION:
            close();
            return;

          case TERMINATION:
            close();
            return;

          default:
            System.out.println("Unknown request type received in login loop of " +
                "server.");
            close();
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
      boolean userDeleted = false;

      while (!sessionover) {
        req = receive();

        switch (req.getEvent()) {
          case LOGIN:
            LoginReq sessionloginresp;
            if (((LoginReq) req).getUpdating()) {									
              boolean sucess = DB.storeUserPWD(((LoginReq) req).getUsername(), ((LoginReq) req).getPassword());
              sessionloginresp = new LoginReq(sucess, ErrorMessage.NONE);
            }else{
              sessionloginresp =
                new LoginReq(false, ErrorMessage.BAD_REQUEST);}
            send(sessionloginresp);
            break;

          case LOG:
            String log = DB.readUserLog(username);
            LogReq logResponse = null;

            if (log == null) {
              logResponse = new LogReq(ErrorMessage.DATABASE_FAILURE);
              System.err.println("log is null");
            } else {
              logResponse = new LogReq(log);
            }

            send(logResponse);
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

            send(UDResponse);
            break;

          case DELETION:
            AccountDeletionReq deleteResponse;
            if (DB.deleteUser(username)) {
              deleteResponse = new AccountDeletionReq(true, ErrorMessage.NONE);
              userDeleted = true;
            } else {
              deleteResponse =
                new AccountDeletionReq(false, ErrorMessage.DATABASE_FAILURE);
            }
            send(deleteResponse);
            break;

          case TERMINATION:
            sessionover = true;
            if (!userDeleted) {
              writeLogs(username, "User " + username + " logged out.");
            }
            break;

          default:
            System.out.println("Session loop got unexpected request type... " +
                "exiting.");
            close();
            return;
        }
      }
      close();
    }
    catch(SocketException se){
      close();
    }
  }

  public void setTimeout(int seconds){
    try{
      this.socket.setSoTimeout(seconds*1000);
    }
    catch(SocketException se){
      se.printStackTrace();
    }
  }

  /* Writes the given string to the server log and the specified user's log.
  */
  private static void writeLogs(String username, String text) {
    if (!DB.writeServerLog(text)) {
      System.out.println("Could not write to server log...");
    }
    if (!DB.writeUserLog(username, text)) {
      System.out.println("Could not write to user " + username + "'s log...");
    }
  }
}
