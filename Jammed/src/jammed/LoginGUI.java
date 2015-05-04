package jammed;

/*
 * Created by Marcos on 4/27/15
 *
 * LoginGUI for the client application
 */

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.nio.file.Paths;

public class LoginGUI extends JFrame {

    private JTextField usernameTF, passwordTF;
    private JLabel extraInfo = new JLabel(" ");
    private String fileChosenToStoreKeys="keys/";

    private LoginInfo login = new LoginInfo();

    public LoginGUI() { // the frame constructor method
        super("Jammed"); setBounds(300, 200, 600, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Container con = getContentPane(); // inherit main frame
        con.setBackground(Color.WHITE);
        con.setLayout(null);
        //
        //td = new theDirectory();
        //
        JLabel headerLabel = new JLabel("Welcome to Jammed! Please login or register!", JLabel.CENTER);
        headerLabel.setSize(300, 30); headerLabel.setLocation(150, 5);

        JLabel usernameLabel = new JLabel("Enter your username: ");
        usernameLabel.setSize(300, 30); usernameLabel.setLocation(50, 45);
        JLabel passwordLabel = new JLabel("Enter your password: ");
        passwordLabel.setSize(300, 30); passwordLabel.setLocation(50, 85);

        usernameTF = new JTextField(10);
        usernameTF.setBounds(200, 45, 300, 35);
        passwordTF = new JPasswordField(10);
        passwordTF.setBounds(200, 85, 300, 35);

        JButton loginB = new JButton("Login"); loginB.setBounds(55, 140, 150, 50);
        loginB.addActionListener(new LoginButtonHandler());
        JButton registerB = new JButton("Register"); registerB.setBounds(225, 140, 150, 50);
        registerB.addActionListener(new RegisterButtonHandler());
        JButton exitB = new JButton("Exit"); exitB.setBounds(395, 140, 150, 50);
        exitB.addActionListener(new ExitButtonHandler());

        JButton fileChooserB = new JButton("Choose Dir"); fileChooserB.setBounds(395, 200, 150, 50);
        fileChooserB.addActionListener(new FChooserButtonHandler());

        String infoMessage = "Usernames must be alphanumeric";
        JLabel info = new JLabel(infoMessage);
        info.setSize(300, 30); info.setLocation(20, 200);

        extraInfo.setSize(300, 30); extraInfo.setLocation(20, 230);

        con.add(fileChooserB);
        con.add(headerLabel);
        con.add(usernameLabel);
        con.add(usernameTF);
        con.add(passwordLabel);
        con.add(passwordTF);
        con.add(loginB);
        con.add(registerB);
        con.add(exitB);
        con.add(info);
        con.add(extraInfo);

        setVisible(true);
    }

    /** Class to handle user registration */
    private class RegisterButtonHandler implements ActionListener {
        public void actionPerformed(ActionEvent event) {
            cleartext();
            String username, password;
            username = usernameTF.getText();
            password = passwordTF.getText();

            boolean notBS = !username.isEmpty() && !password.isEmpty();

            if(notBS) {
                //ChooseKeyLocation ckl = new ChooseKeyLocation(); // Make it wait on this
                synchronized (login) {
                    setLogin("enroll", username, password);
                    login.notify();
                }
            } else {
                extraInfo.setText("Invalid username or password!");
                extraInfo.setForeground(Color.RED);
            }
        }
    }

    /** Class to handle when the user presses the loging button */
    private class LoginButtonHandler implements ActionListener {
        public void actionPerformed(ActionEvent event) {
            cleartext();

            String username, password;
            username = usernameTF.getText();
            password = passwordTF.getText();
            boolean notBS = !username.isEmpty() && !password.isEmpty();

            if(notBS) {
                //ChooseKeyLocation ckl = new ChooseKeyLocation(); // Make it wait on this
                synchronized (login) {
                    setLogin("", username, password);
                    login.notify();
                }

            } else {
                extraInfo.setText("Invalid username or password!");
                extraInfo.setForeground(Color.RED);
            }
        }
    }

    /** Exits the program */
    private class ExitButtonHandler implements ActionListener {
        public void actionPerformed(ActionEvent event) {
            System.exit(0);
        }
    }

    public void setLogin(String website, String username, String password) {
        login.website = website; login.username = username; login.password = password;
    }

    public LoginInfo getLogin() throws InterruptedException {
        LoginInfo copy = new LoginInfo();
        synchronized (login) {
            // check if this has been set yet -- if it has, username should be
            // nonempty
            while (login.username.equals("")) {
                login.wait();
            }
            copy.website = login.website;
            copy.username = login.username;
            copy.password = login.password;
            login.website = ""; login.username = ""; login.password = "";
        }
        return copy;
    }

    public void disableGui() {
        setVisible(false);
    }

    public void error(String message) {
        extraInfo.setText(message);
        extraInfo.setForeground(Color.RED);
    }

    private void cleartext() {
        extraInfo.setText("");
        extraInfo.setForeground(Color.WHITE);
    }

    private class FChooserButtonHandler implements ActionListener {
        public void actionPerformed(ActionEvent event) {

            JFrame frame = new JFrame("Choose Key Location");
            frame.setLayout(new FlowLayout());
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            fileChooser.setAcceptAllFileFilterUsed(false);
            int returnValue = fileChooser.showOpenDialog(null);
            if (returnValue == JFileChooser.APPROVE_OPTION) {
                fileChosenToStoreKeys = fileChooser.getSelectedFile().toPath().normalize().toString()+"/";
            }
        }
    }

    public String getDirChosenToStoreKeys() {
        return fileChosenToStoreKeys;
    }

    /* Main method to run the gui*/
    public static void main(String args[]) {
        new LoginGUI();
    }

}
