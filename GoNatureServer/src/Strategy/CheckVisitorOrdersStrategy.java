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

		// =========================================================
		// NEW: Check if the visitor is already logged in elsewhere
		// =========================================================
		boolean loginSuccess = server.loginUser(visitorId, client);

		if (!loginSuccess) {
			server.log("[STRATEGY] Login denied for visitor " + visitorId + " - Already logged in.");
			try {
				client.sendToClient(new Message("ALREADY_LOGGED_IN", null));
			} catch (Exception e) {
				server.log("[ERROR] Failed to send ALREADY_LOGGED_IN to client: " + e.getMessage());
				e.printStackTrace();
			}
			return; // Stop execution, do not query the DB
		}

		// 2. Query the Database
		ArrayList<Order> visitorOrders = server.getDatabase().getVisitorOrders(visitorId);
		ArrayList<String> visitor = null; // Declare outside so we can use it later

		if (visitorOrders.size() > 0) {
			visitor = server.getDatabase().fetchVisitor(visitorId);
		}

		// =========================================================
		// 3. THE FIX: Combine both into an ArrayList of Objects!
		// =========================================================
		ArrayList<Object> combinedData = new ArrayList<>();
		combinedData.add(visitor); // Index 0: The Visitor Data (ArrayList<String> or null)
		combinedData.add(visitorOrders); // Index 1: The Orders Data (ArrayList<Order>)

		// 4. Package the combined result into your Message and send it back
		Message response = new Message("RETURN_VISITOR_ORDERS_AND_DATA", combinedData);

		try {
			client.sendToClient(response);
			server.log("[STRATEGY] Sent visitor data and " + visitorOrders.size() + " orders back to client.");
		} catch (Exception e) {
			server.log("[ERROR] Failed to send RETURN_VISITOR_ORDERS_AND_DATA to client: " + e.getMessage());
			e.printStackTrace();
		}
	}
}