#!/bin/bash
#clear
#java -Djavax.net.ssl.trustStore=serverkeystore.jks -Djavax.net.ssl.trustStorePassword=cs5430 jammed.Jammed
javac ./src/jammed/* -d .
echo "Compiled!"
