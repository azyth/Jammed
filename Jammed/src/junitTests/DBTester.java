package junitTests;


import org.junit.Test;
import jammed.*;

/**
 * Created by Marcos on 3/19/15.
 */
public class DBTester {

        @Test
        public static void test() {
            // Init db
            boolean res = DB.initialize();
            // make test user and search for it
            boolean mkUsr = DB.newUser("test001");
            boolean srchUsr = DB.searchUser("test001");
            System.out.println(srchUsr);

            // Update the server log
            String dataToLog = "Hello world";
            boolean didWriteLog = DB.writeLog(dataToLog);
            System.out.println(didWriteLog);

            // Stress test the log
            for(int i = 0; i < 1000; i++) {
                String msg = "This is a test, #" + i;
                boolean sTest = DB.writeLog(msg);
            }

            // Write data to a user file
            String samplePassword = "username: mvp34, password: 123";
            String passwordUpdated = "Password Updated";
            String login = "username: example, password: exampleP, salt: 123";

            boolean udRes = DB.writeEncodedFile("test001", DB.DBFileTypes.USER_DATA, samplePassword.getBytes());
            boolean pwdRes = DB.writeEncodedFile("test001", DB.DBFileTypes.USER_PWD_FILE, login.getBytes());
            boolean logRes = DB.writeUserLog("test001", DB.DBFileTypes.USER_LOG, passwordUpdated);

            // read that data
            byte[] userData = DB.readEncodedFile("test001", DB.DBFileTypes.USER_DATA);
            byte[] userLogin = DB.readEncodedFile("test001", DB.DBFileTypes.USER_PWD_FILE);
            String userLog = DB.readUserLog("test001", DB.DBFileTypes.USER_LOG);

            System.out.println("User data: " + userData.toString());
            System.out.println("User login: " + userLogin.toString());
            System.out.println("User Log: " + userLog);
        }


} // END TESTS
