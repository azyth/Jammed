import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

	//John

/*
 * UserData Handles dencryption and decryption of the user data file 
 * which contains all of the protected password information .
 * 
 * This is done with secretkey encrpytion of which the secret key is NEVER TRANSMITTED
 * except when enrolling a new machine
 *
 * TODO AES 256 
 */
public class UserData {
	
	private SecretKey dataSecKey;
	private byte[] iv;
	
	//initilizes the instance by populating key variables.
	public boolean init(){
		return false;
	}

  public String decData(String thing) { return ""; }
  public String encData(String thing) { return ""; }
	
	
	
	/******************************************************************/
	
	
	
	
	//HYBRID DECRYPT?
	//initialization variables
//	private String pubkeyFile = "Not Initialized";
//	private String privkeyFile = "Not Initialized";
	private String sesskeyFile = "Not Initialized";
	private String ivFile = "Not Initialized";
	private String ciphertextFile = "Not Initialized";
	private String plaintextFile = "Not Initialized";
	//working variables
	private SecretKey symKey = null;
//	private PrivateKey rsaPrivKey = null;
//	private PublicKey rsaPubKey = null;
//	private String plaintext = "Not Initialized";
	private byte[] ivector;
	
	
	/*input_var requires exactly 6 inputs. it inputs the variable values from a 
	command line call to HybridDecrypt. 
	
	command call should be as follows: 
	java HybridDecrypt pubkey privkey sesskey iv ciphertext plaintext
	*/
    public void input_var(String[] inputs){
    	if (inputs.length != 6){
    		System.out.println("Sorry you messed up the # of inputs");
    	}else{
    		//TODO change this whole function
//    	this.pubkeyFile = inputs[0];
//    	this.privkeyFile = inputs[1];
    	this.sesskeyFile = inputs[2];
    	this.ivFile = inputs[3];
    	this.ciphertextFile = inputs[4];
    	this.plaintextFile = inputs[5];
    	}
    }
//    public String getpubkey(){return this.pubkeyFile;}
	
	
//	public Key getDecKey(){
//		//RSA Dec the session key and store in symKey
//		
//		
//		return this.rsaPrivKey;
//		
//	}
    
	/* decKey reads the shared private key from file and decodes it using 
	 * the private key which it also reads in from file and stores to rsaPrivKey.
	 * 
	 * MAY NOT BE NEEDED
	 */
	public void decKey() throws NoSuchAlgorithmException, NoSuchPaddingException, 
	        InvalidKeyException, IOException, InvalidKeySpecException, IllegalBlockSizeException, BadPaddingException {
//		//Read in rsa private key and store value
//		Cipher rsa = Cipher.getInstance("RSA/ECB/OAEPwithSHA-256AndMGF1padding");
//		File privKey = new File(this.privkeyFile);
//		byte[] key = new byte[(int)privKey.length()];
//		FileInputStream kInput = new FileInputStream(privKey);
//    	System.out.println("rsa key size is "+key.length);
//
//
//    	System.out.println("rsa key bytes read in "+kInput.read(key));
//		kInput.close();
//		
//		KeyFactory rsaKeyFact = KeyFactory.getInstance("RSA");
//		this.rsaPrivKey = rsaKeyFact.generatePrivate(new PKCS8EncodedKeySpec(key));
//		
//		rsa.init(Cipher.DECRYPT_MODE, rsaPrivKey);
		
		//read in session key, decrypt and store value
		
//		File sessKey = new File(this.sesskeyFile);
//		byte[] skey = new byte[(int) sessKey.length()];                      
//		FileInputStream sessInput = new FileInputStream(sessKey);
//		//CipherInputStream decKey = new CipherInputStream(sessInput, rsa); 
//    	System.out.println("sess key size is "+skey.length);
//
//    	System.out.println("sess key bytes read in "+sessInput.read(skey));     
//		sessInput.close();
//
//		skey = rsa.doFinal(skey);
//		
//		SecretKeyFactory skf = SecretKeyFactory.getInstance("AES");
//		this.symKey = skf.generateSecret(new SecretKeySpec(skey,"AES"));
//		
//    	System.out.println("session key : "+ this.symKey);
//
//    	System.out.println("session key has been aquired");
    	
    	//bring in the IV
    	File ivf = new File(this.ivFile);
		ivector = new byte[(int) ivf.length()];                      
		FileInputStream ivInput = new FileInputStream(ivf);
    	System.out.println("iv size is "+ ivf.length());

		System.out.println("initialization vector length is " +ivInput.read(ivector));
		ivInput.close();
		
		
		

		
	}
	
	
	
	
	
