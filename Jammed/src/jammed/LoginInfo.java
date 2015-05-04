package jammed;

/** Wrapper for two strings. Seems moderately useful. */
public class LoginInfo implements Comparable<LoginInfo> {

  public static final long serialVersionUID = 6L; // For serialization

  public String website = "";
  public String username = "";
  public String password = "";
  public String directory = "keys/";

  @Override
  public String toString() {
    return String.format("%-20s %-20s %-20s", website, username, password);
  }

  public String secureToString(boolean showpwd) {
    String stars = "*****";
    if(showpwd) {
      return String.format("%-20s %-20s %-20s", website, username, password);
    } else {
      return String.format("%-20s %-20s %-20s", website, username, stars);
    }
  }

  @Override
  public int compareTo(LoginInfo l) {
    int wcomp = website.compareTo(l.website);
    if (wcomp == 0) {
      return username.compareTo(l.username);
    } else {
      return wcomp;
    }
  }

  @Override
  public boolean equals(Object obj) {
    if (!(obj instanceof LoginInfo)) {
      return false;
    } else {
      LoginInfo l = (LoginInfo) obj;
      return website.equals(l.website) && username.equals(l.username);
    }
  }

  @Override
  public int hashCode() {
    return website.concat(username).hashCode();
  }
}
