package jammed;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;

/**
 * For now, just a command line printer class.
 * Written this way to allow for a more impressive GUI to be built on this
 * framework later.
 */
public class UserInterface {

  // test test test
  public static void main(String[] args) throws UnsupportedEncodingException,
        IOException {
    ArrayList<LoginInfo> data = new ArrayList<LoginInfo>();
    UserInterface ui = new UserInterface();
    ui.error("hihi error");
    LoginInfo thing = ui.getLoginInfo();
    System.out.println(thing);
    ui.error("ok done");
    for (LoginInfo d : data) {
      System.out.println(d);
    }
  }

  /* Initializes and displays the user interface */
  public UserInterface() {
    System.out.println("Welcome to Jammed");
    System.out.println("..._____...");
    System.out.println("..|~~~~~|..");
    System.out.println("..|-JAM-|..");
    System.out.println("..|-----|..");
    System.out.println("...........");
    System.out.println("Your passwords have been preserved...");
    System.out.println();
  }

  /* Returns a LoginInfo object with the username and password fields filled in.
   * Usable to get new enrollment credentials as well as existing user
   * credentials. IF THIS IS AN ENROLLMENT, the "website" field of the returned
   * object should be set to "enroll". Anything else and the returned object is
   * interpreted as an existing user's credentials. */
  public LoginInfo getLoginInfo() throws UnsupportedEncodingException,
                                         IOException {
    BufferedReader br =
      new BufferedReader(new InputStreamReader(System.in, "UTF-8"));

    LoginInfo login = new LoginInfo();

    System.out.println("Enter \"*\" in the username field in order to enroll " +
        "yourself as a new user.\nUsernames should be chosen from the " +
        "alphabet [a-zA-Z0-9].");

    System.out.print("Enter username: ");
    login.username = br.readLine();

    // sanity check; should (probably?) never happen
    if (login.username == null) {
      throw new IOException();
    }

    if (login.username.equals("*")) {
      // we want to make a new user and check username validity e.g. that the
      // name comes from a correct alphabet.
      System.out.print("Enter desired username: ");
      login.username = br.readLine();

      // sanity check sanity check
      if (login.username == null) {
        throw new IOException();
      }

      // signal that this is a new user
      login.website = "enroll";
    }

    System.out.print("Enter password: ");
    login.password = br.readLine();

    // sanity check sanity check sanity check
    if (login.password == null) {
      throw new IOException();
    }

    return login;
  }
  
  public String getNewPassword() throws IOException {
	BufferedReader br = new BufferedReader(new InputStreamReader(System.in, "UTF-8"));
	
	LoginInfo login = new LoginInfo();
	String password;
	
	System.out.println("Enter a NEW password, passwords should be chosen from the " +
	    "alphabet [a-zA-Z0-9].");

	password = br.readLine();
	
	// sanity check sanity check sanity check
	if (login.password == null) {
	  throw new IOException();
	}
	
	return password;

	}

  /* Displays the given message somehow */
  public void error(String message) {
    System.out.println(message);
  }

  /* Exits the user interface */
  public void close() {}

  /* Displays the data contained in the given ArrayList */
  public void display(ArrayList<LoginInfo> data) {
    System.out.println();
    System.out.println(String.format("%-20s %-20s %-20s", "Website:",
                                     "Username:", "Password:"));
    for (LoginInfo l : data) {
      System.out.println(l);
    }
    System.out.println();
  }

  /* Gets the next user type. Actions are chosen from the enum ActionType and
   * returned in an Action object. */
  public Action getAction() throws IOException, UnsupportedEncodingException {
    String help = "Possible operations:\n" +
      "\t\"add\" adds or modifies an entry\n" +
      "\t\"remove\" deletes an entry\n" +
      "\t\"log\" obtains log files for your account\n" +
      "\t\"change\" changes the password to your account\n" +
      "\t\"exit\" saves changes and exits the program";

    BufferedReader br =
      new BufferedReader(new InputStreamReader(System.in, "UTF-8"));

    Action action = new Action();
    boolean success = false;

    System.out.println(help);

    while (!success) {
      System.out.print("Enter type: ");

      String choice = br.readLine();
      if (choice == null) {
        throw new IOException();
      }

      switch (choice) {
        case "add":

          action.info = new LoginInfo();

          System.out.print("Enter website: ");
          action.info.website = br.readLine();

          System.out.print("Enter username: ");
          action.info.username = br.readLine();

          System.out.print("Enter password: ");
          action.info.password  = br.readLine();

          // sanity check
          if (action.info.website == null || action.info.username == null ||
              action.info.password == null) {
            throw new IOException();
          }

          // TODO maybe move this to jammed?
          if (action.info.website.contains("\n") ||
              action.info.username.contains("\n") ||
              action.info.password.contains("\n")) {
            System.out.println("You are not allowed to have newlines in your " +
                "data");
          } else {
            success = true;
            action.type = ActionType.ADD;
          }
          break;

        case "remove":

          action.info = new LoginInfo();

          System.out.print("Enter website: ");
          action.info.website = br.readLine();

          System.out.print("Enter username: ");
          action.info.username = br.readLine();

          // sanity check
          if (action.info.website == null || action.info.username == null) {
            throw new IOException();
          }

          success = true;
          action.type = ActionType.REMOVE;
          break;

        case "log":

          success = true;
          action.type = ActionType.LOG;
          break;

        case "change":

          action.info = new LoginInfo();
          System.out.print("Enter new password: ");
          action.info.password = br.readLine();

          if (action.info.password == null) {
            throw new IOException();
          }

          success = true;
          action.type = ActionType.CHANGE;
          break;

        case "exit":

          success = true;
          action.type = ActionType.EXIT;
          break;

        default:

          System.out.println(help);
      }
    }

    return action;
  }

  /* Everything that the user can do. Note that "ADD" covers both adding and
   * modifying existing passwords, and "CHANGE" refers to modifying the overall
   * account password. */
  public enum ActionType {ADD, REMOVE, LOG, CHANGE, EXIT};

  /* Holds an ActionType and a LoginInfo containing information pertaining to
   * that type. */
  public class Action {
    public ActionType type;
    public LoginInfo info;
  }



}
