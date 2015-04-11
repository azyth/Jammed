README for jammed


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