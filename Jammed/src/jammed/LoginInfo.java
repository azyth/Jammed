package jammed;

/** Wrapper for two strings. Seems moderately useful. */
public class LoginInfo implements Comparable<LoginInfo> {
  public String website = "";
  public String username = "";
  public String password = "";

  @Override
  public String toString() {
    return String.format("%-20s %-20s %-20s", website, username, password);
  }

  public int compareTo(LoginInfo l) {
    int wcomp = website.compareTo(l.website);
    if (wcomp == 0) {
      return username.compareTo(l.username);
    } else {
      return wcomp;
    }
  }
}
