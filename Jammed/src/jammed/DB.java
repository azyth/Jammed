package jammed;

/* Marcos Pedreiro - mvp34
   3/16/15
   Class to perform file management for the server

   NOTES:
    1) As of 3/29/15, interface has been refactored
    2) Enums depreciated after refactoring
 */


import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Random;

// TODO: Secure logs
public class DB {

    private static final String serverPath = "root/server/";
    private static final String usersPath = "root/users/";
    private static final String serverLogPath = serverPath + "log/";
    private static final Charset charsetUTF8 = Charset.forName("UTF-8");


    private static final String userDataSuffix = "_USERDATA.txt";
    private static final String userPWDSuffix = "_PWD.txt";
    private static final String userIVSuffix = "_IV.txt";
    private static final String userLogSuffix = "_LOG.txt";

    private static final Random sRANDOM = new SecureRandom();
    private static final int iterations = 20000;
    private static final int desiredKeyLen = 512;

    @Deprecated
    public enum DBFileTypes {
        USER_DATA, USER_PWD_FILE, USER_LOG, USER_IV
    }

    /*
    public static void main(String[] args) {
        System.out.println("Use the junit Test instead");
    }
    */

    /** Purpose: Initializes the file structure for the DB in the current folder
     *           and creates the server log. Only should be called once per install.
     *           Does nothing if file structure exists already.
     *  Input: None
     *  Output: A folder tree root/(server or users)/, and log.txt under server with
     *          time stamp of initialization.
     *  Return: Boolean, true if initialization was successful
     * */
    public static boolean initialize() {
        Path server = Paths.get(serverPath);
        Path user = Paths.get(usersPath);
        Path log = Paths.get(serverLogPath);

        if(Files.exists(server) && Files.exists(user)) {
            //System.out.println("DB has already been initialized");
            return true;
        }

        // Create root and server folder
        try {
            Files.createDirectories(server);
            Files.createDirectories(log);
        } catch(Exception e) {
            //System.out.println("Could not create root and server dir!");
            return false;
        }

        // Create user folder
        try {
            Files.createDirectories(user);
        } catch(Exception e) {
            //System.out.println("Could not create root and user dir!");
            return false;
        }

        // create server log file
        try {
            Path lgPath = Paths.get(serverLogPath + "log.txt");
            Files.createFile(lgPath);
            FileOutputStream initLog = new FileOutputStream(serverLogPath + "log.txt");

            String timeStamp = new SimpleDateFormat("yyyy/MM/dd_HH:mm:ss").format(Calendar.getInstance().getTime());
            String initialCreation = "Database initialization time: " + timeStamp + "\n";
            byte[] iCBytes = initialCreation.getBytes(charsetUTF8);
            initLog.write(iCBytes);
            initLog.close();

        } catch(Exception e) {
            //System.out.println("Could not create server log file!");
            return false;
        }

        return true;

    }

    /********************************************************/
    /***********               Users               **********/
    /********************************************************/

