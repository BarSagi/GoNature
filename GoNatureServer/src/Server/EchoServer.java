package Server;

import java.util.ArrayList;
import Common.Message;
import Database.DBController;
import GUI.ServerPortFrameController;
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
	@SuppressWarnings("unchecked")
	public void handleMessageFromClient(Object msg, ConnectionToClient client) {
		if (database == null) {
		    log("DB not initialized yet!");
		    return;
		}
		log("Message received: " + msg); // print the command
		try {
			Message message = (Message) msg;
			switch (message.getCommand()) { 
				case "GET_ORDERS": // this case will handle getting all orders from DB
					ArrayList<ArrayList<String>> orders = database.getAllOrders(); // this 2 dimensional array will save all orders data
					client.sendToClient(orders); // send all orders data to client
					break;
					
				case "UPDATE_ORDER": // this case will handle updating an order in DB
					ArrayList<Object> data = (ArrayList<Object>) message.getData();  // create a list of the data that needs to be updated
					int orderNumber = (int) data.get(0);
					String date = (String) data.get(1);
					int numberOfVisitors = (int) data.get(2);
					boolean success = database.updateOrder(orderNumber,  date,  numberOfVisitors); // call DB controller to update the order
					client.sendToClient(success); // send feedback to the client
					break;
					
				default:
					log("Unkown command: " + message.getCommand());
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
			log("Server listening for connections on port " + getPort());
			database = new DBController(this);
	}

	/**
	 * This method overrides the one in the superclass. Called when the server stops
	 * listening for connections.
	 */
	protected void serverStopped() {
		log("Server has stopped listening for connections.");
	}
	// this method will handle prints in side the GUI
	public void log(String msg) {
	    System.out.println(msg); // תמיד לוג בסיסי

	    if (ServerPortFrameController.instance != null) {
	        javafx.application.Platform.runLater(new Runnable() {
	            @Override
	            public void run() {
	                ServerPortFrameController.instance.log(msg);
	            }
	        });
	    }
	}
}