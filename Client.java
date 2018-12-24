
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.math.RoundingMode;
import java.net.Socket;
import java.text.DecimalFormat;
import java.util.ArrayList;

import javafx.application.*;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;
import javafx.stage.Stage;

public class Client extends Application {// Extends Application to implement JavaFX features

	clientHTTP chtp;// Instance of clientHTTP class

	TextArea ta = new TextArea();// TextArea to show output on GUI
	TextField clientName = new TextField("Enter name"); // Text field to get Client's name

	Button enter = new Button("Enter"); // Button to enter the name of client and it will start the client
	String localValue = "1"; // Local value for client. It is initialized to 1
	String value = "" + localValue; // Temporary variable for operations
	Label valueLabel = new Label("Local Value : " + localValue); // Label to display the local value;
	DecimalFormat df = new DecimalFormat("0.0000"); // to format result in proper form
	FileWriter fw;// To write the persistent log into file
	FileReader fr;// To read the persistent log from file
	BufferedReader br; // To work with file reader
	File file; // File to store persistent storage
	String name = "", tempOldVal = ""; // Name of client and temporary vairable for operation
	ArrayList<String> valueList = new ArrayList<>(); // ArrayList which will store the expression into array.

	public static void main(String[] args) {

		launch(args);// Starting GUI thread
		System.exit(0);// Terminate programs on using the close button of JavaFX window
	}

