package jammed;

import java.net.SocketException;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.ServerSocket;
import javax.net.ServerSocketFactory;
import javax.net.ssl.*;
import java.security.KeyStore;
import java.io.FileReader;
import java.io.BufferedReader;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;


/**
 * The server class of the <code>Jammed</code> password management system.
 * 
 * @version Final (3.0) 5.5.15
 */
public class Jelly {

  public static void main(String[] args) throws SocketException {
	  
	  BufferedReader read = null;
	  ServerSocket serverSocket = null;

    if (!DB.initialize()) {
      System.err.println("DB Initialization Failure");
      System.exit(1);
    }
    
    /* Default Network Information */
    int port = 54309;
    String keyStoreFile = "jammedkeystore.jks";
	String keyStorePwd = "cs5430";
    
	// Server config settings
	// Line 0: port
	// Line 1: KeyStoreFile
	// Line 2: KeyStorePwd
	try{
		read = new BufferedReader(new FileReader("server.config"));
		port = Integer.parseInt(read.readLine());
		keyStoreFile = read.readLine();
		keyStorePwd = read.readLine();
	}
	catch(IOException io){
		System.err.println("Error in intializing server configuration files");
		io.printStackTrace();
	}
	finally{
		try{
			if(read != null) read.close();
		}
		catch(IOException io){
			System.err.println("Error in intializing server configuration files");
			io.printStackTrace();
		}
	}
	
	/* SSL Configuration */
	try{
		SSLContext context = SSLContext.getInstance("SSL");
		KeyStore ks = KeyStore.getInstance("JKS");
		KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
		FileInputStream ksin = new FileInputStream(keyStoreFile);
		ks.load(ksin, keyStorePwd.toCharArray());
		kmf.init(ks, keyStorePwd.toCharArray());
		context.init(kmf.getKeyManagers(), null, null);
		ServerSocketFactory serverSocketFactory = context.getServerSocketFactory();
		serverSocket = serverSocketFactory.createServerSocket(port);
	}
	catch(Exception e){
		System.err.println("Error in intializing server SSL configuration");
		e.printStackTrace();
	}
	
	/* Logged In Users Set Setup */
	Set<String> login = Collections.synchronizedSet(new HashSet<String>());

	/* Main Server Loop */
    while (true) {
    	try{
    		Socket clientSocket = serverSocket.accept();
    		Communication client = new Communication(clientSocket, login);
    		new Thread(client).start();
    	}
    	catch(IOException io){
    		io.printStackTrace();
    	}
    }
  }
}
