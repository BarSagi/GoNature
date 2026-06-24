package Strategy;

import java.util.ArrayList;

import Common.Message;
import Common.Order;
import OCSFUtils.ConnectionToClient;
import Server.EchoServer;

/**
 * Strategy class responsible for fetching all orders for a specific park.
 * The strategy receives a park name from the client, retrieves the matching
 * orders from the database, and sends them back to the client.
 */
public class GetParkOrdersStrategy implements MessageStrategy {

	/**
	 * Executes the get park orders command.
	 * The method extracts the park name from the message, fetches all orders
	 * related to that park, and sends the order list back to the client.
	 *
	 * @param msg the message received from the client
	 * @param client the client connection that sent the message
	 * @param server the server that handles the request and provides database access
	 * @throws Exception if an error occurs while fetching or sending the orders
	 */
	@Override
	public void execute(Message msg, ConnectionToClient client, EchoServer server) throws Exception {
		String parkName = (String) msg.getData();

		ArrayList<Order> orders = server.getDatabase().getOrdersByParkName(parkName);

		client.sendToClient(new Message("RETURN_PARK_ORDERS", orders));
	}
}