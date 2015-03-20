import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

public class SSLServerTest {
	public static void main(String[] args){
		//sSystem.setProperty("javax.net.ssl.keyStore","serverkeystore.jks");
    	//System.setProperty("java.net.ssl.keyStorePassword", "cs5430");
		try {
	        SSLServerSocketFactory sslserversocketfactory =
	                (SSLServerSocketFactory) SSLServerSocketFactory.getDefault();
	        SSLServerSocket sslserversocket =
	                (SSLServerSocket) sslserversocketfactory.createServerSocket(9999);
	        SSLSocket sslsocket = (SSLSocket) sslserversocket.accept();

	        InputStream inputstream = sslsocket.getInputStream();
	        InputStreamReader inputstreamreader = new InputStreamReader(inputstream);
	        BufferedReader bufferedreader = new BufferedReader(inputstreamreader);

	        String string = null;
	        while ((string = bufferedreader.readLine()) != null) {
	            System.out.println(string);
	            System.out.flush();
	        }
	    } catch (Exception exception) {
	        exception.printStackTrace();
	    }
	}
}