package jammed;

import java.io.UnsupportedEncodingException;
import java.net.SocketException;

import jammed.Request.ErrorMessage;
import jammed.Request.EventType;
import jammed.UserDataReq.ReqType;

import java.io.EOFException;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.ServerSocket;

import javax.net.ServerSocketFactory;

import java.net.SocketException;

import javax.net.ssl.*;

import java.security.KeyStore;
import java.util.HashSet;
import java.io.FileReader;
import java.io.BufferedReader;

// TODO: Add proper system logging
// TODO: Add more verbose error handling and reporting

/**
 * The server class of the <code>Jammed</code> password management system.
 * 
 * @Author Daniel Etter (dje67)
 * @version Alpha (1.0) 3.20.15
 */
public class Jelly {

  public static void main(String[] args) throws SocketException {
	  
	  BufferedReader read = null;
	  ServerSocket serverSocket = null;

    if (!DB.initialize()) {
      System.err.println("DB Initialization Failure");
      System.exit(1);
    }
    
    // Network Information (default)
    int port = 54309;
    String keyStoreFile = "jammedkeystore.jks";
	String keyStorePwd = "cs5430";
    
    // **** CONFIG INITIALIZATION ****
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
		io.printStackTrace();
	}
	finally{
		try{
			if(read != null) read.close();
		}
		catch(IOException io){
			io.printStackTrace();
		}
	}
	
	// **** SSL INITIALIZATION ****
	try{
		SSLContext context = SSLContext.getInstance("SSL");
		KeyStore ks = KeyStore.getInstance("JKS");
		KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
		FileInputStream ksin = new FileInputStream(keyStoreFile);
		ks.load(ksin, keyStorePwd.toCharArray());
		kmf.init(ks, keyStorePwd.toCharArray());
		context.init(kmf.getKeyManagers(), null, null);
		ServerSocketFactory serverSocketFactory = context
				.getServerSocketFactory();
		serverSocket = serverSocketFactory.createServerSocket(port);
	}
	catch(Exception e){
		e.printStackTrace();
	}

	// Main Server Loop
    while (true) {
    	try{
    		Socket clientSocket = serverSocket.accept();
    		Communication client = new Communication(clientSocket);
    		//client.setTimeout(60);
    		new Thread(client).start();
    	}
    	catch(IOException io){
    		io.printStackTrace();
    	}
    }
  }

  
}
