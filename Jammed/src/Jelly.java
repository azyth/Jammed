import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Paths;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

public class Jelly {
	private enum ServerState{INIT, ACCEPTING, AUTHENTICATING, AUTHORIZING, SESSION, DATA_UP, DATA_DOWN, LOG, TERM}
    //private int port;
    //private String addr;
	private static ServerState state;
	
	// TODO: Add multi threading
	// TODO: Add actual authentication

	public static void main(String[] args){
		if(!DB.initialize()){System.exit(1);}
		
        try{
        	Request req = null;
        	state = ServerState.INIT;
            Communication comm = new Communication(Communication.Type.SERVER);
            System.out.println("Jelly Server Created on port 54309");
            
            // Main Server Loop
            /*
             * Currently, server will accept any and all LoginReqs as valid
             */
            
            // Commented code for now, since there isn't currently a safe way to
    		// differentiate between request types (Login, Term, etc.)
            
            while(true){
            	if(state == ServerState.INIT){
            		state = ServerState.ACCEPTING;
            		comm.accept();
            		continue;
            	}
            	
            	if(state == ServerState.ACCEPTING){
            		// Connection established, since accept() blocks=
            		req = comm.receive();
            		// Check if req is a LoginRequest
            		// Send back a success message
            		// state = ServerState.AUTHENTICATING; // TO BE USED when we add this
            		// Send back success or failed
            		state = ServerState.SESSION;
            		
            		
            		// If any other request type
            		// Ignore
            		
            		
            		continue;
            	}
            	
            	if(state == ServerState.SESSION){
            		req = comm.receive();
            		
            		
            		// If Login Request
            		// Ignore
            		
            		// If Termination Request
            		// state = ServerState.TERM;
            		// Send success
            		// Close socket
            		
            		// If Data Upload Request
            		// state = ServerState.DATA_UP;
            		
            		// If Data Download Request
            		// state = ServerState.DATA_DOWN;
            		
            		// If Log Request
            		// state = ServerState.LOG;
            	}
            	
            	if(state == ServerState.DATA_DOWN){
            		
            	}
            	
            	if(state == ServerState.DATA_UP){
            		
            	}
            	
            	if(state == ServerState.DATA_DOWN){
            		
            	}
            	
            	if(state == ServerState.LOG){
            		
            	}
            	
            	if(state == ServerState.LOG){
            		
            	}
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }
	}
}
