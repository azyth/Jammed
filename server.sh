#!/bin/bash
clear
echo 'Starting Jelly Server'
java -Djavax.net.ssl.keyStore=serverkeystore.jks -Djavax.net.ssl.keyStorePassword=cs5430 jammed.Jelly
