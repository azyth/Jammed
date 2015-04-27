package jammed;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/*
 * 
protocol:
    M:

        ek = H("encrypt", ak) // hash authentication key to derive encryption key for this entry
        c = Enc(m; ek) // encrypt the message in this entry
        erase ek // forget the encryption key
        t = MAC(c; ak) // create a tag for the new link
        entry = c, t // the log entry contains the encrypted message and the tag
        record entry in log
        ak = H("iterate", ak) // iterated hash of authentication key to derive new authentication key for the next entry


 */

public class SecureLog {

	
	private String dir = "keys/";
	private SecretKey ek;
	private SecretKey ak;
	private String m;
	private byte[] c;
	private byte[] t;
	private byte[] entry;
	
	public SecureLog(){
		//load secret key ak from dir
		//this.ak = this.loadkey(...);
		//instantiate ek
		this.ek = this.encryptionKey(this.ak);
		//establish connection with secure log server?  ie folder named "secure log server"
		//
	}
	
	public boolean createSecureLog(String log){
		//
		return false;
		
	}
	
	public boolean updateSecureLog(String log, byte[] secLog){
		
		
		return false;
	}
	
	public boolean checkSecureLog(String log){
		return false;
	}
	
	//decrypts and loads secret key into this.dataSeckey
	private void loadKey(SecretKey hashPass, String file, String ivfile) throws IllegalBlockSizeException, 
			BadPaddingException, InvalidKeyException, NoSuchAlgorithmException, 
			NoSuchPaddingException, IOException, InvalidAlgorithmParameterException {
		
		byte[] encoded = Files.readAllBytes(Paths.get(file));
		byte[] iv = Files.readAllBytes(Paths.get(ivfile));
		Cipher pwc = Cipher.getInstance("AES/CBC/PKCS5Padding"); //AES/CBC/PKCS5Padding
		IvParameterSpec ips = new IvParameterSpec(iv);
		pwc.init(Cipher.DECRYPT_MODE, new SecretKeySpec(hashPass.getEncoded(), "AES"),ips); //invalid key length?
		byte[] decoded = pwc.doFinal(encoded);
		this.logkey = new SecretKeySpec(decoded,"AES");
	}
	public SecretKey encryptionKey(SecretKey ak){
		return null;
	}
	public SecretKey iterateKey(SecretKey ak){
		return null;
	}
	public byte[] encryptLogEntry(SecretKey ek, String entry){
		return null;
	}
	public byte[] tagLogEntry(SecretKey ak, byte[] encEntry){
		return null;
	}
	public byte[] writeEntry(byte[] tag, byte[] c, byte[] log){
		return null;
	}
}
