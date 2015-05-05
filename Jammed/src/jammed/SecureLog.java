package jammed;



import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.Mac;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;


/*
 * 
protocol:
    M:

        ek = H("encrypt", ak) 		// hash authentication key to derive encryption key for this entry
        c = Enc(m; ek) 				// encrypt the message in this entry
        erase ek 					// forget the encryption key
        t = MAC(c; ak) 				// create a tag for the new link
        entry = c, t 				// the log entry contains the encrypted message and the tag
        record entry in log
        ak = H("iterate", ak) 		// iterated hash of authentication key to derive new authentication key for the next entry



	functionality:
	
	SecureLog sl = new SecureLog();
	String entry = sl.createEntry(loginfo);
	...write to log
	(repeat sl.createEntry(loginfo') as many times as you would like)
	...
	sl.close(); 			//MUST be closed to record final hashed value for the next use of ak.
	
	
	in order to decode a log entry you need to know how many times the origional key was hashed to create that entry
	recording this as the line number could be useful or writing entry as hashnum:entry could be useful.  
 */

/**
 * Skeleton code in place, but current implementation is defunct.
 * @deprecated
 */
public class SecureLog {

	private static final String LOGGINGMASTERKEY = "nutritionfactsLogKey.txt";
	private static final String LOGGINGKEYFILE = "JellySecureLogKey.txt";
	private static String dir = "keys/";
	private SecretKey ek;
	private SecretKey ak;
	//private byte[] iv;
	//private String m;
	//private byte[] c;
	//private byte[] t;
	//private byte[] entry;
	/**
	 * CONTRUCTOR
	 * initializes instance variables
	 */
	public SecureLog() throws InvalidKeyException, IllegalBlockSizeException, 
			BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException, 
			InvalidAlgorithmParameterException, IOException{
		//load secret key ak from dir
		this.loadKey(dir + LOGGINGKEYFILE);
		
		//instantiate ek
		this.ek = this.encryptionKey(this.ak);
		//load iv , saved to an iv file on closeing of the last session.
		
	}
	
	public boolean close() throws IOException{
		//write ak to server ak file
		return write(this.ak.getEncoded(), dir + LOGGINGKEYFILE);
		//write last block of log file (current IV) to IV file
		
	}
	
	/********************ENROLMENT****************************/
	//called to set up server key
	public static void initLog(){
			//TODO
	}
	
	/*
	 * 
	 */
	public static void generateLogKey() throws NoSuchAlgorithmException, IOException{
		//gen key
		KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");//TODO change to ???
	    keyGenerator.init(256); 
//		System.out.println("key created");
	    SecretKey k = keyGenerator.generateKey();
		//write key to file
	    write(k.getEncoded(),LOGGINGMASTERKEY); //TODO server needs to send this to log server and then delete it. (this shoudl not remail in jelly
	    write(k.getEncoded(), dir + LOGGINGKEYFILE); // OR server need to make a copy of this on log sever before ever using it. 
		//also write to "log server"?...no
		
	}
	
	
	
	/********************LOGGING****************************/
	
	//creates the entry to be recorded in the log.
	public String createEntry(String logentry) throws InvalidKeyException, 
			NoSuchAlgorithmException{
		//take in logentry, secure it, and return encoded entry
		byte[] iv = UserData.generateIV();
		byte[] c = this.encryptLogEntry(this.ek, iv, logentry);
		byte[]  t = this.tagLogEntry(ak, c);
		//combine c and t
		
		//iterate ak and ek
		this.ak = this.iterateKey(ak);
		this.ek = this.encryptionKey(ak);
		
		//TODO get encoded of c and t
		//TODO concatenate c and t into e
		
		String e = Arrays.toString(t);// c concat e, TODO: correct this for byte array functionality
		return e;
		
	}
	
	//this is the function for reading a secure log
	public boolean checkSecureLog(String log){
		//stuff
		return false;
	}
	
	/********************Housekeeping****************************/
	
	
	//
	private SecretKey getHashedLogServerKey(SecretKey serverkey, int numhash){
		// iterate serverkey(origional ak)numhash number of times to be able to decode a log entry
		// produces ek for log entry at numhashes location
		SecretKey temp = serverkey;
		
		for (int x=0; x<numhash; x++){//itterate num hash times
			temp = encryptionKey(temp);
			
		}
		return temp;
	}
	
	
	
	//decrypts and loads secret key into this.dataSeckey
	private void loadKey(String file) throws IllegalBlockSizeException, 
			BadPaddingException, InvalidKeyException, NoSuchAlgorithmException, 
			NoSuchPaddingException, IOException, InvalidAlgorithmParameterException {
		
		byte[] encoded = Files.readAllBytes(Paths.get(file));
		//byte[] iv = Files.readAllBytes(Paths.get(ivfile));
		
		//SecretKeySpec signingKey = new SecretKeySpec(key.getBytes(), "HmacSHA1");
		
		
		//Cipher pwc = Cipher.getInstance("AES/CBC/PKCS5Padding"); //AES/CBC/PKCS5Padding
		//IvParameterSpec ips = new IvParameterSpec(iv);
		//pwc.init(Cipher.DECRYPT_MODE, new SecretKeySpec(hashPass.getEncoded(), "AES"),ips); //invalid key length?
		//byte[] decoded = pwc.doFinal(encoded);
		this.ak = new SecretKeySpec(encoded,"HmacSHA1");
	}
	
	private SecretKey encryptionKey(SecretKey ak){
		//hash key to make "encryption key" "AES?"
		
		
		return null;
	}
	
	private SecretKey iterateKey(SecretKey ak){
		//hash key with "iterate"
		return null;
	}
	
	private byte[] encryptLogEntry(SecretKey ek, byte[] ivector, String entry){
		//encrypt entry m with ek adn ivector to prduce c
		
		return null;//TODO change to string
	}
	
	private byte[] tagLogEntry(SecretKey ak, byte[] encEntry) throws 
			NoSuchAlgorithmException, InvalidKeyException{

		// get an hmac_sha1 Mac instance and initialize with the signing key
		Mac mac = Mac.getInstance("HmacSHA1");
		mac.init(ak);

		// compute the hmac on input data bytes
		byte[] hmac = mac.doFinal(encEntry);

		// base64-encode the hmac ???
		//String result = Encoding.EncodeBase64(rawHmac);

		return hmac;//TODO change to string
		
	}
	//writes a key or iv byte[] to a file. file must include filepath
	private static boolean write(byte[] skey, String file) throws IOException{
		FileOutputStream fout = new FileOutputStream(file);			
		//byte[] skey = k.getEncoded();
		try {
			fout.write(skey);
		} catch (Exception e) {
			return false; //throw e;
		}finally {
			try{
				if(fout != null) fout.close();
			}
			catch(IOException io){
				return false;
			}
		}
		return true;
	}
	
}
