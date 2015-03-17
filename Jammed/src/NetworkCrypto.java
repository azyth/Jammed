//This may be completely irrelivant with SSL

//add import file paths for key names;
//server and client will call seperate function no need to streamline





	/*************************Possibly Depreciated*********************************/




import java.io.*;
import java.security.*;
import java.security.spec.*;

import javax.crypto.*;
import javax.crypto.spec.*;

/*Class NetworkCrypto 
 * Class in charge of encryption and decryption for packets which are passed to 
 * and from the user and the server.
 * Also contains functions fro verification and signing the same packets for 
 * Integrety
 * Implemented with Asymetric Crypto RSA 3072
 *  
 *  */

public class NetworkCrypto {
	private String[] userkeynames = {"serverEnc.txt","serverVer.txt","Dec.txt","Sig.txt"};
	private String[] serverkeynames = {"serverDec.txt","serverSig.txt","Enc.txt","Ver.txt"};
	//server RSA keys
	private PublicKey serverEncKey; //distributed to all users
	private PrivateKey serverDecKey; //only on server
	
	private PublicKey serverSigKey; //only on server
	private PrivateKey serverVerKey; //dist to all users
	//user RSA keys
	private PublicKey userEncKey; //sent to server, stored in user folder
	private PrivateKey userDecKey; //only on Local machine, must be copied to all verified machines
	
	private PublicKey userSigKey; //only on Local machine, must be copied to all verified machines
	private PrivateKey userVerKey; //sent to server, stored in user folder
	
	private Key[] keyRing;
//RSA 3072 SHA 256
	
	public boolean init(String username){
		//populate keynames with username
		//fill keyring with for loop reading in all keys needed server/user
		return false;
	}
	//enrollment is for a new user, or generating new keys for a user. 
	public KeyPair enrollRSA(){
		return null;
	}
	public KeyPair enrollDigCert(){
		return null;
	}
	
	
	/******************************************************************/
	
	
	
	//RSA KEY GENERATORS
	
	private PublicKey pubKey;
	private PrivateKey privKey;
	
	/*Generates a new RSA key pair and saves them to private veriables
	 */
	public boolean GenKeyPair() {
		try {
			KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
			kpg.initialize(3072);
			KeyPair keyPair = kpg.generateKeyPair();
			privKey = keyPair.getPrivate();
			pubKey = keyPair.getPublic();
			return true;
		} catch (NoSuchAlgorithmException NoSuchPaddingException) {
			return false;
		}
	}
	
	/*Saves the current Keypair to local files 
	  */
	public void ExportKeyPair(String pubFile, String privFile) 
	
			throws IOException{
		
		FileOutputStream pubos = new FileOutputStream(new File(pubFile));
		System.out.println("Public Key encoding: "+this.pubKey.getFormat());
		pubos.write(this.pubKey.getEncoded());
		FileOutputStream privos = new FileOutputStream(new File(privFile));
		System.out.println("Private Key encoding: "+this.privKey.getFormat());
		privos.write(this.privKey.getEncoded());
		
		pubos.close();
		privos.close();

	}
	
	/*Loads a key ring from stored files on the server to local variables
	 * 
	 */
	public boolean ImportServerKeyRing(String username){
		int x = 0;
		try {
			while(x < 3){
				File keyf = new File(this.serverkeynames[x]);
				byte[] key = new byte[(int)this.serverkeynames[x].length()];
				FileInputStream kInput = new FileInputStream(keyf);
				kInput.read(key);
				kInput.close();
				
				KeyFactory rsaKeyFact = KeyFactory.getInstance("RSA");
				if(x==0){//"serverDec.txt",
					this.serverDecKey = rsaKeyFact.generatePrivate(new PKCS8EncodedKeySpec(key));
				}
				else if(x==1){//"serverSig.txt",
					this.serverSigKey = rsaKeyFact.generatePublic(new PKCS8EncodedKeySpec(key));//TODO check on encodeings of these keys
				}
				else if (x==2){//"Enc.txt",
					this.userEncKey = rsaKeyFact.generatePublic(new PKCS8EncodedKeySpec(key));//TODO
				}
				else if(x==3){//"Ver.txt"
					this.userVerKey = rsaKeyFact.generatePrivate(new PKCS8EncodedKeySpec(key));//TODO
				}
				x++;
			}
		} catch (NoSuchAlgorithmException
				| InvalidKeySpecException | IOException e) {
			return false;
		}
		return true;
	}
	
