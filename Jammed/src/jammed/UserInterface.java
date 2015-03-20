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
    data = ui.getChanges(data);
    ui.error("ok done");
    for (LoginInfo d : data) {
      System.out.println(d);
    }
  }

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

  public LoginInfo getLoginInfo() throws UnsupportedEncodingException,
                                         IOException {
    BufferedReader br =
      new BufferedReader(new InputStreamReader(System.in, "UTF-8"));

    LoginInfo login = new LoginInfo();

    System.out.print("Enter username: ");
    login.username = br.readLine();
    System.out.print("Enter password: ");
    login.password = br.readLine();

    return login;
  }

  public void error(String message) {
    System.out.println(message);
  }

  public void close() {}

  private void display(ArrayList<LoginInfo> data) {
    System.out.println();
    System.out.println(String.format("%-20s %-20s %-20s", "Website:",
                                     "Username:", "Password:"));
    for (LoginInfo l : data) {
      System.out.println(l);
    }
    System.out.println();
  }

  public ArrayList<LoginInfo> getChanges(ArrayList<LoginInfo> data)
      throws UnsupportedEncodingException {
    BufferedReader br =
      new BufferedReader(new InputStreamReader(System.in, "UTF-8"));
    boolean changes = false;

    Collections.sort(data);

    System.out.println("Type \"exit\" to save changes and close, or \"add\" " +
        "to add or modify an existing password.\n");

    try {
      display(data);

      System.out.print("Enter command: ");
      String command = br.readLine();

      while (!command.equals("exit")) {

        if (command.equals("add")) {
          LoginInfo l = new LoginInfo();

          System.out.print("Enter the website: ");
          l.website = br.readLine();
          System.out.print("Enter the username: ");
          l.username = br.readLine();
          System.out.print("Enter the password: ");
          l.password = br.readLine();

          int index = data.indexOf(l);

          if (l.website.contains("\n") || l.username.contains("\n") ||
              l.password.contains("\n")) {
            System.out.println("You are not allowed to have newlines in your " +
                "data.\n");
          } else if (index != -1) {
            // replace the existing element with a new one
            data.set(index, l);
            changes = true;
            display(data);
          } else {
            // add the new element to the end
            data.add(l);
            Collections.sort(data);
            changes = true;
            display(data);
          }

        } else {
          System.out.println("Unrecognized command. Use \"exit\" to save " +
              "changes and close, and \"add\" to add to or " +
              "modify the password list.\n");
        }

        System.out.print("Enter command: ");
        command = br.readLine();
      }
    } catch (IOException e) {
      System.out.println("Error reading input; exiting...");
    }

    if (changes) {
      return data;
    } else {
      return null;
    }
  }

}
