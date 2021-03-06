package jammed;

/* Marcos Pedreiro - mvp34
   Created: 3/16/15
   Class to perform file management for the server

   NOTES:
    1) 4/27/15 - All methods are synchronized for thread safety
 */

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
    private static final int saltLength = 16;
    
    /** Purpose: Initializes the file structure for the DB in the current folder
     *           and creates the server log. Only should be called once per install.
     *           Does nothing if file structure exists already.
     *  Input: None
     *  Output: A folder tree root/(server or users)/, and log.txt under server with
     *          time stamp of initialization.
     *  Return: Boolean, true if initialization was successful
     *  IMPORTANT: If either the server directory or the users directory does not exist,
     *  Will proceed with initialization and overwrite what was there.
     * */
    public static synchronized boolean initialize() {
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
        FileOutputStream initLog = null;
        try {
            Path lgPath = Paths.get(serverLogPath + "log.txt");
            Files.createFile(lgPath);
            initLog = new FileOutputStream(serverLogPath + "log.txt");

            String timeStamp = new SimpleDateFormat("yyyy/MM/dd_HH:mm:ss").format(Calendar.getInstance().getTime());
            String initialCreation = "Database initialization time: " + timeStamp + "\n";
            byte[] iCBytes = initialCreation.getBytes(charsetUTF8);
            initLog.write(iCBytes);
            initLog.close();

        } catch(Exception e) {
            //System.out.println("Could not create server log file!");
            return false;
        }
        finally{
        	try{
        		if(initLog != null) initLog.close();
        	}
        	catch(IOException io){
        		return false;
        	}
        }
        return true;
    }

    /********************************************************/
    /***********               Users               **********/
    /********************************************************/

    /** Purpose: Adds a new user to be managed by the server.
     *  Input: A unique user id: uid, and their password as a string
     *  Output: A new user folder with name uid: root/users/uid/
     *          Writes that the user was created to the user and server log.
     *          An empty user data, IV file, and the hashed and salted user password
     *  Return: Boolean, true if creation was successful.
     * */
    public static synchronized boolean newUser(String uid, String uPWD) {
        Path newUser = Paths.get(usersPath + uid + "/");

        if(uid == null || uPWD == null || uid.equals("") || uPWD.equals("")) {
            return false;
        }

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
            boolean recordOnServerLog = writeServerLog(userInit);
            if(!initUserLog || !recordOnServerLog) {
                return false;
            }
        } catch(Exception e) {
            //System.out.println("Could not create new user: " + uid);
            return false;
        }

        // create password file, create user data file, create user iv file
        return storeUserPWD(uid, uPWD) || writeUserData(uid, null) || writeUserIV(uid, null);

    }

    /** Purpose: Searches the database for a user specified by uid.
     *  Input: A user id, uid.
     *  Output: None.
     *  Return: True if uid found, false otherwise.
     * */
    public static synchronized boolean searchUser(String uid) {
        Path user = Paths.get(usersPath + uid + "/");
        return Files.exists(user);
    }

    /** Purpose: Removes a user managed by the server.
     *  Input: A unique user id: uid.
     *  Output: Removal of a user folder with name uid: root/users/uid/
     *          Removes all files stored inside that folder.
     *  Return: Boolean, true if deletion was successful.
     * */
    public static synchronized boolean deleteUser(String uid) {
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

        writeServerLog("User " + uid + " deleted"); // server
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
    public static synchronized boolean writeServerLog(String dataToLog) {
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
        OutputStreamWriter appendLog = null;
        BufferedWriter bw = null;

        try {
            appendLog = new OutputStreamWriter(new FileOutputStream(sLogPath, true), "UTF-8");
            bw = new BufferedWriter(appendLog);

            String timeStamp = new SimpleDateFormat("yyyy/MM/dd_HH:mm:ss").format(Calendar.getInstance().getTime());
            bw.write("Time Written: " + timeStamp + " | Entry: " + dataToLog);
            bw.newLine();
            bw.flush();
            bw.close();
        } catch(Exception e) {
            //System.out.println("Could not append data to log!");
            return false;
        } finally{
        	try{
        		if(appendLog != null) appendLog.close();
        		if(bw != null) bw.close();
        	}
        	catch(IOException io){
        		return false;
        	}
        }
        return true;

    }

    /** Purpose: Reads the server log.
     *  Input: None.
     *  Output: None.
     *  Return: String of the server log data.
     * */
    public static synchronized String readServerLog() {
        Path logPath = Paths.get(serverLogPath + "log.txt");
        if(!Files.exists(logPath)) {
            return null;
        }

        byte[] fileArray;
        try {
            fileArray = Files.readAllBytes(logPath);
        } catch (Exception e) {
            //System.out.println("Could not read User Data");
            return null;
        }

        boolean didUpdate = writeServerLog("Log read");
        if(!didUpdate) {
            return null;
        }
        return new String(fileArray, charsetUTF8);

    }

    /********************************************************/
    /***********          Read User Files          **********/
    /********************************************************/

    /** Purpose: Reads the log from a specific user.
     *  Input: User id uid of user who's data is needed, fileType of which file to read.
     *  Output: None.
     *  Return: The data of the file (As a string).
     * */
    public static synchronized String readUserLog(String uid) {
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

            boolean didUpdateServerLog = writeServerLog("Log read for user " + uid);
            boolean didUpdateUserLog = writeUserLog(uid, "Log read");
            if(!didUpdateServerLog || !didUpdateUserLog) {
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
    public static synchronized byte[] readUserData(String uid) {
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

            boolean didUpdateServerLog = writeServerLog("User data read for user " + uid);
            boolean didUpdateUserLog = writeUserLog(uid, "Data read");
            if(!didUpdateServerLog || !didUpdateUserLog) {
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
    private static synchronized byte[] readUserPWD(String uid) {
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

            boolean didUpdateServerLog = writeServerLog("Pwd read for user " + uid);
            boolean didUpdateUserLog = writeUserLog(uid, "pwd read");
            if(!didUpdateServerLog || !didUpdateUserLog) {
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
    public static synchronized byte[] readUserIV(String uid) {
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

            boolean didUpdateServerLog = writeServerLog("IV read for user " + uid);
            boolean didUpdateUserLog = writeUserLog(uid, "IV read");
            if(!didUpdateServerLog || !didUpdateUserLog) {
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
    public static synchronized boolean writeUserLog(String uid, String fileData) {
        if(!searchUser(uid)) {
            return false;
        }

        String filePathName = usersPath + uid + "/" + uid + userLogSuffix;
        String dataToWrite;
        BufferedWriter bw = null;
        OutputStreamWriter writer = null;
        try {
            writer = new OutputStreamWriter(new FileOutputStream(filePathName, true), "UTF-8");
            bw = new BufferedWriter(writer);

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
        finally{
        	try{
        		if(writer != null) writer.close();
        		if(bw != null)bw.close();
        	}
        	catch(IOException io){
        		return false;
        	}
        }
        return true;

    }

    /** Purpose: Writes the user data for a specific user.
     *  Input: User id uid of user to be update and the fileData to be written.
     *  Output: Updated (Overwritten) version of the user data in the specified uid folder.
     *  Return: Boolean, true if operation successful.
     * */
    public static synchronized boolean writeUserData(String uid, byte[] fileData) {
        if (!searchUser(uid)) {
            return false;
        }
        FileOutputStream fileOut = null;
        String filePath = usersPath + uid + "/" + uid + userDataSuffix;
        try {
            fileOut = new FileOutputStream(filePath);

            fileOut.write(fileData);
            fileOut.close();
        } catch (Exception e) {
            return false;
        }
        finally{
        	try{
        		if(fileOut != null) fileOut.close();
        	}
        	catch(IOException io){
        		return false;
        	}
        }

        boolean didUpdateServerLog = writeServerLog("User data written for user " + uid);
        boolean didUpdateUserLog = writeUserLog(uid, "Data written");

        return didUpdateServerLog && didUpdateUserLog;

    }

    /** Purpose: Writes the password file for a specific user.
     *  Input: User id uid of user to be update and the fileData to be written.
     *  Output: Updated (Overwritten) version of the password file in the specified uid folder.
     *  Return: Boolean, true if operation successful.
     * */
    private static synchronized boolean writeUserPWD(String uid, byte[] fileData) {
        if (!searchUser(uid)) {
            return false;
        }


        String filePath = usersPath + uid + "/" + uid + userPWDSuffix;
        FileOutputStream fileOut = null;
        try {
            fileOut = new FileOutputStream(filePath);

            fileOut.write(fileData);
            fileOut.close();
        } catch (Exception e) {
            return false;
        }
        finally{
        	try{
        		if(fileOut != null) fileOut.close();
        	}
        	catch(IOException io){
        		return false;
        	}
        }

        boolean didUpdateServerLog = writeServerLog("pwd written for user " + uid);
        boolean didUpdateUserLog = writeUserLog(uid, "pwd written");

        return didUpdateServerLog && didUpdateUserLog;

    }

    /** Purpose: Writes the IV file for a specific user.
     *  Input: User id uid of user to be update and the fileData to be written.
     *  Output: Updated (Overwritten) version of the IV file in the specified uid folder.
     *  Return: Boolean, true if operation successful.
     * */
    public static synchronized boolean writeUserIV(String uid, byte[] fileData) {
        if (!searchUser(uid)) {
            return false;
        }

        String filePath = usersPath + uid + "/" + uid + userIVSuffix;
        FileOutputStream fileOut = null;
        try {
            fileOut = new FileOutputStream(filePath);

            fileOut.write(fileData);
            fileOut.close();
        } catch (Exception e) {
            return false;
        }
        finally{
        	try{
        		if(fileOut != null) fileOut.close();
        	}
        	catch(IOException io){
        		return false;
        	}
        }

        boolean didUpdateServerLog = writeServerLog("IV written for user " + uid);
        boolean didUpdateUserLog = writeUserLog(uid, "IV written");

        return didUpdateServerLog && didUpdateUserLog;

    }

    /********************************************************/
    /***********  Secure Password Authentication   **********/
    /********************************************************/

    /** Purpose: Get the next 4 bytes of salt for passwords.
     *  Input: None.
     *  Output: None.
     *  Return: 4 bytes of salt, generated with secureRandom.
     * */
    public static synchronized byte[] getNextSalt() {
        byte[] salt = new byte[saltLength];
        sRANDOM.nextBytes(salt);
        return salt;
    }

    /** Purpose: Hash a given password with a given salt
     *  Input: password as a string, salt as a byte[]
     *  Output: None.
     *  Return: The hashed password as a byte[]. Returns null upon failure
     * */
    public static synchronized byte[] hashPwd(String pwd, byte[] salt) throws AssertionError {
        if(pwd == null || pwd.length() == 0) {
            return null;
        }

        char[] password = pwd.toCharArray();

        PBEKeySpec spec = new PBEKeySpec(password, salt, iterations, desiredKeyLen);
        try {
            SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
            return skf.generateSecret(spec).getEncoded();

        } catch (Exception e) {
            throw new AssertionError("Error while hashing a password: " + e.getMessage(), e);
        } finally {
            spec.clearPassword();
        }

    }

    /** Purpose: Store a specific users password *** Should overwrite old password
     *  Input: user id and password as a string
     *  Output: Updated user PWD.txt file
     *  Return: True if successful, false otherwise.
     * */
    public static synchronized boolean storeUserPWD(String uid, String pwd) {
        if(!searchUser(uid)) {
            return false;
        }

        byte[] salt = getNextSalt(); // create salt
        byte[] hashedPWD = hashPwd(pwd, salt); // hash pwd

        if(hashedPWD == null) {
            return false;
        }

        byte[] storeThis = new byte[salt.length + hashedPWD.length];
        System.arraycopy(salt, 0, storeThis, 0, salt.length);
        System.arraycopy(hashedPWD, 0, storeThis, salt.length, hashedPWD.length);

        return writeUserPWD(uid, storeThis);

    }

    /** Purpose: Authenticate a user based on a given password
     *  Input: user id and  given password as a string
     *  Output: None.
     *  Return: True if given PWD matches stored PWD.
     * */
    public static synchronized boolean checkUserPWD(String uid, String givenPWD) {
        if(!searchUser(uid)) {
            return false;
        }

        // load stored
        byte[] storedBytes = readUserPWD(uid); // DB method
        if(storedBytes == null) {
            return false;
        }

        byte[] storedSalt = new byte[saltLength];
        System.arraycopy(storedBytes, 0, storedSalt, 0, saltLength); // length of salt

        byte[] storedHash = new byte[storedBytes.length - saltLength];
        System.arraycopy(storedBytes, saltLength, storedHash, 0, storedHash.length);

        byte[] givenHash = hashPwd(givenPWD, storedSalt);
        if(givenHash == null || givenHash.length != storedHash.length) {
            return false;
        }

        for(int i = 0; i < givenHash.length; i++) {
            if(givenHash[i] != storedHash[i]) {
                writeServerLog("pwd authentication failed for user " + uid);
                writeUserLog(uid, "pwd authentication failed");
                return false;
            }
        }

        boolean didUpdateServerLog = writeServerLog("pwd authentication success for user " + uid);
        boolean didUpdateUserLog = writeUserLog(uid, "pwd authentication success");

        return didUpdateServerLog && didUpdateUserLog;
    }




} // END CLASS
