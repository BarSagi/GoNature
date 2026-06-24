package Strategy;

import java.util.ArrayList;

import Common.Message;
import Common.Order;
import OCSFUtils.ConnectionToClient;
import Server.EchoServer;

/**
 * Strategy class responsible for fetching park orders.
 * This strategy is used by park workers and park managers
 * to view all orders related to a specific park.
 */
public class GetOrdersStrategy implements MessageStrategy {
	// Reusing for park worker and manager "view orders" screen 

	/**
	 * Executes the get orders command.
	 * The method extracts the park name from the message, retrieves all orders
	 * for that park from the database, and sends the result back to the client.
	 *
	 * @param message the message received from the client
	 * @param client the client connection that sent the message
	 * @param server the server that handles the request and provides database access
	 */
	@Override
	public void execute(Message message, ConnectionToClient client, EchoServer server) {
		
		try {
			String parkName = message.getData().toString();
			ArrayList<Order> orders = server.getDatabase().getAllParkOrders(parkName);

			client.sendToClient(new Message ("PARK_ORDERS_RESULT", orders));

		} catch (Exception e) {

			e.printStackTrace();

			try {
				client.sendToClient("ERROR: Could not fetch orders");
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}
}