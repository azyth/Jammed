package junitTests;

import jammed.*;
import org.junit.Test;
import java.nio.charset.Charset;

/*
 * Created by Marcos on 3/19/15.
 */
public class DBTester {

    private final Charset charsetUTF8 = Charset.forName("UTF-8");

        @Test
        public void testDB() {

            System.out.println("Initializing...");
            boolean initRes = DB.initialize();
            System.out.println("Initialized!");
            String u = "test001";
            String p = "pass123";
            String j = "wrongPWD";

            System.out.println("New user " + u + " being added...");
            boolean initUserRes = DB.newUser(u, p);
            System.out.println("User " + u + " added! Storing pwd " + p);
            boolean pwdStoredRes = DB.storeUserPWD(u, p);
            System.out.println("pwd " + p + " stored!");

            System.out.println("Authenticating user " + u + " with pwd " + p);
            boolean authenticateWithCorrectPWD = DB.checkUserPWD(u, p);
            System.out.println("Result: " + authenticateWithCorrectPWD);

            System.out.println("Authenticating user " + u + " with pwd " + j);
            boolean authenticateWithWrongPWD = DB.checkUserPWD(u, j);
            System.out.println("Result: " + authenticateWithWrongPWD);

            System.out.println("Authenticating user " + u + " with pwd " + p);
            boolean authenticateWithCorrectPWDAgain = DB.checkUserPWD(u, p);
            System.out.println("Result: " + authenticateWithCorrectPWDAgain);

            System.out.println("User " + u + " added! Storing pwd " + j);
            pwdStoredRes = DB.storeUserPWD(u, j);
            System.out.println("pwd " + j + " stored!");

            System.out.println("Authenticating user " + u + " with pwd " + p);
            authenticateWithCorrectPWD = DB.checkUserPWD(u, p);
            System.out.println("Result: " + authenticateWithCorrectPWD);

            System.out.println("Authenticating user " + u + " with pwd " + j);
            authenticateWithWrongPWD = DB.checkUserPWD(u, j);
            System.out.println("Result: " + authenticateWithWrongPWD);

            System.out.println("User " + u + " added! Storing pwd " + p);
            pwdStoredRes = DB.storeUserPWD(u, p);
            System.out.println("pwd " + p + " stored!");

            System.out.println("Authenticating user " + u + " with pwd " + p);
            authenticateWithCorrectPWD = DB.checkUserPWD(u, p);
            System.out.println("Result: " + authenticateWithCorrectPWD);

            DB.readUserLog(u);
            DB.readUserData(u);
            DB.readUserIV(u);
            DB.deleteUser(u);
            DB.readServerLog();

            /* Note: Reading of Data written to log cannot be tested due to time stamps and formatting */

            /* Init DB */
            /*assertEquals(true, DB.initialize());

            // User creation and searching
            // make test user and search for it
            assertEquals(true, DB.newUser("test001"));
            assertEquals(true, DB.searchUser("test001"));
            // search for user that does not exist, make it, then search again
            assertEquals(false, DB.searchUser("test002"));
            assertEquals(true, DB.newUser("test002"));
            assertEquals(true, DB.searchUser("test002"));

            // create and delete user
            assertEquals(false, DB.searchUser("test003"));
            assertEquals(true, DB.newUser("test003"));
            assertEquals(true, DB.searchUser("test003"));
            assertEquals(true, DB.deleteUser("test003"));
            assertEquals(false, DB.searchUser("test003"));

            // try creating existing user
            assertEquals(true, DB.newUser("test002"));

            // Writing and reading user files
            String user001UserData = "username: mvp34, password: 123";
            String user001Login = "username: example, password: exampleP, salt: 123";
            String user001IV = "123456789";
            String user001Log = "Password Updated";
                // user 2
            String user002UserData = "username: 34pvm, password: 321";
            String user002Login = "username: steve, password: jobs, salt: 345";
            String user002IV = "987654321";
            String user002Log = "Logged in";

            // Writing to user 1
            assertEquals(true, DB.writeUserData("test001", user001UserData.getBytes()));
            assertEquals(true, DB.writeUserPWD("test001", user001Login.getBytes()));
            assertEquals(true, DB.writeUserIV("test001", user001IV.getBytes()));
            assertEquals(true, DB.writeUserLog("test001", user001Log));
            // Writing to user 2
            assertEquals(true, DB.writeUserData("test002", user002UserData.getBytes()));
            assertEquals(true, DB.writeUserPWD("test002", user002Login.getBytes()));
            assertEquals(true, DB.writeUserIV("test002", user002IV.getBytes()));
            assertEquals(true, DB.writeUserLog("test002", user002Log));
            // Try writing to user 3, should be false
            assertEquals(false, DB.writeUserData("test003", user002UserData.getBytes()));
            assertEquals(false, DB.writeUserPWD("test003", user002Login.getBytes()));
            assertEquals(false, DB.writeUserIV("test003", user002IV.getBytes()));
            assertEquals(false, DB.writeUserLog("test003", user002Log));

            // Read user data (1)
            if(DB.readUserData("test001") != null) {
                String readUser001Data = new String(DB.readUserData("test001"), charsetUTF8);
                System.out.println(readUser001Data);
            }

            if(DB.readUserPWD("test001") != null) {
                String readUser001PWD = new String(DB.readUserPWD("test001"), charsetUTF8);
                System.out.println(readUser001PWD);
            }

            if(DB.readUserIV("test002") != null) {
                String readUser002IV = new String(DB.readUserIV("test002"), charsetUTF8);
                System.out.println(readUser002IV);
            }

            if(DB.readUserLog("test002") != null) {
                String readUser002Log = DB.readUserLog("test002");
                System.out.println(readUser002Log);
            }

            // Server log stuff
            // Update the server log
            String dataToLog = "Hello world";
            assertEquals(DB.writeLog(dataToLog), true);
            // read "Hello World"
            String serverLogRead = DB.readServerLog();
            try {
                assertEquals("".equals(serverLogRead), false);
            } catch(Exception e) {
                assertEquals(true, true); // null pointer exception expected.
            }
            //assertEquals(serverLogRead.equals(dataToLog), true);

            // write a lot to the log
            for(int i = 0; i < 10000; i++) {
                String msg = "This is test #" + i;
                assertEquals(DB.writeLog(msg), true);
            }

            // Just to finish off, I have to get this right at least...
            assertEquals(true, true); */
        }


} // END TESTS
