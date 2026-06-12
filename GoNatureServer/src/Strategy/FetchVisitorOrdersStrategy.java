package Strategy;

import java.util.ArrayList;

import Common.Message;
import Common.Order;
import OCSFUtils.ConnectionToClient;
import Server.EchoServer;

public class FetchVisitorOrdersStrategy implements MessageStrategy {

	@Override
	public void execute(Message message, ConnectionToClient client, EchoServer server) throws Exception {
		String visitorId = (String) message.getData();
		ArrayList<Order> visitorOrders = server.getDatabase().getVisitorOrders(visitorId);

		Message response = new Message("RETURN_VISITOR_ORDERS", visitorOrders);

		try {
			client.sendToClient(response);
			server.log("[STRATEGY] Sent " + visitorOrders.size() + " visitor orders back to client.");
		} catch (Exception e) {
			server.log("[ERROR] Failed to send RETURN_VISITOR_ORDERS to client: " + e.getMessage());
			e.printStackTrace();
		}
	}
}
