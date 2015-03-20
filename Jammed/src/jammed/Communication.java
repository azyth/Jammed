package jammed;
/**
 * @Author Daniel Etter (dje67)
 */

// Wrapper for Socket things

// TODO: Fix all of the exceptions!

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

// Todo: Certs?

public class Communication {
    public enum Type{SERVER, CLIENT}

    // Hard Coded for now
    private String hostname = "localHost";
    private int port = 54309;

    private Communication.Type type = null;
    private ServerSocket serverSocket;// = null;
    private Socket socket;// = null;
    private ObjectInputStream rx;// = null;
    private ObjectOutputStream tx;// = null;
    private boolean dummy = true;

    public Communication(Communication.Type type) throws SocketException{
        try {
            this.type = type;

            if (this.type == Type.SERVER) {
            	// Key store  [Work in Progress]
            	
            	
            	
                // Create a Server
                ServerSocketFactory serverSocketFactory = SSLServerSocketFactory.getDefault();
                this.serverSocket = serverSocketFactory.createServerSocket(port);
                this.dummy = false;
                // Waits for new connection
                // this.socket = this.serverSocket.accept(); // Moving so we can block in Jelly
            }
            else if (this.type == Type.CLIENT) {
                // Create a Client
            	// Dummy
            }
            else{
            	throw new SocketException("Invalid type!");
            }
        }
        catch(RuntimeException re){
        	throw re;
        }
        catch(Exception e){
        	e.printStackTrace();
            throw new SocketException("Failure in Communicaiton constructor!");   
        }
    }
    
    public Communication(SSLContext context) throws SocketException{
    	try{
    		// Remove me eventually
    	}
    	catch(RuntimeException re){
    		throw re;
    	}
    	catch(Exception e){
    		e.printStackTrace();
    		throw new SocketException("Failure in Communication Server Constructor!");
    	}
    }
    
  public void connect() throws SocketException{
	  if(this.type != Type.CLIENT){
		  throw new SocketException("Can't connect from a server!");
	  }
	  try{
		  /*
		  // Code adapted from nakov.com/blog
		  // Create a trust manager that does not validate certificate chains
	      TrustManager[] trustAllCerts = new TrustManager[] {new X509TrustManager() {
	                public java.security.cert.X509Certificate[] getAcceptedIssuers() {
	                    return null;
	                }
	                public void checkClientTrusted(X509Certificate[] certs, String authType) {
	                }
	                public void checkServerTrusted(X509Certificate[] certs, String authType) {
	                }
	            }
	        };
	        // Install the all-trusting trust manager
	        SSLContext sc = SSLContext.getInstance("SSL");
	        sc.init(null, trustAllCerts, new java.security.SecureRandom());
	        
	        // Create all-trusting host name verifier
	        HostnameVerifier allHostsValid = new HostnameVerifier() {
	            public boolean verify(String hostname, SSLSession session) {
	                return true;
	            }
	        };
		  */
		  SocketFactory ClientSocketFactory = SSLSocketFactory.getDefault();
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
      }
      catch(RuntimeException re){
      	throw re;
      }
      catch(Exception e){
          throw new SocketException("Error in Communication.send!");
      }
  }

  public Request receive() throws SocketException{
      try{
          return (Request) this.rx.readObject();
      }
      catch(RuntimeException re){
      	throw re;
      }
      catch(Exception e){
            throw new SocketException("Error in Communication.receive!");
      }
  }

  public void close(){
      try{
    	  if(this.dummy == false){
    		  return; // Can't close a dummy
    	  }
    	  else if(this.type == Type.SERVER){
        	  this.tx.close();
        	  this.rx.close();
              this.serverSocket.close();
          }
          else if(this.type == Type.CLIENT){
              this.tx.close();
              this.rx.close();
              this.socket.close();
          }
          else{
        	  //throw new SocketException("Invalid type!"); // This should never be reached. _Ever_.
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
			  throw new SocketException("Invalid type in accept!"); // Should never see the light of day
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
			  throw new SocketException("Invalid type!");  // Still shouldn't ever be used
		  }
	  }
	  catch(Exception e){
		  e.printStackTrace();
		  throw new SocketException("Error in getPort()!");
	  }
  }

}
