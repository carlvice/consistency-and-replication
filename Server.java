
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.math.RoundingMode;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.DecimalFormat;
import java.util.ArrayList;

import javafx.application.*;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.layout.FlowPane;

import javafx.stage.Stage;

public class Server extends Application { // Extends Application to implement JavaFX features

	ServerSocket ss; // Server Socket which will act as port to listen incoming request
	ServerHTTP shtp; // SeverHTTP class reference which will be used later in the program
	String sharedValue = "1"; // shared value for Server
	static ArrayList<Server.ServerHTTP.ClientThread> clientArrayList = new ArrayList<>(); // List of all the clients
																							// connected at this time
	TextArea ta = new TextArea(); // Text area to display text on server
	Button poll = new Button("POLL"); // Button for Server POLL
	Label valueLabel = new Label("Shared Value : " + sharedValue); // Label to display shared variable

	static ListView<String> clientList = new ListView<String>();// List the connected client
	static ArrayList<Integer> waitList = new ArrayList<>();// List to store integers provided by client

	public static void main(String[] args) {

		launch(args); // Starting GUI thread
		System.exit(0);// Terminate programs on using the close button of JavaFX window

	}

	String calculator(String value) {// Calculator function which take input as expression , calculate the result and
										// return it
		DecimalFormat df = new DecimalFormat("0.0000");// To display the result in proper format
		df.setRoundingMode(RoundingMode.CEILING); // To display the result in proper format
		ArrayList<String> valueList = new ArrayList<>(); // ArrayList to store the expression in array.
		String op = "";
		int index = 0;
		int x = 0;
		
		while (x < value.length()) {
			if ((value.charAt(x) == '+' || value.charAt(x) == '-' || value.charAt(x) == '*'
					|| value.charAt(x) == '/') && value.charAt(x + 1) == '-') {
				valueList.add("" + value.charAt(x));
				x++;
				String number = "" + value.charAt(x);
				x++;
				while (x < value.length() && value.charAt(x) != '+' && value.charAt(x) != '-'
						&& value.charAt(x) != '/' && value.charAt(x) != '*') {
					number += value.charAt(x);
					x++;
				}
				valueList.add(number);
			} else if (x == 0 && value.charAt(x) == '-') {
				String number = "" + value.charAt(x);
				x++;
				while (x < value.length() && value.charAt(x) != '+' && value.charAt(x) != '-'
						&& value.charAt(x) != '/' && value.charAt(x) != '*') {
					number += value.charAt(x);
					x++;
				}
				valueList.add(number);
			} else if ((value.charAt(x) == '+' || value.charAt(x) == '-' || value.charAt(x) == '*'
					|| value.charAt(x) == '/')) {
				valueList.add("" + value.charAt(x));
				x++;
			} else {
				String number = "" + value.charAt(x);
				x++;
				while (x < value.length() && value.charAt(x) != '+' && value.charAt(x) != '-'
						&& value.charAt(x) != '/' && value.charAt(x) != '*') {
					number += value.charAt(x);
					x++;
				}
				valueList.add(number);
				
			}

		}

		while (valueList.size() > 1) {

			if (valueList.contains("/")) {
				op = "/";
				index = valueList.indexOf("/");
			} else if (valueList.contains("*")) {
				op = "*";
				index = valueList.indexOf("*");
			} else if (valueList.contains("-")) {
				op = "-";
				index = valueList.indexOf("-");
			} else if (valueList.contains("+")) {
				op = "+";
				index = valueList.indexOf("+");

			}

			if (index > 0) {
				String strOperandLeft = valueList.get(index - 1);
				String strOperandRight = valueList.get(index + 1);

				double operandLeft = Double.parseDouble(strOperandLeft);
				double operandRight = Double.parseDouble(strOperandRight);
				double result = 0.0;
				if (op.equals("/")) {
					result = operandLeft / operandRight;

				} else if (op.equals("*")) {
					result = operandLeft * operandRight;

				} else if (op.equals("+")) {
					result = operandLeft + operandRight;

				} else if (op.equals("-")) {
					result = operandLeft - operandRight;

				}
				valueList.set(index, ""+result);
				valueList.remove(index - 1);
				valueList.remove(index);

			}

		}
		value = "";

		value += String.format("%.4f", Double.parseDouble(df.format(Double.parseDouble(valueList.get(0))))); // To
																												// get
																												// result
																												// in
																												// 4
																												// digit
																												// precision
																												// round
																												// off
		valueList.clear();
		
		return value; // return result.

	}

