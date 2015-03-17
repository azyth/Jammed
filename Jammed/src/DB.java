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
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class DB {

    private static final String serverPath = "root/server/";
    private static final String usersPath = "root/users/";

    /* Testing */
    public static void main(String[] args) {
        boolean res = initialize();
        boolean mkUsr = newUser("test001");
        boolean srchUsr = searchUser("test001");
        System.out.println(srchUsr);

        String dataToLog = "Hello world";
        boolean didWriteLog = writeLog(dataToLog);
        System.out.println(didWriteLog);

        /*byte[] fileData = readFile("test001", Constants.USER_DATA);
        System.out.println(fileData); */
    }

    /** Method Description here */
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
                String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());
                String initialCreation = "Database initialized on " + timeStamp;
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

    /** Method Description here */
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

    /** Method Description here */
    public static boolean searchUser(String uid) {
        Path user = Paths.get(usersPath + uid + "/");
        return Files.exists(user);
    }

    /** Method Description here */
    public static boolean writeLog(String dataToLog) {
        Path lgname = Paths.get(serverPath + "log.txt");
        File log = new File(serverPath + "log.txt");
        if(!Files.exists(lgname)) {
            try {
                Files.createFile(lgname);
            } catch(Exception e) {
                System.out.println("Could not create log.");
                return false;
            }
        }


        try {
            FileWriter appendLog = new FileWriter(log.getName(), true);
            BufferedWriter bw = new BufferedWriter(appendLog);
            try {
                bw.write(dataToLog);
            } finally {
                bw.close();
            }
        } catch(Exception e) {
            System.out.println("Could not append data to log!");
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

        Path fileToRead = Paths.get(usersPath + uid + "/" + uid + fname);
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

        Path fileToRead = Paths.get(usersPath + uid + "/" + uid + fname);
        if(Files.exists(fileToRead)) {
            return false;
        }
        return false;
    }






} // END CLASS
