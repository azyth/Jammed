package junitTests;

import jammed.*;
import org.junit.Test;
import static junit.framework.Assert.assertEquals;
import java.nio.charset.Charset;

/**
 * Created by Marcos on 3/19/15.
 */
public class DBTester {

    private final Charset charsetUTF8 = Charset.forName("UTF-8");

        @Test
        public void testDB() {
            /* Note: Reading of Data written to log has to be done manually due to time stamps and formatting */

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
            assertEquals(DB.writeEncodedFile("test001", DB.DBFileTypes.USER_DATA, user001UserData.getBytes(charsetUTF8)), true); // ud
            assertEquals(DB.writeEncodedFile("test001", DB.DBFileTypes.USER_PWD_FILE, user001Login.getBytes(charsetUTF8)), true); // login
            assertEquals(DB.writeEncodedFile("test001", DB.DBFileTypes.USER_IV, user001IV.getBytes(charsetUTF8)), true);
            assertEquals(DB.writeUserLog("test001", DB.DBFileTypes.USER_LOG, user001Log), true); // ulog
            // Writing to user 2
            assertEquals(DB.writeEncodedFile("test002", DB.DBFileTypes.USER_DATA, user002UserData.getBytes(charsetUTF8)), true); // ud
            assertEquals(DB.writeEncodedFile("test002", DB.DBFileTypes.USER_PWD_FILE, user002Login.getBytes(charsetUTF8)), true); // login
            assertEquals(DB.writeEncodedFile("test002", DB.DBFileTypes.USER_IV, user002IV.getBytes(charsetUTF8)), true);
            assertEquals(DB.writeUserLog("test002", DB.DBFileTypes.USER_LOG, user002Log), true); // ulog
            // Try writing to user 3, should be false
            assertEquals(DB.writeEncodedFile("test003", DB.DBFileTypes.USER_DATA, user001UserData.getBytes(charsetUTF8)), false); // ud
            assertEquals(DB.writeEncodedFile("test003", DB.DBFileTypes.USER_PWD_FILE, user001Login.getBytes(charsetUTF8)), false); // login
            assertEquals(DB.writeEncodedFile("test003", DB.DBFileTypes.USER_IV, user001IV.getBytes(charsetUTF8)), false);
            assertEquals(DB.writeUserLog("test003", DB.DBFileTypes.USER_LOG, user001Log), false); // ulog

            // Read user data (1)
            String user001udRead = new String(DB.readEncodedFile("test001", DB.DBFileTypes.USER_DATA), charsetUTF8);
            String user001LoginRead = new String(DB.readEncodedFile("test001", DB.DBFileTypes.USER_PWD_FILE), charsetUTF8);
            String user001IVRead = new String(DB.readEncodedFile("test001", DB.DBFileTypes.USER_IV), charsetUTF8);
            //String user001LogRead = DB.readUserLog("test001", DB.DBFileTypes.USER_LOG);
                // compare
            assertEquals(user001udRead.equals(user001UserData), true);
            assertEquals(user001Login.equals(user001LoginRead), true);
            assertEquals(user001IV.equals(user001IVRead), true);
            //assertEquals(user001Log.equals(user001LogRead), true);
            // Read user data (2)
            String user002udRead = new String(DB.readEncodedFile("test002", DB.DBFileTypes.USER_DATA), charsetUTF8);
            String user002LoginRead = new String(DB.readEncodedFile("test002", DB.DBFileTypes.USER_PWD_FILE), charsetUTF8);
            String user002IVRead = new String(DB.readEncodedFile("test002", DB.DBFileTypes.USER_IV), charsetUTF8);
            //String user002LogRead = DB.readUserLog("test002", DB.DBFileTypes.USER_LOG);
                // compare
            assertEquals(user002udRead.equals(user002UserData), true);
            assertEquals(user002LoginRead.equals(user002Login), true);
            assertEquals(user002IVRead.equals(user002IV), true);
            //assertEquals(user002LogRead.equals(user002Log), true);
            // Read user data (3) Doesn't exist
            try {
                String user003udRead = new String(DB.readEncodedFile("test003", DB.DBFileTypes.USER_DATA), charsetUTF8);
                String user003LoginRead = new String(DB.readEncodedFile("test003", DB.DBFileTypes.USER_PWD_FILE), charsetUTF8);
                String user003IVRead = new String(DB.readEncodedFile("test003", DB.DBFileTypes.USER_IV), charsetUTF8);
                //String user003LogRead = DB.readUserLog("test002", DB.DBFileTypes.USER_LOG);
                // compare, should all be false
                assertEquals(user003udRead.equals(user002UserData), false);
                assertEquals(user003LoginRead.equals(user002Login), false);
                assertEquals(user003IVRead.equals(user002IV), false);
                //assertEquals(user003LogRead.equals(user002Log), false);
            } catch(Exception e) {
                assertEquals(true, true); // This is good, should arrive here
                // null pointer exception expected.
            }

            /* Server log stuff */
            // Update the server log
            String dataToLog = "Hello world";
            assertEquals(DB.writeLog(dataToLog), true);
            // read "Hello World"
            //String serverLogRead = DB.readLog();
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
