package jammed;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.AlgorithmParameters;
import java.security.GeneralSecurityException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidParameterSpecException;
import java.util.ArrayList;
import java.util.Arrays.*;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.Mac;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
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
	private final static String SECKEYFILE = "-ProtectedAESkey.txt";
	private final static String IVFILE = "-sessionKey.txt";
	private final static String IVTAG = "-IVtag.txt";
	private final static String DATATAG = "-Datatag.txt";
	private final static byte[] salt = {0x0};
	//private final static byte[] iv = {0xc,0x0,0xf,0x0};
 	//private String keyfile;
 	//private String keyivfile;
	private SecretKey dataSecKey;
	private String dir = "keys/";
	private String uname = "";

	
	//CONSTRUCTOR
	// Load Key and decrypt it with the users password
	public UserData(String username, String password, String filepath) throws GeneralSecurityException, 
	 		IOException, InvalidKeyException {
//		this.keyfile = username+SECKEYFILE;
//		this.keyivfile = username+IVFILE;
		this.uname = username;
		this.dir = filepath;

		//hash the password, 
		SecretKey hashPass = hashPwd(password);
		//load the encrypted key file, decrpyt with hashed password. load secret key to dataSecKey
		this.decKey(hashPass,dir+username+SECKEYFILE, dir+username+IVFILE);
		
		
		//Load SecretKey and IV
//		this.keyfile = username+SECKEYFILE;
//		loadKey(this.keyfile);
	}
	
	//creates a mac tag of the supplied byte[]
	public byte[] tagdata(byte[] encEntry) throws 
			NoSuchAlgorithmException, InvalidKeyException{
		
		// get an hmac_sha1 Mac instance and initialize with the signing key
		Mac mac = Mac.getInstance("HmacSHA1");
		mac.init(this.dataSecKey);
		
		// compute the hmac on input data bytes
		byte[] hmac = mac.doFinal(encEntry);
		
		// base64-encode the hmac ???
		//String result = Encoding.EncodeBase64(rawHmac);
		
		return hmac;//TODO change to string
		
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
		String plaintext = "I\nwish\nmy toast wouldnt\nalways land\nbutter side,\nDown";//Default string for userdata file
		ArrayList<LoginInfo> data = stringToList(plaintext);
		byte[] encUD = this.encData(data,iv);
		storeIV(iv,dir+username+"_IV.txt");
		//this.iv=iv;
		storeIV(encUD,username+"_USERDATA.txt");
		//this.ud=encUD;
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
		if (text.equalsIgnoreCase("")){  				//blank string crypto check
			text = "default\nvalue\nhere";
		}
		byte[] textbyte = text.getBytes("UTF8");
		
		byte[] block = aes.doFinal(textbyte);
		
		return block;
	}
	

	
	/************************  KEY SECTION  *************************/
	
	/*functions for the generation, handling, and storing of secret keys*/
	
	/* used to initiate secret key and store it to users machine, 
	 * either to replace a compromised key or for a new user
	 */
	public static void enroll(String username, String password, String filepath) 
			throws IOException,	GeneralSecurityException {
		
		storeKey(generateKey(),hashPwd(password),username, filepath);//(new AES KEY, filepath to save key file)
		
	}
	
	//generates a new AES-256 secret key stores in dataSecKey
	private static SecretKey generateKey() throws GeneralSecurityException {
	    KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
	    keyGenerator.init(256); 

	    return keyGenerator.generateKey();
	}
	//generates a random string of bytes for a new IV
	public static byte[] generateIV(){
			return SecureRandom.getSeed(16);
	}
	
	  /* Purpose: Hash a given password with a given salt
     *  Input: password as a string, salt as a byte[]
     *  Output: None.
     *  Return: The hashed password as a byte[]. Returns null upon failure
     */
    public static SecretKey hashPwd(String pwd) { 
        
    	if(pwd == null || pwd.length() == 0) {
            return null;
        }

        char[] password = pwd.toCharArray();

        PBEKeySpec spec = new PBEKeySpec(password, salt, 1000, 192);
        try {
            SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
            return skf.generateSecret(spec);

        } catch (Exception e) {
            throw new AssertionError("Error while hashing a password: " + e.getMessage(), e);
        } finally {
            spec.clearPassword();
        }


    }
	
	//writes secret key to a file on local machine for storage 
	private static void storeKey(SecretKey key, SecretKey encryptionKey, String username, String filepath) //add String filepath argument
			throws IOException, InvalidKeyException, IllegalBlockSizeException, 
			BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException, 
			InvalidParameterSpecException, InvalidAlgorithmParameterException {
		
		FileOutputStream fout = new FileOutputStream(filepath+username+SECKEYFILE);			
		FileOutputStream iout = new FileOutputStream(filepath+username+IVFILE);
		byte[] skey = key.getEncoded();
		Keys keys = encKey(skey,encryptionKey);
		byte[] encKey = keys.block;
		byte[] iv = keys.ivector;
		try {
			fout.write(encKey);
			iout.write(iv);
		} catch (Exception e) {
			throw e;
		}finally {
			fout.close();
			iout.close();
		}
	}
	//deletes stored keys for this instance. BE VERY CAREFUL WITH THIS
	public void deleteKeys() throws IOException{
		Files.deleteIfExists(Paths.get(this.dir, this.uname+SECKEYFILE));
		Files.deleteIfExists(Paths.get(this.dir, this.uname+IVFILE));
	}

	//encrypts the secret key with the users password.
	private static Keys encKey(byte[] encodedSecretKey, SecretKey encryptionKey) 
			throws IllegalBlockSizeException, BadPaddingException, NoSuchAlgorithmException, 
			NoSuchPaddingException, InvalidKeyException, IOException, InvalidParameterSpecException, InvalidAlgorithmParameterException {
		Cipher aes = Cipher.getInstance("AES/CBC/PKCS5Padding");
		byte[] ivec = generateIV();
		aes.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(encryptionKey.getEncoded(), "AES"),new IvParameterSpec(ivec)); //invalid key length new SecretKeySpec(encryptionKey.getEncoded(), "AES")
		AlgorithmParameters params = aes.getParameters();
		byte[] iv = params.getParameterSpec(IvParameterSpec.class).getIV();
		byte[] block = aes.doFinal(encodedSecretKey);
		
		return new Keys(block,iv);
	}
	
	//decrypts and loads secret key into this.dataSeckey
	private void decKey(SecretKey hashPass, String file, String ivfile) throws IllegalBlockSizeException, 
			BadPaddingException, InvalidKeyException, NoSuchAlgorithmException, 
			NoSuchPaddingException, IOException, InvalidAlgorithmParameterException {
		//System.out.println(file);
		byte[] encoded = Files.readAllBytes(Paths.get(file));
		byte[] iv = Files.readAllBytes(Paths.get(ivfile));
		Cipher pwc = Cipher.getInstance("AES/CBC/PKCS5Padding"); //AES/CBC/PKCS5Padding
		IvParameterSpec ips = new IvParameterSpec(iv);
		pwc.init(Cipher.DECRYPT_MODE, new SecretKeySpec(hashPass.getEncoded(), "AES"),ips); //invalid key length?
		byte[] decoded = pwc.doFinal(encoded);
		this.dataSecKey = new SecretKeySpec(decoded,"AES");
	}

	//
	public byte[] loadIV(String file) throws IOException {
		return Files.readAllBytes(Paths.get(file));
	}
	public void storeIV(byte[] iv, String ivfile) throws IOException {
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

  // test test test MAIN 
  public static void main(String[] args) throws IOException, InvalidKeyException, GeneralSecurityException {
//    enroll("guest", "guest");
//	  UserData ud = new UserData("guest","guest");
//	  
//	//create new data/IV pair
//		 
//	  ud.createBaseData("guest");
//	  //Test 1
//	  byte[] cryptodata = Files.readAllBytes(Paths.get("guest_USERDATA.txt"));
//	  ArrayList<LoginInfo> udata;
//		byte[] iv = Files.readAllBytes(Paths.get("guest_IV.txt"));
//		
//		udata = ud.decData(cryptodata, iv);
		//System.out.println("un-encrypted");
		//System.out.println(UserData.listToString(udata));
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

  private static class Keys{
	  
	  public Keys(byte[] block, byte[] iv) {
		this.block=block;
		this.ivector=iv;
	}
	  private byte[] block;
	  private byte[] ivector;
}
}

