package junitTests;


import jammed.LoginInfo;
import jammed.UserData;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;


public class Enc {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		String plaintext = "I \n wish \n my toast wouldnt \n always land \n butter side, \n Down";
		ArrayList<LoginInfo> udata;
		byte[] cryptodata;
		
		
		System.out.println(plaintext);
		try {
			UserData.enroll("john");
			UserData ud = new UserData("john");
			byte[] iv = ud.generateIV();
			cryptodata = ud.encData(UserData.stringToList(plaintext), iv);
			System.out.println("encrypted");
			udata = ud.decData(cryptodata, iv);
			System.out.println("un-encrypted");
			System.out.println(UserData.listToString(udata));
			
		} catch (GeneralSecurityException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
		//create a 

	}

}
