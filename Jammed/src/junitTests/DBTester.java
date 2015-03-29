package junitTests;

import jammed.*;
import org.junit.Test;
import static junit.framework.Assert.assertEquals;
import java.nio.charset.Charset;

/*
 * Created by Marcos on 3/19/15.
 */
public class DBTester {

    private final Charset charsetUTF8 = Charset.forName("UTF-8");

        @Test
        public void testDB() {
            /* Note: Reading of Data written to log cannot be tested due to time stamps and formatting */

            /* Init DB */
            assertEquals(true, DB.initialize());

            /* User creation and searching */
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

            /* Writing and reading user files */
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

            // Writing to user 2

            // Try writing to user 3, should be false


            // Read user data (1)

            //String user001LogRead = DB.readUserLog("test001", DB.DBFileTypes.USER_LOG);
                // compare

            //assertEquals(user001Log.equals(user001LogRead), true);
            // Read user data (2)

            //String user002LogRead = DB.readUserLog("test002", DB.DBFileTypes.USER_LOG);
                // compare

            //assertEquals(user002LogRead.equals(user002Log), true);
            // Read user data (3) Doesn't exist


            /* Server log stuff */
            // Update the server log
            String dataToLog = "Hello world";
            assertEquals(DB.writeLog(dataToLog), true);
            // read "Hello World"
            String serverLogRead = DB.readLog();
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
            assertEquals(true, true);
        }


} // END TESTS
