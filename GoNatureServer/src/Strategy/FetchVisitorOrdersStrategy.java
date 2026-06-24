package Strategy;

import java.util.ArrayList;

import Common.Message;
import Common.Order;
import OCSFUtils.ConnectionToClient;
import Server.EchoServer;

/**
 * Strategy class responsible for fetching orders that belong to a specific visitor.
 * The strategy receives a visitor ID from the client, retrieves the visitor's orders
 * from the database, and sends the result back to the client.
 */
public class FetchVisitorOrdersStrategy implements MessageStrategy {

	/**
	 * Executes the fetch visitor orders command.
	 * The method extracts the visitor ID from the message, retrieves all matching
	 * orders from the database, and sends the order list back to the client.
	 *
	 * @param message the message received from the client
	 * @param client the client connection that sent the message
	 * @param server the server that handles the request and provides database access
	 * @throws Exception if an error occurs while fetching or sending the orders
	 */
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