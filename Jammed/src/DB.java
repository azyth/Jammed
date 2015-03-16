/* Marcos Pedreiro - mvp34
   3/16/15
   Class to perform file management for the server

   NOTES:
    1) As of 3/16/15, interface is not final
 */

import java.io.File;

public class DB {
	// Fields here
    //

    /* Methods */

    // May replace with a constructor
    public boolean initialize() {
        // To do
        return false;
    }

    public File readFile(String uid, String fileType) {
        // To do
        return null;
    }

    public boolean writeFile(String uid, String fileType, byte[] fileData) {
        // To do
        return false;
    }

    public boolean newUser(String uid) {
        // To do
        return false;
    }

    public boolean writeLog(byte[] dataToLog) {
        // To do
        return false;
    }

    public boolean searchUser(String uid) {
        // To do
        return false;
    }

}
