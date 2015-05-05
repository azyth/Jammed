package jammed;

// TODO: Fix all of the exceptions!
// TODO: Soft code host name and port

import java.io.EOFException;
import java.io.FileInputStream;
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

/**
 * Provides a wrapper class abstraction for socket communications.
 * 
 * @Author Daniel Etter (dje67)
 * @version Alpha (1.0) 3.20.15
 */
public class Communication {

	private int port = 54309;
	private String keyStoreFile = "jammedkeystore.jks";
	private String keyStorePwd = "cs5430";

	private ServerSocket serverSocket = null;
	private Socket socket = null;
	private ObjectInputStream rx = null;
	private ObjectOutputStream tx = null;
	//private HashSet<String> currentUsers;

	/**
	 * Class Constructor
	 * 
	 * @throws SocketException
	 */
	public Communication() throws SocketException {
		BufferedReader read = null;
		// Server config settings
		// Line 0: port
		// Line 1: KeyStoreFile
		// Line 2: KeyStorePwd
		
		//currentUsers = new HashSet<String>();

		try {
			SSLContext context = SSLContext.getInstance("SSL");
			KeyStore ks = KeyStore.getInstance("JKS");
			KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
			FileInputStream ksin = new FileInputStream(this.keyStoreFile);
			ks.load(ksin, this.keyStorePwd.toCharArray());
			kmf.init(ks, this.keyStorePwd.toCharArray());
			context.init(kmf.getKeyManagers(), null, null);
			ServerSocketFactory serverSocketFactory = context
					.getServerSocketFactory();
			this.serverSocket = serverSocketFactory.createServerSocket(port);
			
			read = new BufferedReader(new FileReader("client.config"));
			this.port = Integer.parseInt(read.readLine());
			this.keyStoreFile = read.readLine();
			this.keyStorePwd = read.readLine();
			
			
		} catch (Exception e) {
			e.printStackTrace();
			throw new SocketException("Failure in Communicaiton constructor!");
		}
		finally{
			try{
				if(read!=null) read.close();
			}
			catch(Exception e){
				e.printStackTrace();
			}
		}
	}

	public void send(Request thing) throws SocketException {
		try {
			this.tx.writeObject(thing);
			this.tx.flush();
		} catch (Exception e) {
			System.err.println(e.getClass());
			e.printStackTrace();
			throw new SocketException("Error in Communication.send!");
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
			} catch (Exception e) {
				System.err.println(e.getClass());
				e.printStackTrace();
				throw new SocketException("Error in Communication.receive!");
			}
		}
	}

	public void close() {
		try {
			if (this.tx != null)
				this.tx.close();
			if (this.rx != null)
				this.rx.close();
			if (this.serverSocket != null)
				this.serverSocket.close();
		} catch (RuntimeException re) {
			throw re;
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("Error in Communication.close!");
		}
	}

	public void accept() throws SocketException {
		try {
			this.socket = this.serverSocket.accept();
			// this.socket.setSoTimeout(60000); // 60s timeout
			// For timeouts, set timeoutsEnable = true/false
			// on timeout, catch exception, close thread and remove user name
			// from logged in
			this.tx = new ObjectOutputStream(this.socket.getOutputStream());
			this.rx = new ObjectInputStream(this.socket.getInputStream());
		} catch (Exception e) {
			e.printStackTrace();
			throw new SocketException("Error in server side accept!!");
		}
	}

	public int getPort() throws SocketException {
		try {
			return this.serverSocket.getLocalPort();
		} catch (Exception e) {
			e.printStackTrace();
			throw new SocketException("Error in server side getPort()!");
		}
	}

}
