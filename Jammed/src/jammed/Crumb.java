package jammed;

import java.io.EOFException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.ServerSocket;
import javax.net.SocketFactory;
import javax.net.ServerSocketFactory;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocketFactory;
import java.net.SocketException;
import javax.net.ssl.*;
import java.security.cert.*;

// A thread instance used to handle an incoming connection
public class Crumb implements Runnable{
	
	protected Socket client = null;
	private ObjectInputStream rx = null;
	private ObjectOutputStream tx = null;
	
	public Crumb(Socket client){
		this.client = client;
	}
	
	public void run(){
		try{
			// Main server loop?
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
}
