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
                String dataHeade2 = "--------\t--------\t--------";
                newFile.println(dataHeader);
                newFile.println(dataHeade2);
                newFile.close();

                Reader rd = new FileReader(fileDataPath);
                userData.read(rd, "Data");
            } catch(Exception er) {
            }
        }
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
        JButton addData = new JButton("Add Data"); addData.setBounds(440, 120, 100, 50);
        addData.addActionListener(new AddDataButtonHandler());

        JButton saveButton = new JButton("Save Changes");
        saveButton.setBounds(30, 500, 100, 50);
        saveButton.addActionListener(new SaveButtonHandler());

        con.add(userData);
        //
        con.add(usernameLabel);
        con.add(usernameTF);
        con.add(passwordLabel);
        con.add(passwordTF);
        con.add(ServiceLabel);
        con.add(serviceTF);
        //
        con.add(saveButton);
        con.add(addData);
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
            } catch (Exception e) {
            }
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

    public static void main(String[] args) {
        new MainGUI();
    }
}
