README for jammed

4 MAY 2015

To run the Jammed application:(detailed commands below)
0. set server ip address or localhost(for testing) at ClientCommunication.hostname, line 23 ClientCommunication.java.
1. compile code 
2. start server
3. run client application
4. preserve your passwords



All commands should be run from the directory: Jammed/bin

To compile code: run compile.sh

To start a server: run server.sh

(Note: a server must be running before a client is opened.)
To start a command line client node: run client.sh

To start the client GUI app: run guiclient.sh





















******************************DELETE BELOW?
Running the code from the command line (as of 4/11/15):

File structure should look like:
Jammed/bin/
Jammed/src/jammed/*.java

Run the following the commands (# designates a comment)
cd Jammed/bin/
javac ../src/jammed/* -d .      #compiles the java files

# Open 2 terminal windows A and B
# From terminal window A:
java -Djavax.net.ssl.keyStore=../serverkeystore.jks -Djavax.net.ssl.keyStorePassword=cs5430 jammed.Jelly        # runs jelly (server)

From terminal window B:
java -Djavax.net.ssl.trustStore=../serverkeystore.jks -Djavax.net.ssl.trustStorePassword=cs5430 jammed.Jammed       # runs jammed, client


If Userdata and IV get out of sync and do not work/ produce errors, run createBaseData(username) in Userdata to create a new data and IV pair. 
  Produces: username_USERDATA.txt and username_IV.txt