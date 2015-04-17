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
  public enum Type{SERVER, CLIENT}

  private String hostname = "localHost";
  private int port = 54309;

  private Communication.Type type = null;
  private ServerSocket serverSocket; // = null;
  private Socket socket; // = null;
  private ObjectInputStream rx; // = null;
  private ObjectOutputStream tx; // = null;
  private boolean dummy = true;

  /**
   * Class Constructor given <code>type</code>, either SERVER or CLIENT
   * 
   * @param type
   * @throws SocketException
   */
  public Communication(Communication.Type type) throws SocketException{
    try {
      
      if (type == Type.SERVER) {
    	  SSLContext context = SSLContext.getInstance("SSL");
    	  KeyStore ks = KeyStore.getInstance("JKS");
    	  KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
    	  FileInputStream ksin = new FileInputStream("serverkeystore.jks");
    	  ks.load(ksin, "cs5430".toCharArray());
    	  kmf.init(ks, "cs5430".toCharArray());
    	  context.init(kmf.getKeyManagers(), null, null);
    	  //System.setProperty("javax.net.ssl.keyStore", "serverkeystore.jks");
    	  //System.setProperty("javax.net.ssl.keyStorePassword", "cs5430");
    	  ServerSocketFactory serverSocketFactory = context.getServerSocketFactory();
    	  this.serverSocket = serverSocketFactory.createServerSocket(port);
    	  this.dummy = false;
      }
      else if (type == Type.CLIENT) {
    	//System.setProperty("javax.net.ssl.trustStore", "serverkeystore.jks");
      	//System.setProperty("javax.net.ssl.trustStorePassword", "cs5430");
        // Create a Client
        // Dummy object, call .connect() method to use
      }
      else{
        throw new SocketException("Invalid type!");
      }
      this.type = type;
    }
    catch(RuntimeException re){
      throw re;
    }
    catch(Exception e){
      e.printStackTrace();
      throw new SocketException("Failure in Communicaiton constructor!");   
    }
  }

  public void connect() throws SocketException{
    if(this.type != Type.CLIENT){
      throw new SocketException("Can't connect from a server!");
    }
    try{
    	SSLContext context = SSLContext.getInstance("SSL");
  	  KeyStore ks = KeyStore.getInstance("JKS");
  	  TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509");
  	  FileInputStream ksin = new FileInputStream("serverkeystore.jks");
  	  ks.load(ksin, "cs5430".toCharArray());
  	  tmf.init(ks);
  	  context.init(null, tmf.getTrustManagers(), null);
      SocketFactory ClientSocketFactory = context.getSocketFactory();
      SSLSocket socket = (SSLSocket) ClientSocketFactory.createSocket(hostname, port);
      socket.startHandshake(); //May not need?
      this.tx = new ObjectOutputStream(socket.getOutputStream());
      this.rx = new ObjectInputStream(socket.getInputStream());
      this.dummy = false;
    }
    catch(RuntimeException re){
      throw re;
    }
    catch(Exception e){
      e.printStackTrace();
      throw new SocketException("Failure in client construtor!");
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
      if(this.dummy == true){
        return; // Can't close a dummy
      }
      else if(this.type == Type.SERVER){
        //this.tx.close();
        //this.rx.close();
        if (this.serverSocket != null) this.serverSocket.close();
      }
      else if(this.type == Type.CLIENT){
        //this.tx.close();
        //this.rx.close();
        if(this.socket != null) this.socket.close();
    	  //this.socket.close();
      }
      else{
          //throw new SocketException("Invalid type!"); 
    	  //Impossible to reach
    	  // Commented out to avoid having close() throw an exception
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
      if(this.type == Type.SERVER){
        this.socket = this.serverSocket.accept();
        this.tx = new ObjectOutputStream(this.socket.getOutputStream());
        this.rx = new ObjectInputStream(this.socket.getInputStream());
      }
      else if (this.type == Type.CLIENT){
        throw new SocketException("Clients can't accept()!");
      }
      else{
        throw new SocketException("Invalid type in accept!");
        // Impossible to reach
      }
    }
    catch(RuntimeException re){
      throw re;
    }
    catch(Exception e){
      e.printStackTrace();
      throw new SocketException("Error in accept!!");
    }
  }

  public int getPort() throws SocketException{
    try{
      if(this.type == Type.SERVER){
        return this.serverSocket.getLocalPort();
      }
      if(this.type == Type.CLIENT){
        return this.socket.getLocalPort();
      }
      else{
        throw new SocketException("Invalid type!");
        // Impossible to reach
      }
    }
    catch(Exception e){
      e.printStackTrace();
      throw new SocketException("Error in getPort()!");
    }
  }

}
