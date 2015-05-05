package jammed;

// TODO: Fix all of the exceptions!
// TODO: Soft code host name and port

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

/**
 * Provides a wrapper class abstraction for socket communications.
 * 
 * @Author Daniel Etter (dje67)
 * @version Alpha (1.0) 3.20.15
 */
public class Communication implements Runnable{
	private Socket socket = null;
	private ObjectInputStream rx = null;
	private ObjectOutputStream tx = null;

	/**
	 * Class Constructor
	 * 
	 * @throws SocketException
	 */
	public Communication(Socket socket) throws SocketException {
		this.socket = socket;
		
		try{
			this.tx = new ObjectOutputStream(this.socket.getOutputStream());
			this.rx = new ObjectInputStream(this.socket.getInputStream());
		}
		catch(IOException io){
			io.printStackTrace();
			throw new SocketException("Error creating a new communication handler");
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
			if (this.tx != null) this.tx.close();
			if (this.rx != null) this.rx.close();
			if (this.socket != null) this.socket.close();
		} catch (Exception e) {
			//e.printStackTrace();
			// Ignore since we don't care about this connection anymore anyway
			System.err.println("Error in Communication.close!");
		}
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		// ADD JELLY CODE HERE
		
	}

}
