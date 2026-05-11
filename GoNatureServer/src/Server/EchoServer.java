package Server;

import java.io.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import Common.Message;
import Database.DBController;
import OCSFUtils.AbstractServer;
import OCSFUtils.ConnectionToClient;

/**
 * This class overrides some of the methods in the abstract superclass in order
 * to give more functionality to the server.
 *
 * @author Dr Timothy C. Lethbridge
 * @author Dr Robert Lagani&egrave;re
 * @author Fran&ccedil;ois B&eacute;langer
 * @author Paul Holden
 * @version July 2000
 */

public class EchoServer extends AbstractServer {
	// Class variables *************************************************

	/**
	 * The default port to listen on.
	 */
	// final public static int DEFAULT_PORT = 5555;

	// Constructors ****************************************************

	/**
	 * Constructs an instance of the echo server.
	 *
	 * @param port The port number to connect on.
	 * 
	 */
	private Connection conn;
	private DBController database;
	public EchoServer(int port) {
		super(port);
	}

	// Instance methods ************************************************

	/**
	 * This method handles any messages received from the client.
	 *
	 * @param msg    The message received from the client.
	 * @param client The connection from which the message originated.
	 * @param
	 */
	public void handleMessageFromClient(Object msg, ConnectionToClient client) {
		System.out.println("Message received: " + msg);
		try {
			Message message = (Message) msg;
			switch (message.getCommand()) {
				case "GET_ORDERS":
					ArrayList<ArrayList<String>> orders = database.getAllOrders();
					client.sendToClient(orders);
					break;
					
				case "UPDATE_ORDER":
					ArrayList<Object> data = (ArrayList<Object>) message.getData();
					int orderNum = (int) data.get(0);
					String date = (String) data.get(1);
					int numberOfVisitors = (int) data.get(2);
					
					boolean success = database.updateOrder(orderNum,  date,  numberOfVisitors);
					client.sendToClient(success);
					break;
					
				default:
					System.out.println("Unkown command: " + message.getCommand());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	/**
	 * This method overrides the one in the superclass. Called when the server
	 * starts listening for connections.
	 */
	protected void serverStarted() {
			System.out.println("Server listening for connections on port " + getPort());
			database = new DBController();
	}

	/**
	 * This method overrides the one in the superclass. Called when the server stops
	 * listening for connections.
	 */
	protected void serverStopped() {
		System.out.println("Server has stopped listening for connections.");
	}
}