	@Override
	public void start(Stage stage) throws Exception {

		shtp = new ServerHTTP(); // Creating new instance of ServerHTTP to start the server
		// JavaFX referred from Java The Complete Reference 9th Edition by Herbert
		// Schildt Chapter 34.
		FlowPane rootNode = new FlowPane();
		stage.setTitle("Server");
		Scene scene = new Scene(rootNode, 1024, 768);
		stage.setScene(scene);
		clientList.getItems().add("Connected Clients");
		rootNode.getChildren().add(valueLabel);
		rootNode.getChildren().add(ta);
		rootNode.getChildren().add(clientList);
		rootNode.getChildren().add(poll);

		poll.setOnAction(new EventHandler<ActionEvent>() { // Process to follow when poll button is clicked

			@Override
			public void handle(ActionEvent event) {

				String result = "";
				String operations = "";
				for (Server.ServerHTTP.ClientThread ct : clientArrayList) { // Traverse through list of each client
																			// connected to server right now.
					operations += ct.getOperations(); // get operation expression from each client
					

				}
				if (!operations.equals("")) { // if operations is not empty
					
					result = calculator(sharedValue + operations);
					// calculate result of expression using calculator
																	// function
				}
				sharedValue="";
				sharedValue = ""+ result; // updating the result to shared value
				
				valueLabel.setText("Shared Value : " + sharedValue); // updating label of shared value
				for (Server.ServerHTTP.ClientThread ct : clientArrayList) { // Traverse through list of each client
																			// connected to server right now.
					ct.writeToClient(result); // Send the value of result to all the connected client as to bring the
												// system to consisten phase
				}
				result = ""; // clearing result
			}
		});

		stage.show(); // Show GUI components

	}

	public class ServerHTTP implements Runnable { // Implements Runnable for Thread

		Thread t = null;
		String response = "";

		public ServerHTTP() {
			t = new Thread(this, "Server"); // Assign a thread to current instance of ServerHTTP
			t.start();// Starting the thread. It calls void run()
		}

		public void run() {

			try {
				ss = new ServerSocket(1234); // Create a Server Socket at port (1234)
			} catch (Exception e) {

				System.exit(0); // If port is busy or server is already running then it will terminate the new
								// instance.
			}
			while (true) {
				try {
					Socket s = ss.accept(); // Accepting incoming connection
					ClientThread serv = new ClientThread(s);// Fork a thread for new client
					clientArrayList.add(serv);// Add new client to list of connected client

				} catch (Exception e) {
				}

			}
		}

		public class ClientThread implements Runnable {

			final Socket s; // Socket for connected client
			final Thread t;// Thread forked to connected client

			DataOutputStream dout = null; // output stream for ClientThread
			DataInputStream din = null; // input stream for ClientThread
			String clientName = ""; // Client 's name

			ClientThread(Socket s) throws IOException {
				this.s = s; // Assigning the provided socket to class variable socket.
				t = new Thread(this, "Client Thread");// Assigning thread to connected client
				dout = new DataOutputStream(s.getOutputStream());// Fetching the output stream of client
				din = new DataInputStream(s.getInputStream()); // Fetching the input stream of client
				t.start();// Starting the thread

			}

			String getOperations() { // function to get the operation expression from client
				String clientExpression = ""; // Operation expression received from client
				String result = ""; // result of the expression
				try {
					dout.writeUTF("POLL"); // Telling client that Server is polling
					clientExpression = din.readUTF();// Receiving operation expression from client
					
					if (!clientExpression.equals("")) { // Operations is not empty
						result += clientExpression; // appending client operations to result
						
					}

				} catch (Exception e) { // If client get disconnected , exception will occur.
					// TODO: handle exception
					if (clientArrayList.contains(this)) // if connected client list have this client
					{
						clientArrayList.remove(this); // remove this client from connected client list
					}
					Platform.runLater(new Runnable() { // to update GUI elements

						@Override
						public void run() {
							// TODO Auto-generated method stub
							Server.clientList.getItems().remove(clientName); // remove the name of client from GUI list

						}
					});
				}

				return result; // return the result which is all operations expression
			}

			void writeToClient(String result) { // function to write the "result" parameters to clients
				try {

					dout.writeUTF(result); // write the "result" parameters to clients
				}

				catch (Exception e) { // If client get disconnected , exception will occur.

					if (clientArrayList.contains(this)) // if connected client list have this client
					{
						clientArrayList.remove(this); // remove this client from connected client list
					}
					Platform.runLater(new Runnable() { // to update GUI elements

						@Override
						public void run() {
							// TODO Auto-generated method stub
							Server.clientList.getItems().remove(clientName); // remove the name of client from GUI list

						}
					});

				}
			}

			@Override
			public void run() { // Run function of client thread
				try {

					clientName = din.readUTF();// Fetching the request message sent by the Client

					Platform.runLater(new Runnable() { // Update the GUI components

						@Override
						public void run() {
							// TODO Auto-generated method stub
							clientList.getItems().add(clientName); // Add the client name to GUI List

						}
					});

				} catch (Exception e) {
				}

			}
		}
	}
}
