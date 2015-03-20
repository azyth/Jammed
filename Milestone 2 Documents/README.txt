README.txt

NOTE: We wrote the cryptography on a machine that used OpenJDK. It runs (at
least) on a machine which has the following output for java -version:


java version "1.7.0_75"
OpenJDK Runtime Environment (IcedTea 2.5.4) (7u75-2.5.4-1~trusty1)
OpenJDK 64-Bit Server VM (build 24.75-b04, mixed mode)


Running:
from folder Jammed (one level up from src). Note that you have to run these both
from the same machine (currently we communicate to localhost) and you must start
the server before the client.


javac src/jammed/* -d bin
cd bin
./client.sh (or) ./server.sh


Once you run the client application, you are prompted to log in. We have
provided one existing user data file for use, which you can access with the
username “guest” (the password does not matter, as there is no authentication in
the alpha version).


In the user interface, there are currently only two commands: add and exit.
Exit closes the client application. Add will prompt you to enter a website,
username, and password triple for storage.


Finally, our server currently only handles one request. As such, you must
restart the server application every time you restart the client.
