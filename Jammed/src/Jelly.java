//import Java.

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

public class Jelly {
	// Dan
	// Server-side stuffs
    private int port;
    private String addr;

    private void run(){
        while(true){

        }
    }

    public Jelly(){

    }

	public static void main(String[] args){
		int port = 5430;
		System.out.println("Jelly Server Created on port "+port);

        Jelly server = new Jelly();
        server.run();
	}
}
