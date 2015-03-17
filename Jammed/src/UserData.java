import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Hashtable;

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
	private SecretKey dataSecKey;
	private byte[] iv;
	private Hashtable<String, String> userData;//TODO. 
	
	
	public UserData(byte[] cypherText, String username){
		//Load SecretKey and IV
		this.SECKEYFILE = username+"AESkey.txt";//TODO delete this 
		//TODO IV what do we wan tto use? does it change?
		try {
			loadKey();
		} catch (NoSuchAlgorithmException | InvalidKeySpecException
				| IOException e) {
				}
	}
	
	
	/*
	 * decrypt data package received from the server for user use. 
	 */
	public String decData(String data) throws NoSuchAlgorithmException, 
	        NoSuchPaddingException, InvalidKeyException, IOException, 
	        InvalidAlgorithmParameterException, IllegalBlockSizeException, 
	        BadPaddingException{
		
		Cipher aes = Cipher.getInstance("AES/CBC/PKCS5Padding");

		IvParameterSpec ips = new IvParameterSpec(iv);
		aes.init(Cipher.DECRYPT_MODE, this.dataSecKey, ips);
		
		byte[] block = new byte[data.length()];
		//TODO block = string to byte array(data)
		block = aes.doFinal();
		return new String(block);
		
		//TODO convert to hash table
		//return / this.userdata= x
		
	}
	
	/*
	 * Encrypts the data for sending back to server. 
	 */
	public String encData(String text) throws NoSuchAlgorithmException, 
			NoSuchPaddingException, InvalidKeyException, 
			InvalidAlgorithmParameterException, IllegalBlockSizeException, 
			BadPaddingException{
		Cipher aes = Cipher.getInstance("AES/CBC/PKCS5Padding");
		IvParameterSpec ips = new IvParameterSpec(iv);
		aes.init(Cipher.ENCRYPT_MODE, this.dataSecKey, ips);
		//TODO hashtable->String/byte[]
		//TODO below is not quite right yet, just copied from above. 
		byte[] block = new byte[text.length()];
		//TODO block = stringto byte array(data)
		block = aes.doFinal();
		//can be anything not String
		return new String(block);
	}
	

	
	/************************  KEY SECTION  *************************/
	
	/*functions for the generation, handling, and storing of secret keys*/
	
	/* used to initiate secret key and store it to users machine, 
	 * either to replace a compromised key or for a new user
	 */
	public void enroll() throws NoSuchAlgorithmException, IOException{
			generateKey();
			storeKey();
	}
	
	//generates a new AES-256 secret key stores in dataSecKey
	private void generateKey() throws NoSuchAlgorithmException{
	    KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
	    keyGenerator.init(256); 
	    dataSecKey =  keyGenerator.generateKey();
	}

	//loads secret key from a local file and stores it to dataSecKey
	private void loadKey() throws IOException, NoSuchAlgorithmException, 
			InvalidKeySpecException{
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
