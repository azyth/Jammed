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
import java.io.*;

public class MainGUI extends JFrame {

    private String fileDataPath = "userData.txt";
    private JTextArea userData;
    private JTextField usernameTF, passwordTF, serviceTF;

    public MainGUI() {
        super("Jammed"); setBounds(300, 100, 800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Container con = getContentPane(); // inherit main frame
        con.setBackground(Color.WHITE);
        con.setLayout(null);

        /* Open File */
        userData = new JTextArea();
        userData.setSize(400, 450); userData.setLocation(30, 30); userData.setBackground(Color.LIGHT_GRAY);
        userData.setEditable(false);
        try {
            Reader rd = new FileReader(fileDataPath);
            userData.read(rd, "Data");
        } catch(Exception e) {
            try {
                PrintWriter newFile = new PrintWriter(fileDataPath);
                String dataHeader = "Username\tPassword\tWebsite";
                String dataHeade2 = "________\t________\t_______";
                newFile.println(dataHeader);
                newFile.println(dataHeade2);
                newFile.close();

                Reader rd = new FileReader(fileDataPath);
                userData.read(rd, "Data");
            } catch(Exception er) { }
        }
        JScrollPane udScroller = new JScrollPane(userData);
        udScroller.setBounds(30,30, 400, 450);
        /* End open file */
        //////////////////////////////////////////////////////////////////////////////////
        JLabel usernameLabel = new JLabel("Username: ");
        usernameLabel.setSize(300, 30); usernameLabel.setLocation(440, 20);
        JLabel passwordLabel = new JLabel("Password: ");
        passwordLabel.setSize(300, 30); passwordLabel.setLocation(440, 50);
        JLabel ServiceLabel = new JLabel("Website: ");
        ServiceLabel.setSize(300, 30); ServiceLabel.setLocation(440, 80);

        usernameTF = new JTextField(10);
        usernameTF.setBounds(510, 20, 150, 30);
        passwordTF = new JPasswordField(10);
        passwordTF.setBounds(510, 50, 150, 30);
        serviceTF = new JTextField(10);
        serviceTF.setBounds(510, 80, 150, 30);
        ////////////////////////////////////////////////////////////////////////
        JButton addDataB = new JButton("Add Data"); addDataB.setBounds(440, 120, 100, 50);
        addDataB.addActionListener(new AddDataButtonHandler());

        JButton deleteDataB = new JButton("Delete Data"); deleteDataB.setBounds(550, 120, 100, 50);
        deleteDataB.addActionListener(new DeleteDataButtonHandler());

        JButton exitB = new JButton("Exit"); exitB.setBounds(700, 500, 70, 50);
        exitB.addActionListener(new ExitButtonHandler());

        JButton changePWD = new JButton("Change Password"); changePWD.setBounds(545, 500, 150, 50);
        changePWD.addActionListener(new ChangePWDButtonHandler());

        JButton saveButton = new JButton("Save Changes");
        saveButton.setBounds(30, 500, 100, 50);
        saveButton.addActionListener(new SaveButtonHandler());

        // Display items
        con.add(udScroller); // Text Area
        con.add(usernameLabel); // Labels
        con.add(usernameTF);
        con.add(passwordLabel);
        con.add(passwordTF);
        con.add(ServiceLabel);
        con.add(serviceTF);
        con.add(saveButton); // Buttons
        con.add(addDataB);
        con.add(deleteDataB);
        con.add(exitB);
        con.add(changePWD);
        // Display all
        setVisible(true);
    }

    private class SaveButtonHandler implements ActionListener {
        public void actionPerformed(ActionEvent event) {
            File file;
            FileWriter out;
            try {
                file = new File(fileDataPath);
                out = new FileWriter(file);
                out.write(userData.getText());
                out.close();
            } catch (Exception e) { }
        }
    }

    private class AddDataButtonHandler implements ActionListener {
        public void actionPerformed(ActionEvent event) {
            String username = usernameTF.getText();
            String password = passwordTF.getText();
            String service = serviceTF.getText();

            if(!(username.isEmpty() || password.isEmpty() || service.isEmpty())) {
                userData.append(username + "\t" + password + "\t" + service + "\n");
            }
        }
    }

    private class DeleteDataButtonHandler implements ActionListener {
        public void actionPerformed(ActionEvent event) {
            String selected = userData.getSelectedText();

            boolean doNotDelete = selected.contains("Username") || selected.contains("Password")
                    || selected.contains("Website") || selected.contains("_");

            if(!doNotDelete) {
                userData.replaceSelection("");
            }
        }
    }

    private class ChangePWDButtonHandler implements ActionListener {
        private JTextField oldpwdTF, newpwdTF, confirmpwdTF;
        private JFrame newWindow;
        private JLabel pwdChangeInfo = new JLabel(" ");

        public void actionPerformed(ActionEvent event) {
            //TODO
            newWindow = new JFrame("Change Password");
            newWindow.setBounds(500, 250, 400, 250);

            Container con2 = newWindow.getContentPane(); // inherit main frame
            con2.setBackground(Color.WHITE);
            con2.setLayout(null);

            JLabel oldpwdLabel = new JLabel("Old Password: ");
            oldpwdLabel.setSize(100, 30); oldpwdLabel.setLocation(10, 10);
            JLabel newpwdLabel = new JLabel("New Password: ");
            newpwdLabel.setSize(100, 30); newpwdLabel.setLocation(10, 40);
            JLabel confirmpwdLabel = new JLabel("Confirm Password: ");
            confirmpwdLabel.setSize(150, 30); confirmpwdLabel.setLocation(10, 70);

            oldpwdTF = new JPasswordField(10);
            oldpwdTF.setBounds(140, 10, 150, 30);
            newpwdTF = new JPasswordField(10);
            newpwdTF.setBounds(140, 40, 150, 30);
            confirmpwdTF = new JPasswordField(10);
            confirmpwdTF.setBounds(140, 70, 150, 30);

            JButton chngpwdB = new JButton("Confirm Change"); chngpwdB.setBounds(120, 120, 150, 50);
            chngpwdB.addActionListener(new ConfirmButtonHandler());
            JButton exitB = new JButton("Cancel"); exitB.setBounds(170, 120, 100, 50);
            exitB.addActionListener(new ExitCHNGPWDButtonHandler());

            con2.add(oldpwdLabel);
            con2.add(newpwdLabel);
            con2.add(confirmpwdLabel);
            con2.add(oldpwdTF);
            con2.add(newpwdTF);
            con2.add(confirmpwdTF);
            con2.add(chngpwdB);
            con2.add(exitB);
            con2.add(pwdChangeInfo);
            newWindow.setVisible(true);
        }

        private class ConfirmButtonHandler implements ActionListener {
            public void actionPerformed(ActionEvent event) {
                String oldpwd = oldpwdTF.getText();
                String newpwd = newpwdTF.getText();
                String conpwd = confirmpwdTF.getText();

                boolean pwdAreEmpty = oldpwd.isEmpty() || newpwd.isEmpty() || conpwd.isEmpty();
                boolean newMatchesCon = newpwd.equals(conpwd);
                boolean oldMatchesnew = oldpwd.equals(newpwd);

                String msg = " ";

                if(pwdAreEmpty) {
                    msg = "Cannot have an empty password";
                } else if(!newMatchesCon) {
                    msg = "New password must match confirmation field";
                } else if(oldMatchesnew) {
                    msg = "Cannot make the new password the same as the old one";
                } else {
                    msg = "Password changed!";
                    // Do password change in here
                }
                pwdChangeInfo.setText(msg); pwdChangeInfo.setForeground(Color.RED);
                pwdChangeInfo.setBounds(10, 185, 400, 30);

            }
        }

        private class ExitCHNGPWDButtonHandler implements ActionListener {
            public void actionPerformed(ActionEvent event) {
                newWindow.setVisible(false);
            }
        }
    } // End chngpwd class

    private class ExitButtonHandler implements ActionListener {
        public void actionPerformed(ActionEvent event) {
            System.exit(0);
        }
    }



    public static void main(String[] args) {
        new MainGUI();
    }


} // END CLASS
