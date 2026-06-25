package Client;

import Common.Message;
import Common.VisitReportData;
import Entity.*;
import OCSFUtils.AbstractClient;
import Strategy.MessageStrategy;
import Strategy.StrategyFactory;
import javafx.application.Platform;

/**
 * The primary client communication class for the GoNature application. Extends
 * the OCSF AbstractClient to handle network connections, sending requests to
 * the server, and receiving server responses via the Strategy Pattern.
 */
public class GoNatureClient extends AbstractClient {

	/**
	 * Temporarily holds the visit report data retrieved from the server.
	 */
	public static VisitReportData currentVisitReport;

	/**
	 * Flag used to pause client execution while waiting for a server response.
	 */
	public static boolean awaitResponse = false;

	/**
	 * The currently logged-in Employee object. Null if no employee is logged in.
	 */
	public static Employee currentEmployee;

	/**
	 * The currently active Visitor object. Null if no visitor is logged in.
	 */
	public static Visitor currentVisitor;

	/**
	 * Constructs the GoNatureClient. * @param host The IP address of the server to
	 * connect to.
	 * 
	 * @param port The port number of the server.
	 */
	public GoNatureClient(String host, int port) {
		super(host, port);
	}

	/**
	 * Handles messages received from the server. Extracts the command from the
	 * message and uses the StrategyFactory to execute the appropriate behavior.
	 * * @param msg The message object received from the server.
	 */
	@Override
	protected void handleMessageFromServer(Object msg) {
		if (msg instanceof Message) {
			Message message = (Message) msg;
			String command = message.getCommand();

			// 1. Ask the factory for the appropriate strategy
			MessageStrategy strategy = StrategyFactory.getStrategy(command);

			// 2. Execute it if it exists
			if (strategy != null) {
				strategy.execute(message);
			} else {
				System.out.println("Client: Received an unknown command from server: " + command);
			}
		}
	}

	/**
	 * Hook method called each time an exception is thrown by the client's thread
	 * that is waiting for messages from the server. This indicates a dropped or
	 * interrupted connection. * @param exception The exception that caused the
	 * connection to drop.
	 */
	// this method will show us if the connection is interupted
	@Override
	protected void connectionException(Exception exception) {

		Platform.runLater(new Runnable() {

			@Override
			public void run() {

				if (ClientUI.connectionController != null) {
					ClientUI.connectionController.showErrorInGUI("Connection failed");
				}
			}
		});
	}

	/**
	 * Hook method called after a connection has been established with the server.
	 * Sends an initial "CONNECT" message to the server containing the client's PC
	 * name.
	 */
	@Override
	protected void connectionEstablished() {
		try {
			String pcName = java.net.InetAddress.getLocalHost().getHostName(); // get the host name

			ClientUI.send(new Message("CONNECT", pcName));

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Closes the connection to the server if the client is currently connected.
	 */
	public void disconnectClient() {
		try {
			if (isConnected()) {
				closeConnection();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * A==================================================DONT THINK THIS IS USED
	 * Sends a request to the server to fetch all orders.
	 */
	/*
	 * public void getOrders() { try { Message msg = new Message("GET_ORDERS",
	 * null); ClientUI.send(msg); } catch (Exception e) { e.printStackTrace(); } }
	 * 
	 * /** ===============================DONT THINK THIS IS USED Sends a request to
	 * the server to update a specific order's visit date and visitor count.
	 * * @param orderId The ID of the order to update.
	 * 
	 * @param visitDate The new date for the visit.
	 * 
	 * @param visitorCount The new count of visitors.
	 */
	/*
	 * public void updateOrder(int orderId, String visitDate, int visitorCount) {
	 * try { ArrayList<Object> data = new ArrayList<>(); data.add(orderId);
	 * data.add(visitDate); data.add(visitorCount);
	 * 
	 * Message msg = new Message("UPDATE_ORDER", data); ClientUI.send(msg); } catch
	 * (Exception e) { e.printStackTrace(); } }
	 * 
	 * /**===============================DONT THINK THIS IS USED Sends a request to
	 * the server to cancel a specific order. * @param orderId The ID of the order
	 * to cancel.
	 */
	/*
	 * public void cancelOrder(int orderId) { try { Message msg = new
	 * Message("CANCEL_ORDER", orderId); ClientUI.send(msg); } catch (Exception e) {
	 * e.printStackTrace(); } }
	 * 
	 * /**===============================DONT THINK THIS IS USED Sends a request to
	 * the server to add a new order to the waiting list. * @param orderData An
	 * ArrayList containing the details of the order to be waitlisted.
	 */
	/*
	 * public void addToWaitingList(ArrayList<String> orderData) { try { Message msg
	 * = new Message("ADD_TO_WAITING_LIST", orderData); ClientUI.send(msg); } catch
	 * (Exception e) { e.printStackTrace(); } }
	 */

}