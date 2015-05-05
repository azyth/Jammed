package jammed;


import java.io.EOFException;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import javax.net.SocketFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.TrustManagerFactory;
import java.net.SocketException;

import javax.net.ssl.*;

import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.io.FileReader;
import java.io.BufferedReader;

public class ClientCommunication{
	private String hostname = "localhost";//"10.148.9.15"; //THIS IS THE ADRESS TO CONECT TO
	private int port = 54309;
	private String trustStoreFile = "5430ts.jks";
	private String trustStorePwd = "cs5430";
	private Socket socket = null;
	private ObjectInputStream rx = null;
	private ObjectOutputStream tx = null;
	private boolean dummy = true;

	public ClientCommunication() {
		BufferedReader read = null;
		
		// Client config settings
		// Line 0: host name
		// Line 1: port
		// Line 2: trust store file name
		// LIne 3: trust store password
		
		try{
			read = new BufferedReader(new FileReader("client.config"));
			
			this.hostname = read.readLine();
			this.port = Integer.parseInt(read.readLine());
			this.trustStoreFile = read.readLine();
			this.trustStorePwd = read.readLine();
		}
		catch(Exception e){
			e.printStackTrace();
		}
		finally{
			try{
				if(read!=null) read.close();
			}
			catch(Exception e){
				e.printStackTrace();
			}
		}
		
		// Read in configuration files for host name and port
		// Ensures dummy ClientCommunication object is returned
		this.dummy = true;
	}

	public void connect() throws SocketException{
		try {
			SSLContext context = SSLContext.getInstance("SSL");
			KeyStore ks = KeyStore.getInstance("JKS");
			TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509");
			FileInputStream ksin = new FileInputStream(this.trustStoreFile);
			ks.load(ksin, this.trustStorePwd.toCharArray());
			tmf.init(ks);
			context.init(null, tmf.getTrustManagers(), null);
			SocketFactory ClientSocketFactory = context.getSocketFactory();
			SSLSocket socket = (SSLSocket) ClientSocketFactory.createSocket(hostname, port);
			socket.startHandshake();
			this.tx = new ObjectOutputStream(socket.getOutputStream());
			this.rx = new ObjectInputStream(socket.getInputStream());
			this.dummy = false;
		} catch (GeneralSecurityException e) {
			throw new SocketException("Security exception in socket connect");
	    } catch (IOException e) {
	    	throw new SocketException("IOException in socket connect");
	    }
	}

	public void send(Request thing) throws SocketException {
		try {
			assert(this.tx!=null);
			this.tx.writeObject(thing);
			this.tx.flush();
		} catch (IOException e) {
		      throw new SocketException("IOException in socket send");
	    }
	}

	public Request receive() throws SocketException {
		while (true) {
			try {
				Request got = (Request) this.rx.readObject();
				return got;
			} catch (EOFException e) {
				// FIXME: Determine if this is a hack fix or properly ignoring
				// this exception.
				continue;
			} catch (IOException e) {
		        throw new SocketException("IOException in socket receive");
		      } catch (ClassNotFoundException e) {
		        throw new SocketException("ClassNotFoundException in socket receive");
		      }
		}
	}

	public void close() {
		if(this.dummy == true){
			// Cannot close a dummy socket!
			return;
		}
		try {
			if (this.tx != null) this.tx.close();
			if (this.rx != null) this.rx.close();
			if (this.socket != null) this.socket.close();
		} catch (IOException e) {
		      // if the connection errors out, we don't have to do anything...we don't
		      // want this connection anymore anyway
		    }
	}
	
	public boolean connected(){
		return !this.dummy;
	}
}
