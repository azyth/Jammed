import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;

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

  public void display(String text) {
    System.out.println(text);
  }

  public String getChanges() {
    return "";
    // TODO finish
  }

}
