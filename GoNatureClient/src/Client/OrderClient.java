package Client;

import Common.Message;
import Common.Order;
import javafx.application.Platform;
import OCSFUtils.AbstractClient;
import java.util.ArrayList;

public class OrderClient extends AbstractClient {

	public static ArrayList<Order> orders;

	public OrderClient(String host, int port) {
		super(host, port);
	}

	// this method will handle messages from server
	@SuppressWarnings("unchecked")
	@Override
	protected void handleMessageFromServer(Object msg) {

		if (!ClientUI.uiReady || ClientUI.clientController == null) {
			System.out.println("UI NOT READY - ignoring message: " + msg);
			return;
		}

		if (msg instanceof ArrayList<?>) { // first case: server sent ArrayList

			ArrayList<ArrayList<String>> orders = (ArrayList<ArrayList<String>>) msg;

			// ensure UI updates happen on javafx thread
			Platform.runLater(new Runnable() {
				@Override
				public void run() {
					ClientUI.clientController.showOrders(orders);
				}
			});

		} else if (msg instanceof Boolean) { // case 2: server sent a boolean variable

			Boolean success = (Boolean) msg;

			// ensure UI updates happen on javafx thread
			Platform.runLater(new Runnable() {
				@Override
				public void run() {
					if (success) {
						ClientUI.clientController.showSuccess("Order updated successfully");
					} else {
						ClientUI.clientController.showSuccess("Update failed");
					}
				}
			});

		} else { // handles unkown message
			System.out.println("Unknown message: " + msg);
		}
	}

	// sends a request to the server to return all orders
	public void getOrders() {

		// check if server is connected
		if (!isConnected()) {

			Platform.runLater(new Runnable() {

				@Override
				public void run() {

					if (ClientUI.clientController != null) {
						ClientUI.clientController.log("Server disconnected");
					}
				}
			});

			return;
		}

		try {

			sendToServer(new Message("GET_ORDERS", null));

		} catch (Exception e) {

			Platform.runLater(new Runnable() {

				@Override
				public void run() {

					if (ClientUI.clientController != null) {
						ClientUI.clientController.log("Failed to communicate with server");
					}
				}
			});
		}
	}

	// sends a request to the server to update an order
	public void updateOrder(Order order) {

		// check if server is connected
		if (!isConnected()) {

			Platform.runLater(new Runnable() {

				@Override
				public void run() {

					if (ClientUI.clientController != null) {
						ClientUI.clientController.log("Server disconnected");
					}
				}
			});

			return;
		}

		try {
			ArrayList<Object> data = new ArrayList<>();

			data.add(order.getOrderNumber());
			data.add(order.getOrderDate().toString());
			data.add(order.getNumberOfVisitors());

			sendToServer(new Message("UPDATE_ORDER", data));

		} catch (Exception e) {

			Platform.runLater(new Runnable() {

				@Override
				public void run() {

					if (ClientUI.clientController != null) {
						ClientUI.clientController.log("Failed to communicate with server");
					}
				}
			});
		}
	}

	// this method show us if the socket is closed
	@Override
	protected void connectionClosed() {
		log("CLIENT DISCONNECTED");
	}

	// this method will show us if the connection is interupted
	@Override
	protected void connectionException(Exception exception) {

		Platform.runLater(new Runnable() {

			@Override
			public void run() {

				if (ClientUI.connectionController != null) {
					ClientUI.connectionController.showErrorInGUI("Connection failed");
				}

				log("Connection interrupted by server");
			}
		});
	}

	public void disconnectClient() {
		try {
			if (isConnected()) {
				closeConnection();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void connectionEstablished() {

		try {

			String pcName = java.net.InetAddress.getLocalHost().getHostName(); // get the host name

			sendToServer(new Message("CONNECT", pcName));

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void log(String msg) {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				if (ClientUI.clientController != null) {
					ClientUI.clientController.log(msg);
				} else {
					System.out.println("UI not ready: " + msg);
				}
			}
		});
	}

}