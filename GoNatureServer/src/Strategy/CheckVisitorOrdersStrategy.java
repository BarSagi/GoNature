package Strategy;

import Common.Message;
import Common.Order;
import OCSFUtils.ConnectionToClient;
import Server.EchoServer;

import java.util.ArrayList;

public class CheckVisitorOrdersStrategy implements MessageStrategy {

	@Override
	public void execute(Message message, ConnectionToClient client, EchoServer server) {
		// 1. Extract the visitor ID from the message
		String visitorId = (String) message.getData();
		server.log("[STRATEGY] Checking database for orders belonging to ID: " + visitorId);

		// 2. Query the Database (Now expecting a list of string lists)
		ArrayList<Order> visitorOrders = server.getDatabase().getVisitorOrders(visitorId);
		System.out.println(visitorOrders);

		// 3. Package the result into your Message and send it back
		Message response = new Message("RETURN_VISITOR_ORDERS", visitorOrders);

		try {
			client.sendToClient(response);
			server.log("[STRATEGY] Sent " + visitorOrders.size() + " orders back to client.");
		} catch (Exception e) {
			server.log("[ERROR] Failed to send RETURN_VISITOR_ORDERS to client: " + e.getMessage());
			e.printStackTrace();
		}
	}
}