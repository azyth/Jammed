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

public class Toast {
	
	private final int port = 54309;
	private final String host = "localhost";
	
	private ServerSocket serverSocket = null;
	
	public Toast(){
		try{
			ServerSocketFactory serverSocketFactory = SSLServerSocketFactory.getDefault();
	        this.serverSocket = serverSocketFactory.createServerSocket(port);
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
}
