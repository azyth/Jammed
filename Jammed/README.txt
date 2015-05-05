README for jammed
4 MAY 2015

To run the Jammed application:(detailed commands below)
0. set server and client config files (found at server/client.config) as follows:
    0.1: SERVER.CONFIG:
        <port>
        <keystore file name>
        <keystore file password>
    0.2: CLIENT.CONFIG:
        <server hostname>
        <server port>
        <truststore file name>
        <truststore file password>
    0.3: Please ensure *.config files follow these formats, failure to do so
may result in undefined behavior.

All commands should be run from the directory: Jammed/bin

1. compile code : run compile.sh

2. start server : run server.sh

    (Note: a server must be running before a client is opened.)

3. run client application

    To start a command line client node: run client.sh

    To start the client GUI app: run guiclient.sh

Once you run the client application, you are prompted to log in. To create a user, enter * in the username field and follow instructions given. Please note that all usernames must be alphanumeric. 





NOTE: The cryptography was written on a machine that used OpenJDK. It runs on a machine which has the following output for java -version:

java version "1.7.0_75"
OpenJDK Runtime Environment (IcedTea 2.5.4) (7u75-2.5.4-1~trusty1)
OpenJDK 64-Bit Server VM (build 24.75-b04, mixed mode)

Alternatively:
java version "1.7.0_75"
Java(TM) SE Runtime Environment (build 1.7.0_75-b13)
Java HotSpot(TM) 64-Bit Server VM (build 24.75-b04, mixed mode)

To run on java version 1.7.0_75 that was not downloaded from the open jdk website you have to paste the jars from Java Cryptography Extension (JCE) Unlimited Strength Jurisdiction Policy Files 7 Download into ${java.home}/jre/lib/security/