	/*Loads a key ring from stored files on the server to local variables
	 * 
	 */
	public boolean ImportUserKeyRing(String username){
		int x = 0;
		try {
			while(x < 3){
				File keyf = new File(this.userkeynames[x]);
				byte[] key = new byte[(int)this.userkeynames[x].length()];
				FileInputStream kInput = new FileInputStream(keyf);
				kInput.read(key);
				kInput.close();
				
				KeyFactory rsaKeyFact = KeyFactory.getInstance("RSA");
				if(x==0){//"serverEnc.txt",
					this.serverEncKey = rsaKeyFact.generatePublic(new PKCS8EncodedKeySpec(key));
				}
				else if(x==1){//"serverVer.txt",
					this.serverVerKey = rsaKeyFact.generatePrivate(new PKCS8EncodedKeySpec(key));//TODO check on encodings of these keys
				}
				else if (x==2){//"Enc.txt",
					this.userDecKey = rsaKeyFact.generatePrivate(new PKCS8EncodedKeySpec(key));//TODO
				}
				else if(x==3){//"Ver.txt"
					this.userSigKey = rsaKeyFact.generatePublic(new PKCS8EncodedKeySpec(key));//TODO
				}
				x++;
			}
		} catch (NoSuchAlgorithmException
				| InvalidKeySpecException | IOException e) {
			return false;
		}
		return true;
	}
	
	//DOCUMENT VERIFICATION
	/*
	 * Signs a document and saves the signature to a file 
	 * 
	 * TODO change this to not write to file but instead return signature. 
	 */
	public void signDoc(String privkeyfn, String filename) 
			throws InvalidKeySpecException, NoSuchAlgorithmException, IOException, InvalidKeyException, SignatureException{
		PrivateKey privkey;
		
		//read key in
		File pKey = new File(privkeyfn);
		byte[] key = new byte[(int)pKey.length()];
		FileInputStream kInput = new FileInputStream(pKey);
		kInput.read(key);
		kInput.close();
		
		//initialize key
		KeyFactory rsaKeyFact = KeyFactory.getInstance("RSA");
		privkey = rsaKeyFact.generatePrivate(new PKCS8EncodedKeySpec(key));
		
		//initialize readers/writers
		Signature rsa = Signature.getInstance("SHA512withRSA");
		rsa.initSign(privkey);
		FileInputStream fis = new FileInputStream(new File(filename));
		BufferedInputStream bufin = new BufferedInputStream(fis);
		FileOutputStream fos = new FileOutputStream(new File(filename+".sig"));
		
		//sign doc and write signature to a file = filename+".sig"
		byte[] block = new byte[filename.length()];                             
		int len;
		while ((len = bufin.read(block)) >= 0) {
		    rsa.update(block, 0, len);
		};
		fis.close();
		bufin.close();
		
		byte[] realSig = rsa.sign();
		fos.write(realSig);
		fos.close();

	}
	/*
	 * Verifys a signature against a given file. 
	 */
	public void verifyDoc(String pubkeyfn, String signaturefn, String filename) 
			throws NoSuchAlgorithmException, InvalidKeySpecException, IOException, InvalidKeyException, SignatureException{
		PublicKey pubkey;
		
		File pKey = new File(pubkeyfn);
		byte[] key = new byte[(int)pKey.length()];
		FileInputStream kInput = new FileInputStream(pKey);
		kInput.read(key);
		kInput.close();
		
		KeyFactory rsaKeyFact = KeyFactory.getInstance("RSA");
		pubkey = rsaKeyFact.generatePublic(new X509EncodedKeySpec(key));
		
		Signature rsa = Signature.getInstance("SHA512withRSA");
		rsa.initVerify(pubkey);
		
		FileInputStream fis = new FileInputStream(new File(filename));
		BufferedInputStream bufinf = new BufferedInputStream(fis);
		FileInputStream sis = new FileInputStream(new File(signaturefn));
		BufferedInputStream bufins = new BufferedInputStream(sis);
		
		//inputsignature
		byte[] signature = new byte[bufins.available()]; 
		bufins.read(signature);
		bufins.close();
		
		//input file
		byte[] block = new byte[filename.length()];
		int len;
		while (bufinf.available() != 0) {
		    len = bufinf.read(block);
		    rsa.update(block, 0, len);
		};
		fis.close();
		bufinf.close();
		
		//certify
		boolean success = rsa.verify(signature);
		
		//output success / failures
        if (success){
        	System.out.println("signature was succsessfully verified! you are still one step ahead of them all.");
        }else{
        	System.out.println("signature verification FAILED!, SUSPECT EVERYONE!");
        }
	}
	public String decryptRequest(String packet) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException{
		Cipher rsa = Cipher.getInstance("RSA/ECB/OAEPwithSHA-256AndMGF1padding");
		
		rsa.init(Cipher.DECRYPT_MODE, rsaPrivKey);
		return null;
	}
	public String encReq(String request){
		return null;
	}
	
	
	/******************************************************************/
	
	
	
