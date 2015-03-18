/**
 * @Author Daniel Etter
 */

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
    private int port = 5430;

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
                this.socket = this.serverSocket.accept();
            }
            if (this.type == Type.CLIENT) {
                // Create a Client
                SocketFactory ClientSocketFactory = SSLSocketFactory.getDefault();
                Socket socket = ClientSocketFactory.createSocket(hostname, port);

                this.tx = new ObjectOutputStream(socket.getOutputStream());
                this.rx = new ObjectInputStream(socket.getInputStream());
            }
        }
        catch(Exception e){
            throw new SocketException("Error!");
        }
    }

  public void send(Request thing) throws SocketException{
      try{
          this.tx.writeObject(thing);
      }
      catch(Exception e){
          throw new SocketException("Error!");
      }
  }

  public Request receive() throws SocketException{
      try{
          return (Request) this.rx.readObject();
      }
      catch(Exception e){
            throw new SocketException("Error!");
      }
  }

  public void close() throws SocketException{
      try{
          if(this.type == Type.SERVER){
              this.socket.close();
          }
          if(this.type == Type.CLIENT){
              send(new TerminationReq(TerminationReq.Term.userReq));
          }
      }
      catch(Exception e){
          throw new SocketException("Error!");
      }
  }

}
