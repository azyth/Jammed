package jammed;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayList;

import javax.crypto.Cipher;
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
 * This is done with secretkey encryption of which the secret key is NEVER
 * TRANSMITTED except when enrolling a new machine
 *
 * AES 256 
 */
public class UserData {

    // local file name key will be stored at
	private final static String SECKEYFILE = "-AESkey.txt";
 	private String keyfile;
	private SecretKey dataSecKey;

	
	// Load Key and decrypt/encrypt Constructor
	public UserData(String username) throws GeneralSecurityException, 
	 		IOException, InvalidKeyException {
		//Load SecretKey and IV
		this.keyfile = username+SECKEYFILE;
		loadKey(this.keyfile);
	}
	
	/*
	 * creates a new userdata file and IV pair from scratch to replace out of sync
	 * pairs
	 * Requires: AES secret key is loaded into instance
	 */
	public void createBaseData(String username) throws 
			IOException, InvalidKeyException, GeneralSecurityException{
		//Cipher aes = Cipher.getInstance("AES/CBC/PKCS5Padding");
		byte [] iv = generateIV();
		String plaintext = "I \n wish \n my toast wouldnt \n always land \n butter side, \n Down";
		ArrayList<LoginInfo> data = stringToList(plaintext);
		byte[] encUD = this.encData(data,iv);
		storeIV(iv,username+"_IV.txt");
		storeIV(encUD,username+"_USERDATA.txt");
		//System.out.println(text);
	}
	
	
	/*
	 * decrypt data package received from the server for user use. 
	 * 
	 */
	public ArrayList<LoginInfo> decData(byte[] data, byte[] iv) throws 
			GeneralSecurityException, InvalidKeyException, IOException {
		
		Cipher aes = Cipher.getInstance("AES/CBC/PKCS5Padding");

		IvParameterSpec ips = new IvParameterSpec(iv);
		aes.init(Cipher.DECRYPT_MODE, this.dataSecKey, ips);
				
		byte[] text = aes.doFinal(data);
		String userdata = new String(text, "UTF8");	
		//System.out.println("data decrypted");
		//System.out.println(userdata);


		return stringToList(userdata);
	}
	
	/*
	 * Encrypts the data for sending back to server. 
	 * 
	 */
	public byte[] encData(ArrayList<LoginInfo> data, byte[] iv) throws 
			GeneralSecurityException, InvalidKeyException, IOException {
		Cipher aes = Cipher.getInstance("AES/CBC/PKCS5Padding");
		
		IvParameterSpec ips = new IvParameterSpec(iv);
		aes.init(Cipher.ENCRYPT_MODE, this.dataSecKey, ips);

		String text = listToString(data);
		byte[] textbyte = text.getBytes("UTF8");
		
		byte[] block = aes.doFinal(textbyte);
		//System.out.println("data encrypted");
		//System.out.println(block);

		return block;
	}
	

	
	/************************  KEY SECTION  *************************/
	
	/*functions for the generation, handling, and storing of secret keys*/
	
	/* used to initiate secret key and store it to users machine, 
	 * either to replace a compromised key or for a new user
	 */
	public static void enroll(String username) throws IOException, 
			GeneralSecurityException {
		
		storeKey(generateKey(),username+SECKEYFILE);
	}
	
	//generates a new AES-256 secret key stores in dataSecKey
	private static SecretKey generateKey() throws GeneralSecurityException {
	    KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
	    keyGenerator.init(256); 
//		System.out.println("key created");
	    return keyGenerator.generateKey();
	}
	//generates a random string of bytes for a new IV
	public byte[] generateIV(){
			return SecureRandom.getSeed(16);
	}

	//loads secret key from a local file and stores it to dataSecKey
	private void loadKey(String file) throws IOException, GeneralSecurityException {
		byte[] encoded = Files.readAllBytes(Paths.get(file));
		
//		SecretKeyFactory skf = SecretKeyFactory.getInstance("AES");				//TODO change to secretKeySpec
//		dataSecKey = skf.generateSecret(new SecretKeySpec(encoded,"AES"));
		dataSecKey = new SecretKeySpec(encoded,"AES");
//		System.out.println("key loaded");
	}
	