	//HYBRID DECRYPT?
	//initialization variables
	private String pubkeyFile = "Not Initialized";
	private String privkeyFile = "Not Initialized";
	private String sesskeyFile = "Not Initialized";
	private String ivFile = "Not Initialized";
	private String ciphertextFile = "Not Initialized";
	private String plaintextFile = "Not Initialized";
	//working variables
	private SecretKey symKey = null;
	private PrivateKey rsaPrivKey = null;
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
    	this.pubkeyFile = inputs[0];
    	this.privkeyFile = inputs[1];
    	this.sesskeyFile = inputs[2];
    	this.ivFile = inputs[3];
    	this.ciphertextFile = inputs[4];
    	this.plaintextFile = inputs[5];
    	}
    }
    public String getpubkey(){return this.pubkeyFile;}
	
	
	public Key getDecKey(){
		//RSA Dec the session key and store in symKey
		
		
		return this.rsaPrivKey;
		
	}
	/* decKey reads the shared private key from file and decodes it using 
	 * the private key which it also reads in from file and stores to rsaPrivKey.
	 * 
	 */
	public void decKey() throws NoSuchAlgorithmException, NoSuchPaddingException, 
	        InvalidKeyException, IOException, InvalidKeySpecException, IllegalBlockSizeException, BadPaddingException {
		//Read in rsa private key and store value
		Cipher rsa = Cipher.getInstance("RSA/ECB/OAEPwithSHA-256AndMGF1padding");
		File privKey = new File(this.privkeyFile);
		byte[] key = new byte[(int)privKey.length()];
		FileInputStream kInput = new FileInputStream(privKey);
    	System.out.println("rsa key size is "+key.length);


    	System.out.println("rsa key bytes read in "+kInput.read(key));
		kInput.close();
		
		KeyFactory rsaKeyFact = KeyFactory.getInstance("RSA");
		this.rsaPrivKey = rsaKeyFact.generatePrivate(new PKCS8EncodedKeySpec(key));
		
		rsa.init(Cipher.DECRYPT_MODE, rsaPrivKey);
		
		//read in session key, decrypt and store value
		
		File sessKey = new File(this.sesskeyFile);
		byte[] skey = new byte[(int) sessKey.length()];                      
		FileInputStream sessInput = new FileInputStream(sessKey);
		//CipherInputStream decKey = new CipherInputStream(sessInput, rsa); 
    	System.out.println("sess key size is "+skey.length);

    	System.out.println("sess key bytes read in "+sessInput.read(skey));     
		sessInput.close();

		skey = rsa.doFinal(skey);
		
		SecretKeyFactory skf = SecretKeyFactory.getInstance("AES");
		this.symKey = skf.generateSecret(new SecretKeySpec(skey,"AES"));
		
    	System.out.println("session key : "+ this.symKey);

    	System.out.println("session key has been aquired");
    	//bring in the IV
    	File ivf = new File(this.ivFile);
		ivector = new byte[(int) ivf.length()];                      
		FileInputStream ivInput = new FileInputStream(ivf);
    	System.out.println("iv size is "+ ivf.length());

		System.out.println("initialization vector length is " +ivInput.read(ivector));
		ivInput.close();
		
		
		

		
	}

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
	public void encCipherText(){
		
	}
	

}
