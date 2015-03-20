#!/bin/bash
clear
echo 'Starting Jammed Client'
java -Djavax.net.ssl.trustStore=serverkeystore.jks -Djavax.net.ssl.trustStorePassword=cs5430 jammed.Jammed
