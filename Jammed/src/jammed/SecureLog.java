package jammed;



import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

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

public class SecureLog {

	private static final String LOGGINGMASTERKEY = "nutritionfactsLogKey.txt";
	private static final String LOGGINGKEYFILE = "JellySecureLogKey.txt";
	private static String dir = "keys/";
	private SecretKey ek;
	private SecretKey ak;
	private byte[] iv;
	//private String m;
	//private byte[] c;
	//private byte[] t;
	//private byte[] entry;
	
	public SecureLog() throws InvalidKeyException, IllegalBlockSizeException, 
			BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException, 
			InvalidAlgorithmParameterException, IOException{
		//load secret key ak from dir
		this.loadKey(dir + LOGGINGKEYFILE);
		
		//instantiate ek
		this.ek = this.encryptionKey(this.ak);
		//
	}
	
	public static void generateLogKey() throws NoSuchAlgorithmException, IOException{
		//gen key
		KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");//TODO change to ???
	    keyGenerator.init(256); 
//		System.out.println("key created");
	    SecretKey k = keyGenerator.generateKey();
		//write key to file
	    writeKey(k,LOGGINGMASTERKEY); //server needs to send this to log server and then delete it. (this shoudl not remail in jelly
	    writeKey(k, dir + LOGGINGKEYFILE); // OR server need to make a copy of this on log sever before ever using it. 
		//also write to "log server"?...no
		
	}
	public String createEntry(String logentry) throws InvalidKeyException, 
			NoSuchAlgorithmException{
		//take in logentry, secure it, and return encoded entry
		byte[] c = this.encryptLogEntry(this.ek, logentry);
		byte[]  t = this.tagLogEntry(ak, c);
		//combine c and t
		
		//iterate ak and ek
		this.ak = this.iterateKey(ak);
		this.ek = this.encryptionKey(ak);
		
		//TODO get encoded of c and t
		//TODO concatenate c and t into e
		
		String e = "c and t";
		return e;
		
	}
	
	public SecretKey getHashedLogServerKey(SecretKey serverkey, int numhash){
		// iterate serverkey(origional ak)numhash number of times to be able to decode a log entry
		// produces ek for log entry at numhashes location
		SecretKey temp = serverkey;
		
		for (int x=0; x<numhash; x++){//itterate num hash times
			temp = encryptionKey(temp);
		}
		return temp;
	}
	
	public boolean checkSecureLog(String log){
		//stuff
		return false;
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
	
	public SecretKey encryptionKey(SecretKey ak){
		//hash key to make "encryption key" "AES?"
		
		
		return null;
	}
	
	public SecretKey iterateKey(SecretKey ak){
		//hash key with "iterate"
		return null;
	}
	
	public byte[] encryptLogEntry(SecretKey ek, String entry){
		//encrypt entry m with ek to prduce c
		
		return null;//TODO change to string
	}
	
	public byte[] tagLogEntry(SecretKey ak, byte[] encEntry) throws 
			NoSuchAlgorithmException, InvalidKeyException{

		// get an hmac_sha1 Mac instance and initialize with the signing key
		Mac mac = Mac.getInstance("HmacSHA1");
		mac.init(ak);

		// compute the hmac on input data bytes
		byte[] rawHmac = mac.doFinal(encEntry);

		// base64-encode the hmac ???
		//String result = Encoding.EncodeBase64(rawHmac);

		return rawHmac;//TODO change to string
		
	}

	public static boolean writeKey(SecretKey k, String file) throws IOException{
		FileOutputStream fout = new FileOutputStream(file);			//dir +"/"+
		byte[] skey = k.getEncoded();
		try {
			fout.write(skey);
		} catch (Exception e) {
			return false; //throw e;
		}finally {
			fout.close();
		}
		return true;
	}
	public boolean close() throws IOException{
		//write ak to server ak file
		return writeKey(this.ak, dir + LOGGINGKEYFILE);
		
	}
}