    /** Purpose: Adds a new user to be managed by the server.
     *  Input: A unique user id: uid.
     *  Output: A new user folder with name uid: root/users/uid/
     *          Writes that the user was created to the user and server log.
     *  Return: Boolean, true if creation was successful.
     * */
    public static boolean newUser(String uid) {
        Path newUser = Paths.get(usersPath + uid + "/");

        if(Files.exists(newUser)) {
            //System.out.println("User already exists");
            return true;
        }
        // Create root and server folder
        try {
            Files.createDirectories(newUser);

            // initialize log
            String userInit = "User " + uid + " Created";
            boolean initUserLog = writeUserLog(uid, userInit);
            boolean recordOnServerLog = writeLog(userInit);
            if(!initUserLog || !recordOnServerLog) {
                return false;
            }
        } catch(Exception e) {
            //System.out.println("Could not create new user: " + uid);
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

    /** Purpose: Removes a user managed by the server.
     *  Input: A unique user id: uid.
     *  Output: Removal of a user folder with name uid: root/users/uid/
     *          Removes all files stored inside that folder.
     *  Return: Boolean, true if deletion was successful.
     * */
    public static boolean deleteUser(String uid) {
        if(!searchUser(uid)) {
            return false;
        }

        String user = usersPath + uid + "/";
        Path userFolderPath = Paths.get(user);
        Path userData = Paths.get(user + uid + userDataSuffix);
        Path userPWD = Paths.get(user + uid + userPWDSuffix);
        Path userIV = Paths.get(user + uid + userIVSuffix);
        Path userLog = Paths.get(user + uid + userLogSuffix);

        boolean didDelete = false;
        try {
            didDelete = Files.deleteIfExists(userData);
            didDelete = Files.deleteIfExists(userPWD);
            didDelete = Files.deleteIfExists(userIV);
            didDelete = Files.deleteIfExists(userLog);

            didDelete = Files.deleteIfExists(userFolderPath);

        } catch(Exception e) {
            return didDelete;
        }

        return didDelete;
    }

    /********************************************************/
    /***********           Server Files            **********/
    /********************************************************/

    /** Purpose: Appends data to the server log.
     *  Input: A string of the dataToLog.
     *  Output: log.txt with dataToLog appended to it.
     *  Return: Boolean, true if operation successful.
     * */
    public static boolean writeLog(String dataToLog) {
        String sLogPath = serverLogPath + "log.txt";
        Path logPath = Paths.get(sLogPath);
        if(!Files.exists(logPath)) {
            try {
                Files.createFile(logPath);
            } catch(Exception e) {
                //System.out.println("Could not create log.");
                return false;
            }
        }

        try {
            OutputStreamWriter appendLog = new OutputStreamWriter(new FileOutputStream(sLogPath, true), "UTF-8");
            BufferedWriter bw = new BufferedWriter(appendLog);

            String timeStamp = new SimpleDateFormat("yyyy/MM/dd_HH:mm:ss").format(Calendar.getInstance().getTime());
            bw.write("Time Written: " + timeStamp + " | Entry: " + dataToLog);
            bw.newLine();
            bw.flush();
            bw.close();
        } catch(Exception e) {
            //System.out.println("Could not append data to log!");
            return false;
        }
        return true;

    }

    /** Purpose: Reads the server log.
     *  Input: None.
     *  Output: None.
     *  Return: String of the server log data.
     * */
    public static String readLog() {
        Path logPath = Paths.get(serverLogPath + "log.txt");
        if(!Files.exists(logPath)) {
            return null;
        }

        byte[] fileArray;
        try {
            fileArray = Files.readAllBytes(logPath);
            return new String(fileArray, charsetUTF8);
        } catch (Exception e) {
            //System.out.println("Could not read User Data");
            return null;
        }

    }

    /********************************************************/
    /***********          Read User Files          **********/
    /********************************************************/

    /** Purpose: Reads the log from a specific user.
     *  Input: User id uid of user who's data is needed, fileType of which file to read.
     *  Output: None.
     *  Return: The data of the file (As a string).
     * */
    public static String readUserLog(String uid) {
        if(!searchUser(uid)) {
            return null;
        }

        String fileAsString;
        Path fileToRead = Paths.get(usersPath + uid + "/" + uid + userLogSuffix);

        if(Files.exists(fileToRead)) {
            byte[] fileArray;
            try {
                fileArray = Files.readAllBytes(fileToRead);
                fileAsString = new String(fileArray, charsetUTF8);
            } catch (Exception e) {
                //System.out.println("Could not read User Data");
                return null;
            }
            return fileAsString;
        }
        return null;

    }

    /** Purpose: Reads the user data of a specific user.
     *  Input: User id uid of user who's data is needed
     *  Output: None.
     *  Return: The data of the file (As a byte[]).
     * */
    public static byte[] readUserData(String uid) {
        if(!searchUser(uid)) {
            return null;
        }

        Path fileToRead = Paths.get(usersPath + uid + "/" + uid + userDataSuffix);

        if(Files.exists(fileToRead)) {
            byte[] fileArray;
            try {
                fileArray = Files.readAllBytes(fileToRead);
            } catch (Exception e) {
                //System.out.println("Could not read User Data");
                return null;
            }
            return fileArray;
        }
        return null;

    }

    /** Purpose: Reads the password file of a specific user.
     *  Input: User id uid of user who's password is needed
     *  Output: None.
     *  Return: The data of the password file (As a byte[]).
     * */
    public static byte[] readUserPWD(String uid) {
        if(!searchUser(uid)) {
            return null;
        }

        Path fileToRead = Paths.get(usersPath + uid + "/" + uid + userPWDSuffix);
        if(Files.exists(fileToRead)) {
            byte[] fileArray;
            try {
                fileArray = Files.readAllBytes(fileToRead);
            } catch (Exception e) {
                //System.out.println("Could not read User Data");
                return null;
            }
            return fileArray;
        }
        return null;

    }

    /** Purpose: Reads the IV of a specific user.
     *  Input: User id uid of user who's IV is needed
     *  Output: None.
     *  Return: The data of the IV file (As a byte[]).
     * */
    public static byte[] readUserIV(String uid) {
        if(!searchUser(uid)) {
            return null;
        }

        Path fileToRead = Paths.get(usersPath + uid + "/" + uid + userIVSuffix);
        if(Files.exists(fileToRead)) {
            byte[] fileArray;
            try {
                fileArray = Files.readAllBytes(fileToRead);
            } catch (Exception e) {
                //System.out.println("Could not read User Data");
                return null;
            }
            return fileArray;
        }
        return null;

    }

    /********************************************************/
    /***********            Write Files            **********/
    /********************************************************/

    /** Purpose: Writes (Appends) the log for a specific user.
     *  Input: User id uid of user to be update and the fileData to be written.
     *  Output: Updated (Appended) version of the log in the specified uid folder.
     *  Return: Boolean, true if operation successful.
     * */
    public static boolean writeUserLog(String uid, String fileData) {
        if(!searchUser(uid)) {
            return false;
        }

        String filePathName = usersPath + uid + "/" + uid + userLogSuffix;
        String dataToWrite;

        try {
            OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(filePathName, true), "UTF-8");
            BufferedWriter bw = new BufferedWriter(writer);

            String timeStamp = new SimpleDateFormat("yyyy/MM/dd_HH:mm:ss").format(Calendar.getInstance().getTime());
            dataToWrite = "Time Written: " + timeStamp + " | Entry: " + fileData;
            bw.write(dataToWrite);
            bw.newLine();
            bw.flush();
            bw.close();
        } catch(Exception e) {
            //System.out.println("Could not write data to file!");
            return false;
        }
        return true;

    }

    /** Purpose: Writes the user data for a specific user.
     *  Input: User id uid of user to be update and the fileData to be written.
     *  Output: Updated (Overwritten) version of the user data in the specified uid folder.
     *  Return: Boolean, true if operation successful.
     * */
    public static boolean writeUserData(String uid, byte[] fileData) {
        if (!searchUser(uid)) {
            return false;
        }

        String filePath = usersPath + uid + "/" + uid + userDataSuffix;
        try {
            FileOutputStream fileOut = new FileOutputStream(filePath);

            fileOut.write(fileData);
            fileOut.close();
        } catch (Exception e) {
            return false;
        }
        return true;

    }

    /** Purpose: Writes the password file for a specific user.
     *  Input: User id uid of user to be update and the fileData to be written.
     *  Output: Updated (Overwritten) version of the password file in the specified uid folder.
     *  Return: Boolean, true if operation successful.
     * */
    public static boolean writeUserPWD(String uid, byte[] fileData) {
        if (!searchUser(uid)) {
            return false;
        }


        String filePath = usersPath + uid + "/" + uid + userPWDSuffix;

        try {
            FileOutputStream fileOut = new FileOutputStream(filePath);

            fileOut.write(fileData);
            fileOut.close();
        } catch (Exception e) {
            return false;
        }
        return true;

    }

    /** Purpose: Writes the IV file for a specific user.
     *  Input: User id uid of user to be update and the fileData to be written.
     *  Output: Updated (Overwritten) version of the IV file in the specified uid folder.
     *  Return: Boolean, true if operation successful.
     * */
    public static boolean writeUserIV(String uid, byte[] fileData) {
        if (!searchUser(uid)) {
            return false;
        }

        String filePath = usersPath + uid + "/" + uid + userIVSuffix;

        try {
            FileOutputStream fileOut = new FileOutputStream(filePath);

            fileOut.write(fileData);
            fileOut.close();
            /*PrintWriter printer = new PrintWriter(filePath);
            String dataAsString = new String(fileData, charsetUTF8);
            printer.print(dataAsString);
            printer.close(); */
        } catch (Exception e) {
            return false;
        }
        return true;

    }

    /********************************************************/
    /***********  Secure Password Authentication   **********/
    /********************************************************/

    /** Purpose: Get the next 16 bytes of salt for passwords.
     *  Input: None.
     *  Output: None.
     *  Return: 16 bytes of salt, generated with secureRandom.
     * */
    public static byte[] getNextSalt() {
        byte[] salt = new byte[16];
        sRANDOM.nextBytes(salt);
        return salt;
    }

    /** Purpose: Hash a given password with a given salt
     *  Input: password as a string, salt as a byte[]
     *  Output: None.
     *  Return: The hashed password as a byte[]. Returns null upon failure
     * */
    public static byte[] hashPwd(String pwd, byte[] salt) { //TODO error probably happening here
        if(pwd == null || pwd.length() == 0) {
            return null;
        }

        try {
            SecretKeyFactory f = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
            SecretKey key = f.generateSecret(new PBEKeySpec(pwd.toCharArray(), salt, iterations, desiredKeyLen)
            );

            return key.getEncoded();
        } catch(Exception e) {
            return null;
        }


    }

    /** Purpose: Store a specific users password *** Should overwrite old password
     *  Input: user id and password as a string
     *  Output: Updated user PWD.txt file
     *  Return: True if successful, false otherwise.
     * */
    public static boolean storeUserPWD(String uid, String pwd) {
        if(!searchUser(uid) || pwd.contains("$")) {
            return false;
        }

        byte[] salt = getNextSalt(); // create salt
        byte[] hashedPWD = hashPwd(pwd, salt); // hash pwd

        if(hashedPWD == null) {
            return false;
        }
        String hashedPwdString = new String(hashedPWD, charsetUTF8);
        String saltAsString = new String(salt, charsetUTF8);

        // format: salt$hash
        //System.out.println("Stored values: " + saltAsString + "&" + hashedPWD);
        byte[] storedVals = (saltAsString + "$" + hashedPwdString).getBytes();

        return writeUserPWD(uid, storedVals);
    }

    /** Purpose: Authenticate a user based on a given password
     *  Input: user id and  given password as a string
     *  Output: None.
     *  Return: True if given PWD matches stored PWD.
     * */
    public static boolean checkUserPWD(String uid, String givenPWD) { //TODO currently always returns false...
        if(!searchUser(uid) || givenPWD.contains("$")) {
            return false;
        }

        // load stored
        byte[] storedBytes = readUserPWD(uid);
        if(storedBytes == null) {
            return false;
        }
        String storedPWD = new String(storedBytes, charsetUTF8);
        //System.out.println("Stored values: " + storedPWD);
        String[] saltAndHash = storedPWD.split("\\$");
        if(saltAndHash.length != 2) {
            return false;
        }
        //System.out.println(storedPWD);
        String storedSalt = saltAndHash[0];
        String storedHash = saltAndHash[1];

        // hash given
        byte[] hashOfGiven = hashPwd(givenPWD, storedSalt.getBytes());
        if(hashOfGiven == null) {
            return false;
        }

        System.out.println("Stored values: " + storedPWD);
        System.out.println("Given values: " + hashOfGiven);

        return storedHash.getBytes() == hashOfGiven;

        // compare
        //String givenString = new String(hashOfGiven, charsetUTF8);
        //System.out.println("Hash of given: " + givenString);
        //return storedHash.equals(givenString);


    }


    /********************************************************/
    /********************************************************/
    /***********            DEPRECIATED            **********/
    /********************************************************/
    /********************************************************/

    /** Purpose: Reads the log from a specific user.
     *  Input: User id uid of user who's data is needed, fileType of which file to read.
     *  Output: None.
     *  Return: The data of the file (As a string).
     * */
    @Deprecated
    public static String readUserLog(String uid, DBFileTypes fileType) {
        if(!searchUser(uid)) {
            return null;
        }

        String fileName;
        String fileAsString;
        switch (fileType) {
            case USER_LOG:
                fileName = userLogSuffix;
                break;
            default:
                return null;
        }

        Path fileToRead = Paths.get(usersPath + uid + "/" + uid + fileName);
        if(Files.exists(fileToRead)) {
            byte[] fileArray;
            try {
                fileArray = Files.readAllBytes(fileToRead);
                fileAsString = new String(fileArray, charsetUTF8);
            } catch (Exception e) {
                //System.out.println("Could not read User Data");
                return null;
            }
            return fileAsString;
        }
        return null;

    }

    /** Purpose: Writes the log for a specific user.
     *  Input: User id uid of user to be update, fileType of which file to written,
     *         and the fileData to be written.
     *  Output: Updated version of the file in the specified uid folder.
     *  Return: Boolean, true if operation successful.
     * */
    @Deprecated
    public static boolean writeUserLog(String uid, DBFileTypes fileType, String fileData) {
        if(!searchUser(uid)) {
            return false;
        }

        String fileName;
        switch (fileType) {
            case USER_LOG:
                fileName = userLogSuffix;
                break;
            default:
                return false;
        }

        String dataToWrite;
        try {
            OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(usersPath + uid + "/" + uid + fileName, true), "UTF-8");
            BufferedWriter bw = new BufferedWriter(writer);
            try {
                String timeStamp = new SimpleDateFormat("yyyy/MM/dd_HH:mm:ss").format(Calendar.getInstance().getTime());
                dataToWrite = "Entry: " + fileData + " | time written: " + timeStamp;
                bw.write(dataToWrite);
                bw.newLine();
                bw.flush();
            } finally {
                bw.close();
            }
        } catch(Exception e) {
            //System.out.println("Could not write data to file!");
            return false;
        }
        return true;

    }

