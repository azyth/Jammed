import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

	//John

/*
 * UserData Handles encryption and decryption of the user data file 
 * which contains all of the protected password information .
 * 
 * This is done with secretkey encryption of which the secret key is NEVER TRANSMITTED
 * except when enrolling a new machine
 *
 * AES 256 
 */
public class UserData {
	
	//instance variables 
	private String SECKEYFILE = "userAESkey.txt";//local file path key will be stored at
	private String IVFILE = "userIV.txt";
	private SecretKey dataSecKey;
	private byte[] iv;
	private String userdata;
	
	// New Key generation Constructor
	public UserData(String username) throws NoSuchAlgorithmException,
                                          IOException {
		this.SECKEYFILE = username+"AESkey.txt";
		this.IVFILE = username+"IV.txt";
    enroll();
	}

	// Load Key and decrypt/encrypt Constructor
	public UserData(byte[] cypherText, String username)
         throws NoSuchAlgorithmException, IOException, InvalidKeySpecException, 
         InvalidKeyException, NoSuchPaddingException, InvalidAlgorithmParameterException, 
         IllegalBlockSizeException, BadPaddingException {
		//Load SecretKey and IV
		this.SECKEYFILE = username+"AESkey.txt";
		this.IVFILE = username+"IV.txt";
	    loadKey();
	    loadIV();
	    decData(cypherText); //automatically decode? or wait and manually call it later?
	}
	
	
	/*
	 * decrypt data package received from the server for user use. 
	 * 
	 * return hashtable or String?
	 */
	public ArrayList<LoginInfo> decData(byte[] data)
         throws NoSuchAlgorithmException, NoSuchPaddingException,
                InvalidKeyException, IOException,
                InvalidAlgorithmParameterException, IllegalBlockSizeException, 
                BadPaddingException {
		
		Cipher aes = Cipher.getInstance("AES/CBC/PKCS5Padding");

		IvParameterSpec ips = new IvParameterSpec(iv);
		aes.init(Cipher.DECRYPT_MODE, this.dataSecKey, ips);
		
		//TODO dec any string encoding
		
		byte[] text = aes.doFinal(data);
		this.userdata = new String(text, "UTF8");	//stores userdata to instance
		
		
		//TODO String -> Hashtable
		//return this.userdata;						//returns userdata string
		return null;							//DO you want to actually return and array list here?
	}
	
	/*
	 * Encrypts the data for sending back to server. 
	 * convert hashtabel -> string before passing to endData()
	 */
	public byte[] encData(ArrayList<LoginInfo> data)
         throws NoSuchAlgorithmException, NoSuchPaddingException,
                InvalidKeyException, InvalidAlgorithmParameterException,
                IllegalBlockSizeException, BadPaddingException,
                IOException {
		Cipher aes = Cipher.getInstance("AES/CBC/PKCS5Padding");
		generateIV();
		storeIV();
		IvParameterSpec ips = new IvParameterSpec(iv);
		aes.init(Cipher.ENCRYPT_MODE, this.dataSecKey, ips);
		//TODO hashtable->String/byte[]
    String text = "";
		byte[] textbyte = text.getBytes("UTF8");
		
		byte[] block = aes.doFinal(textbyte);
		
		//TODO any encoding or just plain bytes?s
		return block;
	}
	

	
	/************************  KEY SECTION  *************************/
	
	/*functions for the generation, handling, and storing of secret keys*/
	
	/* used to initiate secret key and store it to users machine, 
	 * either to replace a compromised key or for a new user
	 */
	public void enroll() throws NoSuchAlgorithmException, IOException{
			generateKey();
			storeKey();
			generateIV();
			storeIV();
	}
	
	//generates a new AES-256 secret key stores in dataSecKey
	private void generateKey() throws NoSuchAlgorithmException{
	    KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
	    keyGenerator.init(256); 
	    dataSecKey =  keyGenerator.generateKey();
	}
	private void generateIV(){
						
				iv = SecureRandom.getSeed(16);
	}
	private void loadIV() throws IOException{
		FileInputStream ivInput = new FileInputStream(this.IVFILE);
		ivInput.read(iv);
		ivInput.close();
	}
	private void storeIV() throws IOException{
		FileOutputStream ivout = new FileOutputStream(this.IVFILE);
		ivout.write(iv);
		ivout.close();
	}

	//loads secret key from a local file and stores it to dataSecKey
	private void loadKey() throws IOException, NoSuchAlgorithmException, 
                                InvalidKeySpecException {
		FileInputStream secInput = new FileInputStream(this.SECKEYFILE);
		byte[] encoded = new byte[this.SECKEYFILE.length()];
		secInput.read(encoded);
		secInput.close();
		SecretKeyFactory skf = SecretKeyFactory.getInstance("AES");
		dataSecKey = skf.generateSecret(new SecretKeySpec(encoded,"AES"));
		
		System.out.println("session key has been aquired");//TODO comment
	}
	
	//writes secret key to a file on local machine for storage 
	private void storeKey() throws IOException{
		FileOutputStream fout = new FileOutputStream(this.SECKEYFILE);
		fout.write(dataSecKey.getEncoded());
		fout.close();
	}
}