	/******************************************************************/
	
	
	
	
	
	//decrypts data recieved from the server for user use. 
	public void decCipherText() throws NoSuchAlgorithmException, 
	        NoSuchPaddingException, InvalidKeyException, IOException, InvalidAlgorithmParameterException{
		
		Cipher aes = Cipher.getInstance("AES/CBC/PKCS5Padding");
		System.out.println(aes.getAlgorithm());
		IvParameterSpec ips = new IvParameterSpec(ivector);
		aes.init(Cipher.DECRYPT_MODE, this.symKey, ips);
		FileInputStream ctextin = new FileInputStream(this.ciphertextFile);
		CipherInputStream cis = new CipherInputStream(ctextin, aes);
		FileOutputStream mout = new FileOutputStream(this.plaintextFile);
		
		byte[] block = new byte[this.ciphertextFile.length()];
		int x;
		while((x=cis.read(block)) != -1){
			mout.write(block, 0, x);
		}
		mout.close();
		cis.close();
		System.out.println("text has been decrypted to file "+ this.plaintextFile);
	}
	
	//Encrypts the data for sending back to server. 
	public void encCipherText(String text){
		//TODO
	}
	
	
	
	/******************************************************************/
	
	
	
	
	
	
	//Secret Key encryption  DES HACK CODE
	
	private byte[] desKey;
	private byte[] keyA;
	private byte[] keyB;
//	private byte[] iv = new byte[8] ;
	private int bsize;
	private String plaintext = "";
	private Cipher des; 
	private File crFile;
	private SecretKey secKey;
	
	public byte[] geta() {return this.keyA;	}
	public byte[] getb() {return this.keyB;	}


	//initializes variables for DES
	public void initVar(String cFile, String mFile) 
			throws FileNotFoundException, NoSuchAlgorithmException, NoSuchPaddingException{
													//classmate hinted to look at the bytes and look for patterns
//		desKey[0] = Byte.parseByte("01101001",2); //i
//		desKey[1] = Byte.parseByte("01101110",2); //n
//		desKey[2] = Byte.parseByte("01110011",2); //s
//		desKey[3] = Byte.parseByte("01110100",2); //t ....inct,ance,alls,etc...f
//		desKey[4] = Byte.parseByte("01101001",2); 
//		desKey[5] = Byte.parseByte("01101110",2);
////		desKey[6] = Byte.parseByte("01100011",2); //switch these two lines with the two below to simply decode the doc.
////		desKey[7] = Byte.parseByte("01110100",2);
//		desKey[6] = Byte.parseByte("00000000",2); //proof that program will work by brute force. it will also work if you 0 out all unknown didgets but just takes longer
//		desKey[7] = Byte.parseByte("00000000",2);
//		
//		one[7] = Byte.parseByte("00000001",2);
//		full[7] = Byte.parseByte("01111111",2);
//		

		
		for (byte b : desKey) {
		    System.out.println(Integer.toBinaryString(b & 255 | 256).substring(1));
		}
		this.crFile = new File(cFile);
		bsize = cFile.length();

		des = Cipher.getInstance("DES/CBC/PKCS5Padding");
		
	}
	//runs one round of a DES decryption
	public void runDES() 
			throws InvalidKeyException, IOException, InvalidAlgorithmParameterException{
		
		FileOutputStream mOut = new FileOutputStream(new File("m.txt"));
		IvParameterSpec ips = new IvParameterSpec(this.iv);
		des.init(Cipher.DECRYPT_MODE, this.secKey, ips);	
		FileInputStream cIn = new FileInputStream(this.crFile);
		CipherInputStream cis = new CipherInputStream(cIn, des);
		
		byte[] block = new byte[bsize];
		int x;
		try {
			while((x=cis.read(block)) != -1){
				mOut.write(block, 0, x);
			}
		} catch (Exception BadPaddingException) {
			
		}finally{
			cis.close();
			mOut.close();
		}
	}

}