    /** Purpose: Reads a specified file from a specific user.
     *  Input: User id uid of user who's data is needed, fileType of which file to read.
     *  Output: None.
     *  Return: The data of the file (As a byte[]).
     * */
    @Deprecated
    public static byte[] readEncodedFile(String uid, DBFileTypes fileType) {
        if(!searchUser(uid)) {
            return null;
        }

        String fileName;
        switch (fileType) {
            case USER_DATA:
                fileName = userDataSuffix;
                break;
            case USER_PWD_FILE:
                fileName = userPWDSuffix;
                break;
            case USER_IV:
                fileName = userIVSuffix;
                break;
            default:
                return null;
        }

        Path fileToRead = Paths.get(usersPath + uid + "/" + uid + fileName);
        if(Files.exists(fileToRead)) {
            byte[] fileArray;
            try {
                fileArray = Files.readAllBytes(fileToRead);
            } catch (Exception e) {
                //System.out.println("Could not read User Data");
                return null;
            }
            return fileArray;
        }
        return null;

    }

    /** Purpose: Writes a specified file for a specific user.
     *  Input: User id uid of user to be update, fileType of which file to written,
     *         and the fileData to be written.
     *  Output: Updated version of the file in the specified uid folder.
     *  Return: Boolean, true if operation successful.
     * */
    @Deprecated
    public static boolean writeEncodedFile(String uid, DBFileTypes fileType, byte[] fileData) {
        if(!searchUser(uid)) {
            return false;
        }

        String fileName;
        switch (fileType) {
            case USER_DATA:
                fileName = userDataSuffix;
                break;
            case USER_PWD_FILE:
                fileName = userPWDSuffix;
                break;
            case USER_IV:
                fileName = userIVSuffix;
                break;
            default:
                return false;
        }

        String filePath = usersPath + uid + "/" + uid + fileName;
        // Try writing the way John does
        try {
            FileOutputStream fout = new FileOutputStream(filePath);
            try {
                fout.write(fileData);
            } finally {
                fout.close();
            }
        } catch (Exception e) {
            return false;
        }
        return true;
        /* // original
        try {
            Files.write(Paths.get(filePath), fileData);
        } catch(Exception e) {
            //System.out.println("Could not write data to file!");
            return false;
        }
        return true; */

    }


} // END CLASS
