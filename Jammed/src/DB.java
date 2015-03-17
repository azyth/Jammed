/* Marcos Pedreiro - mvp34
   3/16/15
   Class to perform file management for the server

   NOTES:
    1) As of 3/16/15, interface is not final.
    2) Constants are used to read specific types of files.
 */
import static java.nio.file.StandardOpenOption.*;
import java.nio.file.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class DB {

    /* Testing */
    public static void main(String[] args) {
        boolean res = initialize();
        boolean mkUsr = newUser("test001");
        boolean srchUsr = searchUser("test001");
        System.out.println(srchUsr);

        String dataToLog = "Hello world";
        boolean didWriteLog = writeLog(dataToLog);
        System.out.println(didWriteLog);

        byte[] fileData = readFile("test001", Constants.USER_DATA);
        System.out.println(fileData);
    }

    /** Method Description here */
    public static boolean initialize() {
        Path server = Paths.get("root/server/");
        Path user = Paths.get("root/users/");

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

        System.out.println("Successfully made dirs");
        return true;
    }

    /** Method Description here */
    public static boolean newUser(String uid) {
        Path newUser = Paths.get("root/users/" + uid + "/");

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

    /** Method Description here */
    public static boolean searchUser(String uid) {
        Path user = Paths.get("root/users/" + uid + "/");
        return Files.exists(user);
    }

    /** Method Description here */
    public static boolean writeLog(String dataToLog) {
        Path lgname = Paths.get("root/server/log.txt");
        if(!Files.exists(lgname)) {
            try {
                Files.createFile(lgname);
            } catch(Exception e) {
                System.out.println("Could not create log.");
                return false;
            }
        }

        // Convert the string to a byte array.
        byte data[] = dataToLog.getBytes();
        try {
            OutputStream out = new BufferedOutputStream(Files.newOutputStream(lgname, CREATE, APPEND));
            out.write(data, 0, data.length);
        } catch (IOException x) {
            System.out.println("Could not write data to log.");
            return false;
        }
        return true;

    }

    /** Method Description here */
    public static byte[] readFile(String uid, double fileType) {
        // To do
        String fname;
        if(fileType == Constants.USER_DATA) {
            fname = "USERDATA.bin";
        } else if(fileType == Constants.USER_PWD_FILE) {
            fname = "PWD.bin";
        } else if(fileType == Constants.USER_LOG) {
            fname = "LOG.bin";
        } else {
            // Not a valid file, do nothing
            return null;
        }

        Path fileToRead = Paths.get("root/users/" + uid + "/" + uid + fname);
        if(Files.exists(fileToRead)) {
            byte[] fileArray;
            try {
                fileArray = Files.readAllBytes(fileToRead);
            } catch (Exception e) {
                System.out.println("Could not read User Data");
                return null;
            }
            return fileArray;
        }

        return null;
    }

    /** Method Description here */
    public static boolean writeFile(String uid, double fileType, File fileData) {
        // To do
        String fname;
        if(fileType == Constants.USER_DATA) {
            fname = "USERDATA.bin";
        } else if(fileType == Constants.USER_PWD_FILE) {
            fname = "PWD.bin";
        } else if(fileType == Constants.USER_LOG) {
            fname = "LOG.bin";
        } else {
            // Not a valid file, do nothing
            return false;
        }

        Path fileToRead = Paths.get("root/users/" + uid + "/" + uid + fname);
        if(Files.exists(fileToRead)) {
            return false;
        }
        return false;
    }






} // END CLASS
