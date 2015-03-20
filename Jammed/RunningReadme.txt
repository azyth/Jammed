Running:
Compile the stuff
Then from /bin/:
Jelly:java -Djavax.net.ssl.keyStore=../serverkeystore.jks -Djavax.net.ssl.keyStorePassword=cs5430 jammed.Jelly
Jammed: java -Djavax.net.ssl.trustStore=../serverkeystore.jks -Djavax.net.ssl.trustStorePassword=cs5430 jammed.Jammed