package jammed;


import java.io.EOFException;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import javax.net.SocketFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;

import java.net.SocketException;

import javax.net.ssl.*;

import java.security.KeyStore;

public class ClientCommunication{
	private String hostname = "localhost";
	private int port = 54309;

	private Socket socket = null;
	private ObjectInputStream rx = null;
	private ObjectOutputStream tx = null;
	private boolean dummy = true;

	public ClientCommunication() {
			// Ensures dummy ClientCommunication object is returned
			this.dummy = true;

	}

	public void connect() {
		try {
			SSLContext context = SSLContext.getInstance("SSL");
			KeyStore ks = KeyStore.getInstance("JKS");
			TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509");
			FileInputStream ksin = new FileInputStream("5430ts.jks");
			ks.load(ksin, "cs5430".toCharArray());
			tmf.init(ks);
			context.init(null, tmf.getTrustManagers(), null);
			//SSLSocketFactory ClientSocketFactory = context.getSocketFactory();
			SocketFactory ClientSocketFactory = context.getSocketFactory();
			SSLSocket socket = (SSLSocket) ClientSocketFactory.createSocket(hostname, port);
			socket.startHandshake();
			this.tx = new ObjectOutputStream(socket.getOutputStream());
			this.rx = new ObjectInputStream(socket.getInputStream());
			this.dummy = false;
		} catch (RuntimeException re) {
			throw re;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void send(Request thing) throws SocketException {
		try {
			assert(this.tx!=null);
			this.tx.writeObject(thing);
			this.tx.flush();
		} catch (RuntimeException re) {
			throw re;
		} catch (Exception e) {
			throw new SocketException("Error in ClientCommunication.send!");
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
			} catch (RuntimeException re) {
				throw re;
			} catch (Exception e) {
				System.out.println(e.getClass());
				e.printStackTrace();
				throw new SocketException("Error in Communication.receive!");
			}
		}
	}

	public void close() {
		if(this.dummy == true){
			// Cannot close a dummy socket!
			return;
		}
		try {
			if (this.tx != null) {
				this.tx.close();
			}
			if (this.rx != null) {
				this.rx.close();
			}
			if (this.socket != null) {
				this.socket.close();
			}
		} catch (RuntimeException re) {
			throw re;
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Error in ClientCommunication.close!");
		}
	}
}
