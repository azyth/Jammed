import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

/**
 * For now, just a command line printer class.
 * Written this way to allow for a more impressive GUI to be built on this
 * framework later.
 */
public class UserInterface {

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

  public LoginInfo getLoginInfo() {
    BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

    LoginInfo login = new LoginInfo();

    try {
      System.out.println("Enter username: ");
      login.username = br.readLine();
      System.out.println("Enter password: ");
      login.password = br.readLine();
    } catch (IOException e) {
      System.out.println("Error reading username or password - something " +
          "went very wrong...");
      System.exit(1);
    }

    return login;
  }

  public void error(String message) {
    System.out.println(message);
  }

  public void close() {}

  private void display(ArrayList<LoginInfo> data) {
    System.out.println("Website:\t\tUsername:\t\tPassword:");
    for (LoginInfo l : data) {
      System.out.println(l.website + "\t\t" + l.username + "\t\t" + l.password);
    }
  }

  public ArrayList<LoginInfo> getChanges(ArrayList<LoginInfo> data) {
    BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
    boolean changes = false;

    Collections.sort(data);

    System.out.println("Type \"exit\" to save changes and close, or \"add\" " +
                       "to add or modify an existing password.");

    try {
      display(data);

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
                               "data.");
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
                             "modify the password list.");
        }

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
