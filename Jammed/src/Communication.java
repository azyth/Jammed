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

public class Communication {
    public enum Type{SERVER, CLIENT}

    // Hard Coded for now
    private String hostname = "localHost";
    private int port = 54309;

    private Communication.Type type = null;
    private ServerSocket serverSocket = null;
    private Socket socket = null;
    private ObjectInputStream rx = null;
    private ObjectOutputStream tx = null;

    public Communication(Communication.Type type) throws SocketException{
        try {
            this.type = type;

            if (this.type == Type.SERVER) {
                // Create a Server
                ServerSocketFactory serverSocketFactory = SSLServerSocketFactory.getDefault();
                this.serverSocket = serverSocketFactory.createServerSocket(port);

                // Waits for new connection
                // this.socket = this.serverSocket.accept(); // Moving so we can block in Jelly
            }
            else if (this.type == Type.CLIENT) {
                // Create a Client
                SocketFactory ClientSocketFactory = SSLSocketFactory.getDefault();
                Socket socket = ClientSocketFactory.createSocket(hostname, port);
                this.tx = new ObjectOutputStream(socket.getOutputStream());
                this.rx = new ObjectInputStream(socket.getInputStream());
            }
            else{
            	throw new SocketException("Invalid type!");
            }
        }
        catch(Exception e){
        	e.printStackTrace();
            throw new SocketException("Failure in Communicaiton constructor!");   
        }
    }

  public void send(Request thing) throws SocketException{
      try{
          this.tx.writeObject(thing);
      }
      catch(Exception e){
          throw new SocketException("Error in Communication.send!");
      }
  }

  public Request receive() throws SocketException{
      try{
          return (Request) this.rx.readObject();
      }
      catch(Exception e){
            throw new SocketException("Error in Communication.receive!");
      }
  }

  public void close() throws SocketException{
      try{
          if(this.type == Type.SERVER){
              this.serverSocket.close();
          }
          else if(this.type == Type.CLIENT){
              send(new TerminationReq(TerminationReq.Term.userReq)); // May or may not use? @DetterVT
          }
          else{
        	  throw new SocketException("Invalid type!"); // This should never be reached. _Ever_.
          }
      }
      catch(Exception e){
          throw new SocketException("Error in Communication.close!");
      }
  }
  
  public void accept() throws SocketException{
	  try{
		  if(this.type == Type.SERVER){
			  this.serverSocket.accept();
		  }
		  else if (this.type == Type.CLIENT){
			  throw new SocketException("Clients can't accept()!");
		  }
		  else{
			  throw new SocketException("Invalid type in accept!"); // Should never see the light of day
		  }
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
