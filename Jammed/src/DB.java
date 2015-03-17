/* Marcos Pedreiro - mvp34
   3/16/15
   Class to perform file management for the server

   NOTES:
    1) As of 3/16/15, interface is not final.
    2) Enums used to differentiate files.
 */

import com.sun.org.apache.xerces.internal.impl.io.UTF8Reader;
import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class DB {

    private static final String serverPath = "root/server/";
    private static final String usersPath = "root/users/";

    public enum DBFileTypes {
        USER_DATA, USER_PWD_FILE, USER_LOG
    }

    /* Testing */
    public static void main(String[] args) {
        // Init db
        boolean res = initialize();
        // make test user and search for it
        boolean mkUsr = newUser("test001");
        boolean srchUsr = searchUser("test001");
        System.out.println(srchUsr);

        // Update the server log
        String dataToLog = "Hello world";
        boolean didWriteLog = writeLog(dataToLog);
        System.out.println(didWriteLog);

        // Stress test the log
        for(int i = 0; i < 1000; i++) {
            String msg = "This is a test, #" + i;
            boolean sTest = writeLog(msg);
        }

        // Write data to a user file
        String samplePassword = "username: mvp34, password: 123";
        String passwordUpdated = "Password Updated";
        String login = "username: example, password: exampleP, salt: 123";

        boolean udRes = writeFile("test001", DBFileTypes.USER_DATA, samplePassword);
        boolean pwdRes = writeFile("test001", DBFileTypes.USER_PWD_FILE, login);
        boolean logRes = writeFile("test001", DBFileTypes.USER_LOG, passwordUpdated);

        // read that data
        String userData = readFile("test001", DBFileTypes.USER_DATA);
        String userLogin = readFile("test001", DBFileTypes.USER_PWD_FILE);
        String userLog = readFile("test001", DBFileTypes.USER_LOG);

        System.out.println("User data: " + userData);
        System.out.println("User login: " + userLogin);
        System.out.println("User Log: " + userLog);

    }

    /** Purpose: Initializes the file structure for the DB in the current folder
     *           and creates the server log. Only should be called once per install.
     *  Input: None
     *  Output: A folder tree root/(server or users)/, and log.txt under server with
     *          time stamp of initialization.
     *  Return: Boolean, true if initialization was successful
     * */
    public static boolean initialize() {
        Path server = Paths.get(serverPath);
        Path user = Paths.get(usersPath);

        if(Files.exists(server) && Files.exists(user)) {
            System.out.println("DB has already been initialized");
            return true;
        }
        // Create root and server folder
        try {
            Files.createDirectories(server);
        } catch(Exception e) {
            System.out.println("Could not create root and server dir!");
            return false;
        }
        // Create user folder
        try {
            Files.createDirectories(user);
        } catch(Exception e) {
            System.out.println("Could not create root and user dir!");
            return false;
        }

        // create server log file
        try {
            Path lgPath = Paths.get(serverPath + "log.txt");
            Files.createFile(lgPath);
            FileOutputStream initLog = new FileOutputStream(serverPath + "log.txt");
            try {
                String timeStamp = new SimpleDateFormat("yyyy/MM/dd_HH:mm:ss").format(Calendar.getInstance().getTime());
                String initialCreation = "Database initialization time: " + timeStamp + "\n";
                byte[] iCBytes = initialCreation.getBytes();
                initLog.write(iCBytes);
            } finally {
                initLog.close();
            }
        } catch(Exception e) {
            System.out.println("Could not create server log file!");
        }

        System.out.println("Successfully set up DB!");
        return true;
    }

    /** Purpose: Adds a new user to be managed by the server.
     *  Input: A unique user id: uid.
     *  Output: A new user folder with name uid: root/users/uid/
     *  Return: Boolean, true if creation was successful.
     * */
    public static boolean newUser(String uid) {
        Path newUser = Paths.get(usersPath + uid + "/");

        if(Files.exists(newUser)) {
            System.out.println("User already exists");
            return false;
        }
        // Create root and server folder
        try {
            Files.createDirectories(newUser);
        } catch(Exception e) {
            System.out.println("Could not create new user: " + uid);
            return false;
        }

        return true;
    }

    /** Purpose: Searches the database for a user specified by uid.
     *  Input: A user id, uid.
     *  Output: None.
     *  Return: True if uid found, false otherwise.
     * */
    public static boolean searchUser(String uid) {
        Path user = Paths.get(usersPath + uid + "/");
        return Files.exists(user);
    }

    /** Purpose: Appends data to the server log.
     *  Input: A string of the dataToLog.
     *  Output: log.txt with dataToLog appended to it.
     *  Return: Boolean, true if operation successful.
     * */
    public static boolean writeLog(String dataToLog) {
        Path lgname = Paths.get(serverPath + "log.txt");
        if(!Files.exists(lgname)) {
            try {
                Files.createFile(lgname);
            } catch(Exception e) {
                System.out.println("Could not create log.");
                return false;
            }
        }

        try {
            FileWriter appendLog = new FileWriter(serverPath + "log.txt", true);
            BufferedWriter bw = new BufferedWriter(appendLog);
            try {
                String timeStamp = new SimpleDateFormat("yyyy/MM/dd_HH:mm:ss").format(Calendar.getInstance().getTime());
                bw.write("Entry: " + dataToLog + " | time written: " + timeStamp);
                bw.newLine();
                bw.flush();
            } finally {
                bw.close();
            }
        } catch(Exception e) {
            System.out.println("Could not append data to log!");
            return false;
        }
        return true;

    }

    /** Purpose: Reads a specified file from a specific user.
     *  Input: User id uid of user who's data is needed, fileType of which file to read.
     *  Output: None.
     *  Return: The data of the file (As a string).
     * */
    public static String readFile(String uid, DBFileTypes fileType) { // TODO determine output type
        // To do
        if(!searchUser(uid)) {
            return null;
        }

        String fname;
        String fileAsString = "";
        switch (fileType) {
            case USER_DATA:
                fname = "USERDATA.txt";
                break;
            case USER_PWD_FILE:
                fname = "PWD.txt";
                break;
            case USER_LOG:
                fname = "LOG.txt";
                break;
            default:
                return null;
        }

        Path fileToRead = Paths.get(usersPath + uid + "/" + uid + fname);
        if(Files.exists(fileToRead)) {
            byte[] fileArray;
            try {
                fileArray = Files.readAllBytes(fileToRead);
                Charset charset=Charset.forName("UTF-8");
                fileAsString = new String(fileArray, charset);
            } catch (Exception e) {
                System.out.println("Could not read User Data");
                return null;
            }
            return fileAsString;
        }

        return null;
    }

    /** Purpose: Writes a file for a specific user.
     *  Input: User id uid of user to be update, fileType of which file to written,
     *         and the fileData to be written.
     *  Output: Updated version of the file in the specified uid folder.
     *  Return: Boolean, true if operation successful.
     * */
    public static boolean writeFile(String uid, DBFileTypes fileType, String fileData) { // TODO: Determine fileData input type
        // To do
        if(!searchUser(uid)) {
            return false;
        }

        String fname;
        boolean isLog = false;
        switch (fileType) {
            case USER_DATA:
                fname = "USERDATA.txt";
                break;
            case USER_PWD_FILE:
                fname = "PWD.txt";
                break;
            case USER_LOG:
                fname = "LOG.txt";
                isLog = true;
                break;
            default:
                return false;
        }

        String dataToWrite = fileData;
        try {
            FileWriter writer = new FileWriter(usersPath + uid + "/" + uid + fname);
            BufferedWriter bw = new BufferedWriter(writer);
            try {
                if(isLog) {
                    String timeStamp = new SimpleDateFormat("yyyy/MM/dd_HH:mm:ss").format(Calendar.getInstance().getTime());
                    dataToWrite = "Entry: " + fileData + " | time written" + timeStamp;
                }
                bw.write(dataToWrite);
                bw.newLine();
                bw.flush();
            } finally {
                bw.close();
            }
        } catch(Exception e) {
            System.out.println("Could not write data to file!");
            return false;
        }

        return false;
    }

} // END CLASS
