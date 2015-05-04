package jammed;

/*
 * Created by Marcos on 4/27/15.
 * Class for the main gui of the client
 * This is where their data is displayed and can be edited
 */

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;

public class MainGUI extends JFrame {

    private boolean changesMade = false;
    public String CurrentUser;

    private JLabel serverInfoLabel;
    private JTextArea userDataDisplay;
    private JTextField usernameTF, passwordTF, serviceTF;
    private ArrayList<LoginInfo> userDataArray;
    private boolean showPasswords = false;

    private ActionClass action = new ActionClass();

    public MainGUI(ArrayList<LoginInfo> ud) {
        super("Jammed"); setBounds(300, 100, 800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Container con = getContentPane(); // inherit main frame
        con.setBackground(Color.WHITE);
        con.setLayout(null);
        userDataArray = ud;


        action.type = GActionType.NULL;
        /* Display File */
        userDataDisplay = new JTextArea();
        userDataDisplay.setSize(400, 450); userDataDisplay.setLocation(30, 30); userDataDisplay.setBackground(Color.LIGHT_GRAY);
        userDataDisplay.setEditable(false);
        userDataDisplay.append("Website\tUsername\tPassword\n__________\t__________\t__________\n");
        displayUserData(userDataArray); // display info
        JScrollPane udScroller = new JScrollPane(userDataDisplay);
        udScroller.setBounds(30,30, 400, 450);
        /* End Display File */
        //////////////////////////////////////////////////////////////////////////////////
        JLabel ServiceLabel = new JLabel("Website: ");
        ServiceLabel.setSize(300, 30); ServiceLabel.setLocation(440, 20);
        JLabel usernameLabel = new JLabel("Username: ");
        usernameLabel.setSize(300, 30); usernameLabel.setLocation(440, 50);
        JLabel passwordLabel = new JLabel("Password: ");
        passwordLabel.setSize(300, 30); passwordLabel.setLocation(440, 80);

        serverInfoLabel = new JLabel(" ");
        serverInfoLabel.setSize(300, 30); serverInfoLabel.setLocation(440, 230);

        serviceTF = new JTextField(10);
        serviceTF.setBounds(510, 20, 150, 30);
        usernameTF = new JTextField(10);
        usernameTF.setBounds(510, 50, 150, 30);
        passwordTF = new JPasswordField(10);
        passwordTF.setBounds(510, 80, 150, 30);
        ////////////////////////////////////////////////////////////////////////
        JButton addDataB = new JButton("Add Data"); addDataB.setBounds(440, 120, 100, 50);
        addDataB.addActionListener(new AddDataButtonHandler());

        JButton deleteDataB = new JButton("Delete Data"); deleteDataB.setBounds(550, 120, 100, 50);
        deleteDataB.addActionListener(new DeleteDataButtonHandler());

        JButton chngDataB = new JButton("Change Entry"); chngDataB.setBounds(440, 180, 100, 50);
        chngDataB.addActionListener(new AddDataButtonHandler());

        JButton showPWDB = new JButton("Show Passwords"); showPWDB.setBounds(550, 180, 150, 50);
        showPWDB.addActionListener(new ShowPWDButtonHandler());

        JButton exitB = new JButton("Exit"); exitB.setBounds(700, 500, 70, 50);
        exitB.addActionListener(new ExitButtonHandler());

        JButton changePWDB = new JButton("Change Password"); changePWDB.setBounds(545, 500, 150, 50);
        changePWDB.addActionListener(new ChangePWDButtonHandler());

        JButton saveButton = new JButton("Save Changes");
        saveButton.setBounds(30, 500, 100, 50);
        saveButton.addActionListener(new SaveButtonHandler());

        JButton getLogButton = new JButton("Get Log");
        getLogButton.setBounds(135, 500, 100, 50);
        getLogButton.addActionListener(new GetLogButtonHandler());

        // Display items
        con.add(udScroller); // Text Area
        con.add(usernameLabel); // Labels
        con.add(usernameTF);
        con.add(passwordLabel);
        con.add(passwordTF);
        con.add(ServiceLabel);
        con.add(serviceTF);
        con.add(serverInfoLabel);
        con.add(saveButton); // Buttons
        con.add(getLogButton);
        con.add(addDataB);
        con.add(deleteDataB);
        con.add(exitB);
        con.add(changePWDB);
        con.add(chngDataB);
        con.add(showPWDB);
        // Display all
        setVisible(true);
    }

    /** Class to save changes */
    private class SaveButtonHandler implements ActionListener {
        public void actionPerformed(ActionEvent event) {
            if(changesMade) {
                synchronized (action) {
                    action.userData = userDataArray;
                    action.type = GActionType.SAVE;
                    action.notify();
                }
            }
            changesMade = false;
        }
    }

    /** Class to add entries */
    private class AddDataButtonHandler implements ActionListener {
        public void actionPerformed(ActionEvent event) {
            boolean fieldsAreValid = !(usernameTF.getText().isEmpty() ||
                    passwordTF.getText().isEmpty() || serviceTF.getText().isEmpty());

            if(fieldsAreValid) {
                LoginInfo chng = new LoginInfo();
                chng.website = serviceTF.getText();
                chng.username = usernameTF.getText();
                chng.password = passwordTF.getText();
                // check if the added thing is already in the data or not...
                int index = userDataArray.indexOf(chng);

                if (index == -1) {
                    userDataArray.add(chng);
                    Collections.sort(userDataArray);
                } else {
                    userDataArray.set(index, chng);
                }
                changesMade = true;
                refreshDisplay();
            }
        }
    }

    /** Class to handle deletion of entries
     *  Note: Users can do stupid things like delete only the website portion
     *  of their entry, they will have to delete the whole entry and reenter it.
     * */
    private class DeleteDataButtonHandler implements ActionListener {
        public void actionPerformed(ActionEvent event) {
            boolean fieldsAreValid = !(usernameTF.getText().isEmpty() || serviceTF.getText().isEmpty());

            if(fieldsAreValid) {
                LoginInfo chng = new LoginInfo();
                chng.website = serviceTF.getText();
                chng.username = usernameTF.getText();
                int index = userDataArray.indexOf(chng);

                if (index != -1) {
                    userDataArray.remove(chng);
                    Collections.sort(userDataArray);
                }
                refreshDisplay();
                changesMade = true;
            }
        }
    }

    /** Class to handle a password change */
    private class ChangePWDButtonHandler implements ActionListener {
        private JTextField newpwdTF, confirmpwdTF;
        private JFrame newWindow;
        private JLabel pwdChangeInfo = new JLabel(" ");

        public void actionPerformed(ActionEvent event) {
            newWindow = new JFrame("Change Password");
            newWindow.setBounds(500, 250, 400, 225);

            Container con2 = newWindow.getContentPane(); // inherit main frame
            con2.setBackground(Color.WHITE);
            con2.setLayout(null);

            JLabel newpwdLabel = new JLabel("New Password: ");
            newpwdLabel.setSize(100, 30); newpwdLabel.setLocation(10, 10);
            JLabel confirmpwdLabel = new JLabel("Confirm Password: ");
            confirmpwdLabel.setSize(150, 30); confirmpwdLabel.setLocation(10, 40);

            newpwdTF = new JPasswordField(10);
            newpwdTF.setBounds(140, 10, 150, 30);
            confirmpwdTF = new JPasswordField(10);
            confirmpwdTF.setBounds(140, 40, 150, 30);

            JButton chngpwdB = new JButton("Confirm Change"); chngpwdB.setBounds(10, 80, 150, 50);
            chngpwdB.addActionListener(new ConfirmButtonHandler());
            JButton exitB = new JButton("Close"); exitB.setBounds(170, 80, 100, 50);
            exitB.addActionListener(new ExitCHNGPWDButtonHandler());

            con2.add(newpwdLabel);
            con2.add(confirmpwdLabel);
            con2.add(newpwdTF);
            con2.add(confirmpwdTF);
            con2.add(chngpwdB);
            con2.add(exitB);
            con2.add(pwdChangeInfo);
            newWindow.setVisible(true);
        }

        /** Class to handle what happens when user presses "Confirm Change" Button */
        private class ConfirmButtonHandler implements ActionListener {
            public void actionPerformed(ActionEvent event) {
                String newpwd = newpwdTF.getText();
                String conpwd = confirmpwdTF.getText();

                boolean pwdAreEmpty = newpwd.isEmpty() || conpwd.isEmpty();
                boolean newMatchesCon = newpwd.equals(conpwd);
                String msg = " ";

                if(pwdAreEmpty) { // Make sure the attempted change isn't blatant bs
                    msg = "Cannot have an empty password";
                } else if(!newMatchesCon) {
                    msg = "New password must match confirmation field";
                } else {
                    synchronized (action) {
                        LoginInfo newpwdInfo = new LoginInfo();
                        newpwdInfo.password = conpwd; newpwdInfo.username = CurrentUser; newpwdInfo.website = "changepwd";
                        action.pwdChange = newpwdInfo;
                        action.userData = userDataArray;
                        action.type = GActionType.CHANGE_PWD;
                        action.notify();
                    }

                    msg = "Password changed!";
                }
                pwdChangeInfo.setText(msg); pwdChangeInfo.setForeground(Color.RED);
                pwdChangeInfo.setBounds(10, 140, 400, 30);
            }
        }

        /** Close the password change window */
        private class ExitCHNGPWDButtonHandler implements ActionListener {
            public void actionPerformed(ActionEvent event) {
                newWindow.setVisible(false);
            }
        }
    } // End chngpwd class

    /** Class to exit the program */
    private class ExitButtonHandler implements ActionListener {
        public void actionPerformed(ActionEvent event) {
            synchronized (action) {
                action.userData = userDataArray;
                action.type = GActionType.EXIT;
                action.notify();
            }
            changesMade = false;
        }
    }

    private class GetLogButtonHandler implements ActionListener {
        public void actionPerformed(ActionEvent event) {
            synchronized (action) {
                action.type = GActionType.LOG;
                action.notify();
            }
        }
    }

    private class ShowPWDButtonHandler implements ActionListener {
        public void actionPerformed(ActionEvent event) {
            if(showPasswords) {
                showPasswords = false;
            } else {
                showPasswords = true;
            }
            refreshDisplay();
        }
    }

    public void displayUserData(ArrayList<LoginInfo> ud) {
        for(LoginInfo l : ud) {
            userDataDisplay.append(l.secureToString(showPasswords) + "\n");
        }
    }

    private void initDisplay() {
        userDataDisplay.setText("");
        userDataDisplay.append("Website\tUsername\tPassword\n__________\t__________\t__________\n");
    }

    private void refreshDisplay() {
        initDisplay();
        displayUserData(userDataArray);
    }

    public void setUserDataArray(ArrayList<LoginInfo> ud) {
        userDataArray = ud;
    }
    public ArrayList<LoginInfo> getUserDataArray() {
        return userDataArray;
    }

    public void setServerInfoLabel(String msg) {
        serverInfoLabel.setText(msg);
    }

    public enum GActionType {SAVE, CHANGE_PWD, LOG, EXIT, NULL};

    public class ActionClass {
        ArrayList<LoginInfo> userData;
        LoginInfo pwdChange;
        GActionType type;
    }

    public ActionClass getAction() throws InterruptedException {
        ActionClass copy = new ActionClass();
        synchronized (action) {
            while(action.type == GActionType.NULL) {
                action.wait();
            }
            copy.type = action.type; copy.pwdChange = action.pwdChange;
            copy.userData = action.userData;
            action.userData = null;
            action.pwdChange = null;
        }
        return copy;
    }

    public void resetAction() {
        action.type = GActionType.NULL;
    }

    public static void main(String[] args) {
        ArrayList<LoginInfo> ud = new ArrayList<LoginInfo>();
        new MainGUI(ud);
    }

} // END CLASS

