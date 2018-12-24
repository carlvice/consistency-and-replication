# consistency and replication

This program is consisting of two files. “Server.java” and “Client.java”

	1. Start the server
		a. Open command prompt.
		b. Compile the Server.java file using “javac Server.java” and then run it using “java Server”.
		c. It will start the Server and a GUI will pop up on the screen.
		d. This GUI consist of three components. One text area message will be displayed, one is a list which will display the connected clients and one poll button.
		e. As there is no client connected yet, hence these fields are empty.
		
	2. Start the client
		a. Open a new command prompt (Please make sure the command prompt in which the server is executed is not closed).
		b. Compile the Client.java file using “javac Client.java” and then run it using “java Client”.
		c. It will start the Client and a GUI will pop up on the screen.
		d. This GUI consists of several components. One is a text area in which client will print the operations and other are exit button and calculator input.
		
	3. You can repeat step 2 to create more clients just make sure each client is executed from a new command prompt window and all other command prompt windows are still running.
	
	4. As soon as you input name and click on enter Client connect to server, you can enter some operation as follows
		a. Operand followed by operand.
		b. Initial value is 1, so all operation will be done with initial value.
		c. If any value was previously stored in the log, the initial value will be fetched from log.
		
	5. As you click on “poll” button on server, operations from client will be send to server and server evaluate all those operations and write out the result to each client.
	
	6. To close the server, you can use close button of GUI window of server.
	
Notes

	1. If you start client without starting the server, the client will display the message saying, “server is offline”. You need to close the client and start the server and then again start the client.
	2. If server is running on a port already and you try to re-run the server on same port, the program will not let you do that. It will kill the new instance of server as soon as it begins.
	3. If you close the server while client(s) is connected. The client will react same as step 1 and you need to follow the same process again.
	4. To update the client list after client disconnect, Server will need to poll all the clients
	
REFERENCES: -
	1. JAVAFX REFERRED FROM JAVA THE COMPLETE REFERENCE 9TH EDITION BY HERBERT SCHILDT CHAPTER 34.
