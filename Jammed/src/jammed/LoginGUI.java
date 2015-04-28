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

public class LoginGUI extends JFrame {

    private JTextField usernameTF, passwordTF;
    private JLabel extraInfo = new JLabel(" ");

    public LoginGUI() { // the frame constructor method
        super("Jammed"); setBounds(300, 200, 600, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Container con = getContentPane(); // inherit main frame
        con.setBackground(Color.WHITE);
        con.setLayout(null);

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

        String infoMessage = "Passwords must be alphanumeric";
        JLabel info = new JLabel(infoMessage);
        info.setSize(300, 30); info.setLocation(20, 200);

        extraInfo.setSize(300, 30); extraInfo.setLocation(20, 230);

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

    private class RegisterButtonHandler implements ActionListener {
        public void actionPerformed(ActionEvent event) {
            String username, password;
            username = usernameTF.getText();
            password = passwordTF.getText();

            extraInfo.setText("Username already exists!");
            extraInfo.setForeground(Color.RED);

            // register user
        }
    }

    private class LoginButtonHandler implements ActionListener {
        public void actionPerformed(ActionEvent event) {
            String username, password;
            username = usernameTF.getText();
            password = passwordTF.getText();


            extraInfo.setText("Incorrect login credentials!");
            extraInfo.setForeground(Color.RED);
            // Attempt to login
        }
    }

    private class ExitButtonHandler implements ActionListener {
        public void actionPerformed(ActionEvent event) {
            System.exit(0);
        }
    }

    /* Main method to run the gui*/
    public static void main(String args[]) {
        new LoginGUI();
    }

}
