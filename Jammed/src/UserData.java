import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.SecureRandom;
import java.util.ArrayList;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
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
	private static final String SECKEYFILE = "-AESkey.txt";
  // local file name the IV will be stored at
  private static final String IVFILE = "-IV.txt";
	
	//instance variables 
	private SecretKey dataSecKey;
	private byte[] iv;
	
	// Load Key and decrypt/encrypt Constructor
	public UserData(String username)
         throws GeneralSecurityException, IOException, InvalidKeyException {
		//Load SecretKey and IV
    loadKey(username + SECKEYFILE);
    loadIV(username + IVFILE);
	}
	
	
	/*
	 * decrypt data package received from the server for user use. 
	 * 
	 * return hashtable or String?
	 */
	public ArrayList<LoginInfo> decData(byte[] data)
         throws GeneralSecurityException, InvalidKeyException, IOException {
		
		Cipher aes = Cipher.getInstance("AES/CBC/PKCS5Padding");

		IvParameterSpec ips = new IvParameterSpec(iv);
		aes.init(Cipher.DECRYPT_MODE, this.dataSecKey, ips);
		
		//TODO dec any string encoding
		
		byte[] text = aes.doFinal(data);
		String userdata = new String(text, "UTF8");	//stores userdata to instance
		
		//return userdata
		return stringToList(userdata);
	}
	
	/*
	 * Encrypts the data for sending back to server. 
	 * convert hashtabel -> string before passing to endData()
	 */
	public byte[] encData(ArrayList<LoginInfo> data)
         throws GeneralSecurityException, InvalidKeyException, IOException {
		Cipher aes = Cipher.getInstance("AES/CBC/PKCS5Padding");
		//generateIV();
		//storeIV(); // TODO do you use a different IV every time? What if you
                 // want to access from multiple machines?
		IvParameterSpec ips = new IvParameterSpec(iv);
		aes.init(Cipher.ENCRYPT_MODE, this.dataSecKey, ips);

    String text = listToString(data);
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
	public static void enroll(String username)
         throws IOException, GeneralSecurityException {
			SecretKey dataSecKey = generateKey();
			storeKey(dataSecKey, username + SECKEYFILE);
			byte[] iv = generateIV();
			storeIV(iv, username + IVFILE);
	}
	
	//generates a new AES-256 secret key stores in dataSecKey
	private static SecretKey generateKey() throws GeneralSecurityException {
	    KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
	    keyGenerator.init(256); 
	    return keyGenerator.generateKey();
	}

	private static byte[] generateIV(){
			return SecureRandom.getSeed(16);
	}

	private void loadIV(String file) throws IOException {
    iv = Files.readAllBytes(Paths.get(file));
	}

	private static void storeIV(byte[] iv, String ivfile) throws IOException {
		FileOutputStream ivout = new FileOutputStream(ivfile);
		ivout.write(iv);
		ivout.close();
	}

	//loads secret key from a local file and stores it to dataSecKey
	private void loadKey(String file)
          throws IOException, GeneralSecurityException {
		byte[] encoded = Files.readAllBytes(Paths.get(file));
		SecretKeyFactory skf = SecretKeyFactory.getInstance("AES");
		dataSecKey = skf.generateSecret(new SecretKeySpec(encoded,"AES"));
	}
	
	//writes secret key to a file on local machine for storage 
	private static void storeKey(SecretKey key, String kfile) throws IOException {
		FileOutputStream fout = new FileOutputStream(kfile);
		fout.write(key.getEncoded());
		fout.close();
	}


	/************************  ARRAYLIST SECTION  *************************/
  // Note that, for these methods to work, no piece of user data can contain a
  // newline. This is probably OK. The UI ensures this.

  // test test test
  public static void main(String[] args) {
    String good = "this\nis\na\ngood\nstring\nthing";
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
                       equals("wone\nuone\npone\nwtwo\nutwo\nptwo"));
  }

  private static String listToString(ArrayList<LoginInfo> lst) {
    String str = "";

    for (LoginInfo l : lst) {
      if (str.length() > 0) {
        str += "\n";
      }
      str += l.website + "\n" + l.username + "\n" + l.password;
    }

    return str;
  }

  private static ArrayList<LoginInfo> stringToList(String str) {
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
