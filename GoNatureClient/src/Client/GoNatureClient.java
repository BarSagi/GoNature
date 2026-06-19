package Client;

import java.util.ArrayList;

import Common.Message;
import Common.VisitReportData;
import Entity.*;
import OCSFUtils.AbstractClient;
import Strategy.MessageStrategy;
import Strategy.StrategyFactory;
import javafx.application.Platform;

public class GoNatureClient extends AbstractClient {

	public static VisitReportData currentVisitReport;
	public static boolean awaitResponse = false;
	public static Employee currentEmployee;
	public static Visitor currentVisitor;

	public GoNatureClient(String host, int port) {
		super(host, port);
	}

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

	@Override
	protected void connectionEstablished() {
		try {
			String pcName = java.net.InetAddress.getLocalHost().getHostName(); // get the host name

			sendToServer(new Message("CONNECT", pcName));

		} catch (Exception e) {
			e.printStackTrace();
		}
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

	public void getOrders() {
		try {
			Message msg = new Message("GET_ORDERS", null);
			sendToServer(msg);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void updateOrder(int orderId, String visitDate, int visitorCount) {
		try {
			ArrayList<Object> data = new ArrayList<>();
			data.add(orderId);
			data.add(visitDate);
			data.add(visitorCount);

			Message msg = new Message("UPDATE_ORDER", data);
			sendToServer(msg);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void cancelOrder(int orderId) {
	    try {
	        Message msg = new Message("CANCEL_ORDER", orderId);
	        sendToServer(msg);
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}
	
	public void addToWaitingList(ArrayList<String> orderData) {
		try {
			Message msg = new Message("ADD_TO_WAITING_LIST", orderData);
			sendToServer(msg);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}