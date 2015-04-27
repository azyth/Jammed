package junitTests;


import jammed.LoginInfo;
import jammed.UserData;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.GeneralSecurityException;
import java.util.ArrayList;


public class Enc {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		System.out.println("I'm getting an error compiling: UserData ud = new UserData(guest); ");
		/*
		String plaintext = "I \n wish \n my toast wouldnt \n always land \n butter side, \n Down";
		ArrayList<LoginInfo> udata;
		byte[] cryptodata;
		
		
		System.out.println(plaintext);
		try {
//			UserData.enroll("guest");
			UserData ud = new UserData("guest");
			byte[] iv = Files.readAllBytes(Paths.get("guestiv.txt"));
//			FileOutputStream iout = new FileOutputStream("guestiv.txt");
//			try {
//				iout.write(iv);
////				System.out.println("key stored");
//			} catch (Exception e) {
//				throw e;
//			}finally {
//				iout.close();
//			}
			cryptodata = ud.encData(UserData.stringToList(plaintext), iv);
			FileOutputStream fout = new FileOutputStream("guestud.txt");
			try {
				fout.write(cryptodata);
//				System.out.println("key stored");
			} catch (Exception e) {
				throw e;
			}finally {
				fout.close();
			}
			System.out.println("encrypted");
			cryptodata = Files.readAllBytes(Paths.get("guestud.txt"));
			udata = ud.decData(cryptodata, iv);
			System.out.println("un-encrypted");
			System.out.println(UserData.listToString(udata));
			
		} catch (GeneralSecurityException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		*/
		//create a 

	}

}
