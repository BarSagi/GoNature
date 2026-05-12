package Client;

import Common.Message;
import Entity.Order;
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

	    if (msg instanceof ArrayList<?>) { // first case: server sent ArrayList

	        ArrayList<ArrayList<String>> orders =
	                (ArrayList<ArrayList<String>>) msg;
	        
	        // ensure UI updates happen on javafx thread
	        Platform.runLater(new Runnable() {
	            @Override
	            public void run() {
	                ClientUI.controller.showOrders(orders);
	            }
	        });

	    } else if (msg instanceof Boolean) { // case 2: server sent a boolean variable

	        Boolean success = (Boolean) msg;
	        
	        // ensure UI updates happen on javafx thread
	        Platform.runLater(new Runnable() {
	            @Override
	            public void run() {
	                if (success) {
	                    ClientUI.controller.showSuccess("Order updated successfully");
	                } else {
	                    ClientUI.controller.showSuccess("Update failed");
	                }
	            }
	        });

	    } else { // handles unkown message
	        System.out.println("Unknown message: " + msg);
	    }
	}

	// sends a request to the server to return all orders
	public void getOrders() {
		try {
			sendToServer(new Message("GET_ORDERS", null));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	// sends a request to the server to update an order
	public void updateOrder(Order order) {
		try {
			ArrayList<Object> data = new ArrayList<>();
			data.add(order.getOrderNumber());
			data.add(order.getOrderDate().toString());
			data.add(order.getNumberOfVisitors());
			sendToServer(new Message("UPDATE_ORDER", data));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	// this method show us if the socket is closed
	@Override
	protected void connectionClosed() {
	    System.out.println("SOCKET CLOSED");
	}
	//this method will show us if the connection is interupted
	@Override
	protected void connectionException(Exception exception) {
	    System.out.println("CONNECTION ERROR");
	    exception.printStackTrace();
	}
}