	//writes secret key to a file on local machine for storage 
	private static void storeKey(SecretKey key, String file) throws IOException {
		FileOutputStream fout = new FileOutputStream(file);
		try {
			fout.write(key.getEncoded());
//			System.out.println("key stored");
		} catch (Exception e) {
			throw e;
		}finally {
			fout.close();
		}
	}

//	These functions would only be used if we are not passing in the IV with userdata
	
//	private void loadIV(String file) throws IOException {
//		iv = Files.readAllBytes(Paths.get(file));
//	}
	private void storeIV(byte[] iv, String ivfile) throws IOException {
		FileOutputStream ivout = new FileOutputStream(ivfile);
		ivout.write(iv);
		ivout.close();
	}

	/************************  ARRAYLIST SECTION  
	 * @throws IOException 
	 * @throws GeneralSecurityException 
	 * @throws InvalidKeyException *************************/
  // Note that, for these methods to work, no piece of user data can contain a
  // newline. This is probably OK. The UI ensures this.

  // test test test
  public static void main(String[] args) throws IOException, InvalidKeyException, GeneralSecurityException {
	  UserData ud = new UserData("guest");
	  
	//create new data/IV pair
		 
	  ud.createBaseData("guest");
	  //Test 1
	  byte[] cryptodata = Files.readAllBytes(Paths.get("guest_USERDATA.txt"));
	  ArrayList<LoginInfo> udata;
		byte[] iv = Files.readAllBytes(Paths.get("guest_IV.txt"));
		
		udata = ud.decData(cryptodata, iv);
		System.out.println("un-encrypted");
		System.out.println(UserData.listToString(udata));
		//storeIV(,"string.txt")
		
   
	 
	  
	
		
		
		/*String good = "this\nis\na\ngood\nstring\nthing";
    String bad = "this\none\nis\nnot";
    String none = "";

    LoginInfo l1 = new LoginInfo();
    l1.website = "wone"; l1.username = "uone"; l1.password = "pone";
    LoginInfo l2 = new LoginInfo();
    l2.website = "wtwo"; l2.username = "utwo"; l2.password = "ptwo";

    ArrayList<LoginInfo> empty = new ArrayList<LoginInfo>();
    ArrayList<LoginInfo> oneentry = new ArrayList<LoginInfo>();
    oneentry.add(l1);
    ArrayList<LoginInfo> twoentries = new ArrayList<LoginInfo>();
    twoentries.add(l1); twoentries.add(l2);

    try {
      System.out.print("Good: ");
      System.out.println(good.equals(listToString(stringToList(good))));

      System.out.print("None: ");
      System.out.println(none.equals(listToString(stringToList(none))));

      System.out.print("Bad: ");
      System.out.println(bad.equals(listToString(stringToList(bad))));
    } catch (IllegalArgumentException e) {
      System.out.println("exception");
    }

    System.out.print("Empty: ");
    System.out.println(listToString(empty).equals(""));

    System.out.print("One: ");
    System.out.println(listToString(oneentry).equals("wone\nuone\npone"));

    System.out.print("Two: ");
    System.out.println(listToString(twoentries).
                       equals("wone\nuone\npone\nwtwo\nutwo\nptwo"));*/
  }

  public static String listToString(ArrayList<LoginInfo> lst) {
    StringBuffer str = new StringBuffer();

    for (LoginInfo l : lst) {
      if (str.length() > 0) {
        str.append("\n");
      }
      str.append(l.website + "\n" + l.username + "\n" + l.password);
    }

    return str.toString();
  }

  public static ArrayList<LoginInfo> stringToList(String str) {
    // handle zero-length string specially TODO any other edge cases?
    if (str.equals("")) {
      return new ArrayList<LoginInfo>();
    }

    String[] parts = str.split("\n");

    // make sure there are 3x parts
    if (parts.length % 3 != 0) {
      throw new IllegalArgumentException();
    }

    ArrayList<LoginInfo> lst = new ArrayList<LoginInfo>();

    for (int i=0; i<parts.length; i += 3) {
      LoginInfo l = new LoginInfo();
      l.website = parts[i];
      l.username = parts[i+1];
      l.password = parts[i+2];

      lst.add(l);
    }

    return lst;
  }

}