	@Override
	public void start(Stage stage) throws Exception { // GUI

		// Creating new instance of ServerHTTP to start the server
		// JavaFX referred from Java The Complete Reference 9th Edition by Herbert
		// Schildt Chapter 34.
		FlowPane rootNode = new FlowPane();
		stage.setTitle("Client");
		Scene scene = new Scene(rootNode, 640, 480);
		stage.setScene(scene);
		df.setRoundingMode(RoundingMode.UP);

		Button zero = new Button("0");
		Button one = new Button("1");
		Button two = new Button("2");
		Button three = new Button("3");
		Button four = new Button("4");
		Button five = new Button("5");
		Button six = new Button("6");
		Button seven = new Button("7");
		Button eight = new Button("8");
		Button nine = new Button("9");
		Button plus = new Button("+");
		Button minus = new Button("-");
		Button multiply = new Button("*");
		Button divide = new Button("/");
		Button dot = new Button(".");
		Button clear = new Button("C");
		Button equals = new Button("=");
		Button exitButton = new Button("Exit");
		exitButton.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) { // Exit button will close client as well aws file for storage
				try {
					if (fw != null) {
						fw.close();
					}
					if (fr != null) {
						fr.close();
					}

				} catch (IOException e) {
					// TODO Auto-generated catch block

				}

				System.exit(0);// Terminate as user click on "Exit" button
			}
		});
		plus.setOnAction(new EventHandler<ActionEvent>() { // Button for calculator addition operation

			@Override
			public void handle(ActionEvent event) {

				value += "+";
				ta.appendText("+");

			}
		});
		minus.setOnAction(new EventHandler<ActionEvent>() { // Button for calculator subtraction operation

			@Override
			public void handle(ActionEvent event) {
				value += "-";
				ta.appendText("-");
			}
		});
		multiply.setOnAction(new EventHandler<ActionEvent>() { // Button for calculator multiplication operation

			@Override
			public void handle(ActionEvent event) {
				value += "*";
				ta.appendText("*");
			}
		});
		divide.setOnAction(new EventHandler<ActionEvent>() { // Button for calculator division operation

			@Override
			public void handle(ActionEvent event) {
				value += "/";
				ta.appendText("/");
			}
		});
		equals.setOnAction(new EventHandler<ActionEvent>() { // Button for calculator to evaluate the value of the
																// expression entered by user.

			@Override
			public void handle(ActionEvent event) {
				String op = "";
				// This is the entire calculator logic
				try {

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
							valueList.set(index, "" + result);
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
					localValue = "" + value;
					String tempNewVal = ta.getText();

					fw = new FileWriter(file);
					fw.write(localValue + "\n");
					valueLabel.setText("Local Value : " + localValue);
					if (tempOldVal != "" && tempOldVal != null) {
						fw.write(tempOldVal + tempNewVal);
					} else {
						fw.write(tempNewVal);
					}

					fw.flush(); // write log into file

				} catch (NumberFormatException | IOException e) { // If input is not provided in correct format or
																	// result is NAN then user get below message on
																	// screen and client will reset

					ta.appendText("Input provided is not valid. Please provide input as operation followed by operand");
					value = "1";
					valueList.clear();
				}

				valueList.clear();

			}
		});
		one.setOnAction(new EventHandler<ActionEvent>() { // Button for calculator 1 digit

			@Override
			public void handle(ActionEvent event) {
				value += "1";
				ta.appendText("1");

			}
		});
		two.setOnAction(new EventHandler<ActionEvent>() { // Button for calculator 2 digit

			@Override
			public void handle(ActionEvent event) {
				value += "2";
				ta.appendText("2");

			}
		});
		three.setOnAction(new EventHandler<ActionEvent>() { // Button for calculator 3 digit

			@Override
			public void handle(ActionEvent event) {
				value += "3";
				ta.appendText("3");

			}
		});
		four.setOnAction(new EventHandler<ActionEvent>() { // Button for calculator 4 digit

			@Override
			public void handle(ActionEvent event) {
				value += "4";
				ta.appendText("4");

			}
		});
		five.setOnAction(new EventHandler<ActionEvent>() { // Button for calculator 5 digit

			@Override
			public void handle(ActionEvent event) {
				value += "5";
				ta.appendText("5");

			}
		});
		six.setOnAction(new EventHandler<ActionEvent>() { // Button for calculator 6 digit

			@Override
			public void handle(ActionEvent event) {
				value += "6";
				ta.appendText("6");

			}
		});
		seven.setOnAction(new EventHandler<ActionEvent>() { // Button for calculator 7 digit

			@Override
			public void handle(ActionEvent event) {
				value += "7";
				ta.appendText("7");

			}
		});
		eight.setOnAction(new EventHandler<ActionEvent>() { // Button for calculator 8 digit

			@Override
			public void handle(ActionEvent event) {
				value += "8";
				ta.appendText("8");

			}
		});
		nine.setOnAction(new EventHandler<ActionEvent>() { // Button for calculator 9 digit

			@Override
			public void handle(ActionEvent event) {
				value += "9";
				ta.appendText("9");

			}
		});
		zero.setOnAction(new EventHandler<ActionEvent>() { // Button for calculator 0 digit

			@Override
			public void handle(ActionEvent event) {
				value += "0";
				ta.appendText("0");

			}
		});

		dot.setOnAction(new EventHandler<ActionEvent>() { // Button for calculator . key

			@Override
			public void handle(ActionEvent event) {
				value += ".";
				ta.appendText(".");

			}
		});
		clear.setOnAction(new EventHandler<ActionEvent>() { // Button for calculator "clear" key

			@Override
			public void handle(ActionEvent event) {
				ta.clear();
				try {
					fr = new FileReader(file);
					br = new BufferedReader(fr);
					value = "" + br.readLine();
				} catch (IOException e) {
					// TODO Auto-generated catch block

				}

				Platform.runLater(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						valueLabel.setText("Local Value : " + value);

					}
				});
				valueList.clear();

			}
		});
		enter.setOnAction(new EventHandler<ActionEvent>() { // Enter button to input client's name

			@Override
			public void handle(ActionEvent event) {
				// TODO Auto-generated method stub
				name = clientName.getText();
				try {
					file = new File("" + name + ".txt");// log file which take client name and file will be
														// clientname.txt
					if (file.exists()) { // if file already exist , append to current file

						fr = new FileReader(file);
						br = new BufferedReader(fr);
						localValue = br.readLine();
						tempOldVal = br.readLine();
						value = "" + localValue;
						valueLabel.setText("Local Value : " + value);
						fw = new FileWriter(new File("" + name + ".txt"), true);

					} else { // if file not exist create a new file

						fw = new FileWriter(new File("" + name + ".txt"));
						fr = new FileReader(file);
						fw.write(localValue + "\n");
						fw.flush();
					}
					chtp = new clientHTTP(); // Creating new client thread

				} catch (IOException e) {
					// TODO Auto-generated catch block

				}
			}

		});

		rootNode.getChildren().add(clientName);
		rootNode.getChildren().add(enter);
		rootNode.getChildren().add(valueLabel);
		rootNode.getChildren().add(ta);
		rootNode.getChildren().add(exitButton);

		rootNode.getChildren().add(one);
		rootNode.getChildren().add(two);
		rootNode.getChildren().add(three);
		rootNode.getChildren().add(four);
		rootNode.getChildren().add(five);
		rootNode.getChildren().add(six);
		rootNode.getChildren().add(seven);
		rootNode.getChildren().add(eight);
		rootNode.getChildren().add(nine);
		rootNode.getChildren().add(zero);
		rootNode.getChildren().add(dot);

		rootNode.getChildren().add(plus);
		rootNode.getChildren().add(minus);
		rootNode.getChildren().add(multiply);
		rootNode.getChildren().add(divide);
		rootNode.getChildren().add(equals);

		rootNode.getChildren().add(clear);
		stage.show();
		// ABove lines are for GUI

	}

	class clientHTTP implements Runnable { // Implements Runnable for Thread

		Thread t = null; // Thread initialized to null
		Socket s = null; // Socket initialized to null

		public clientHTTP() {

			t = new Thread(this, "Client");// Assign a thread to current instance of ClientHTTP
			t.start();// Starting the thread. It calls void run()
		}

		@Override
		public void run() {

			try {
				s = new Socket("localhost", 1234);// Client socket will connect to localhost:1234

				final DataOutputStream dout = new DataOutputStream(s.getOutputStream()); // Fetching the output stream
																							// of Server
				final DataInputStream din = new DataInputStream(s.getInputStream()); // Fetching the input stream of
																						// Server
				dout.writeUTF(name); // Sending Client's name to server

				while (true) {
					din.readUTF(); // Reading server's POLL

					fr = new FileReader(file); // To read the log file as to see if any operation are not sent to server
												// in previous poll
					br = new BufferedReader(fr);//
					br.readLine();

					String expression = br.readLine();
					
				
					if (expression == null) // If there are no operations in file then just sent * 1 operation which is
											// a dummy operation
					{
						dout.writeUTF("*1");
					} else // Else send the operation from the log
					{
						dout.writeUTF(expression);

					}
					localValue = din.readUTF(); // Read the value provided by server
					if (!localValue.equals("POLL")) // Check if incorrect value is received
					{
						value = "" + localValue; // Set local value as value
					}
					fw = new FileWriter(file);
					fw.write(value); // Write that value into log
					fw.flush();
					Platform.runLater(new Runnable() { // To update GUI components

						@Override
						public void run() {
							// TODO Auto-generated method stub
							valueLabel.setText("Local Value : " + localValue); // display local value
							ta.clear();
							ta.appendText(
									"Local Value is updated from Server. All expression from log is uploaded and evaluated from Server"); // give
																																			// message

						}
					});

				}

			} catch (IOException e) {

				try {
					if (s != null)
						s.close();// Closing client socket as server is no more reachable or not started yet
					Platform.runLater(new Runnable() {

						@Override
						public void run() { // If server is offline

							ta.clear();
							ta.appendText("Server is Offline. Press Exit or Close the window\n");
							ta.appendText(
									"Any input which is not recieved by Server are saved in log and will be sent in next poll");
						}
					});

				} catch (IOException e1) {
					// TODO Auto-generated catch block

				}
			}
		}

	}

}
