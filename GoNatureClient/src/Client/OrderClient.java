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

	protected void handleMessageFromServer(Object msg) {

		if (msg instanceof ArrayList<?>) {

		    ArrayList<ArrayList<String>> orders = (ArrayList<ArrayList<String>>) msg;

			Platform.runLater(new Runnable() {

				@Override
				public void run() {

					ClientUI.controller.showOrders(orders);
				}
			});
		}
	}

	public void getOrders() {
		try {
			sendToServer(new Message("GET_ORDERS", null));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void updateOrder(Order order) {
		try {
			sendToServer(new Message("UPDATE_ORDER", order));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}