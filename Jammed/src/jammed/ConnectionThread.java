package jammed;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ConnectionThread implements Runnable {
	
	private Socket socket;
	
	
	public ConnectionThread(Socket socket){
		// Init fields
		// Call using new Thread(new ConnectionThread()).start();
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		// Move Handle Connection from jelly to here?

	}

}
