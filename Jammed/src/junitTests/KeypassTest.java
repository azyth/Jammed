package junitTests;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.SecureRandom;
import java.util.ArrayList;

import jammed.LoginInfo;
import jammed.UserData;

public class KeypassTest {

	/**
	 * @param args
	 * @throws IOException 
	 * @throws GeneralSecurityException 
	 * @throws InvalidKeyException 
	 */
	public static void main(String[] args) throws InvalidKeyException, GeneralSecurityException, IOException {
		//Full test
//		UserData.enroll("guest", "password");
//		System.out.println("enrolled");
//		UserData ud = new UserData("guest","password");
//		ud.createBaseData("guest");
//		byte[] cryptodata = Files.readAllBytes(Paths.get("guest_USERDATA.txt"));
//		ArrayList<LoginInfo> udata;
//		byte[] iv = Files.readAllBytes(Paths.get("guest_IV.txt"));
//			
//		udata = ud.decData(cryptodata, iv);
//		System.out.println("un-encrypted");
//		System.out.println(UserData.listToString(udata));
//			//storeIV(,"string.txt")
//		System.out.println("done, check the files");
		//test the password decoding once you hav already enrolled
		UserData ud = new UserData("guest","password","keys/");
		byte[] cryptodata = Files.readAllBytes(Paths.get("guest_USERDATA.txt"));
		ArrayList<LoginInfo> udata;
		byte[] iv = Files.readAllBytes(Paths.get("guest_IV.txt"));
			
		udata = ud.decData(cryptodata, iv);
		System.out.println("un-encrypted");
		System.out.println(UserData.listToString(udata));
	}

}
