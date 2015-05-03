package jammed;


// TODO: Fix all of the exceptions!
// TODO: Certificates
// TODO: Soft code host name and port

import java.io.EOFException;
import java.io.FileInputStream;
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

import java.security.KeyStore;
import java.security.cert.*;

/**
 * Provides a wrapper class abstraction for socket communications.
 * 
 * @Author Daniel Etter (dje67)
 * @version Alpha (1.0) 3.20.15
 */
public class Communication {

  private String hostname = "localHost";
  private int port = 54309;

  private ServerSocket serverSocket = null;
  private Socket socket = null;
  private ObjectInputStream rx = null;
  private ObjectOutputStream tx = null;

  /**
   * Class Constructor
   * 
   * @throws SocketException
   */
  public Communication() throws SocketException{
    try {
    	SSLContext context = SSLContext.getInstance("SSL");
  	  	KeyStore ks = KeyStore.getInstance("JKS");
  	  	KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
  	  	FileInputStream ksin = new FileInputStream("serverkeystore.jks");
  	  	ks.load(ksin, "cs5430".toCharArray());
  	  	kmf.init(ks, "cs5430".toCharArray());
  	  	context.init(kmf.getKeyManagers(), null, null);
  	  	ServerSocketFactory serverSocketFactory = context.getServerSocketFactory();
  	  	this.serverSocket = serverSocketFactory.createServerSocket(port);
    }
    catch(RuntimeException re){
      throw re;
    }
    catch(Exception e){
      e.printStackTrace();
      throw new SocketException("Failure in Communicaiton constructor!");   
    }
  }

  public void send(Request thing) throws SocketException{
    try{
      this.tx.writeObject(thing);
      this.tx.flush();
    }
    catch(RuntimeException re){
      throw re;
    }
    catch(Exception e){
      throw new SocketException("Error in Communication.send!");
    }
  }

  public Request receive() throws SocketException {
    while (true) {
      try{
        Request got = (Request) this.rx.readObject();
        return got;
      }
      catch (EOFException e) {
        //FIXME: Determine if this is a hack fix or properly ignoring this exception.
        continue;
      }
      catch(RuntimeException re){
        throw re;
      }
      catch(Exception e){
        System.out.println(e.getClass());
        e.printStackTrace();
        throw new SocketException("Error in Communication.receive!");
      }
    }
  }

  public void close(){
    try{
    	if(this.tx != null){
    		this.tx.close();
    	}
    	if(this.rx != null){
    		this.rx.close();
    	}
    	if(this.serverSocket != null){
    		this.serverSocket.close();
    	}
    }
    catch(RuntimeException re){
      throw re;
    }
    catch(Exception e){
      e.printStackTrace();
      System.out.println("Error in Communication.close!");
    }
  }

  public void accept() throws SocketException{
    try{
    	this.socket = this.serverSocket.accept();
    	this.tx = new ObjectOutputStream(this.socket.getOutputStream());
        this.rx = new ObjectInputStream(this.socket.getInputStream());
    }
    catch(RuntimeException re){
      throw re;
    }
    catch(Exception e){
      e.printStackTrace();
      throw new SocketException("Error in server side accept!!");
    }
  }

  public int getPort() throws SocketException{
    try{
    	return this.serverSocket.getLocalPort();
    }
    catch(Exception e){
      e.printStackTrace();
      throw new SocketException("Error in server side getPort()!");
    }
  }

}
