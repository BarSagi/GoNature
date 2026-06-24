package Strategy;

import Common.Message;
import OCSFUtils.ConnectionToClient;
import Server.EchoServer;

/**
 * Strategy class responsible for canceling an existing order.
 * The strategy receives an order ID from the client, tries to cancel the order
 * in the database, and sends the cancellation result back to the client.
 */
public class CancelOrderStrategy implements MessageStrategy {

	/**
	 * Executes the cancel order command.
	 * The method extracts the order ID from the message, cancels the order
	 * in the database, and sends a success or failure response to the client.
	 *
	 * @param msg the message received from the client
	 * @param client the client connection that sent the message
	 * @param server the server that handles the request and provides database access
	 * @throws Exception if an error occurs while canceling the order or sending the response
	 */
	@Override
	public void execute(Message msg, ConnectionToClient client, EchoServer server) throws Exception {
		try {
			int orderId = (int) msg.getData();

			boolean success = server.getDatabase().cancelOrder(orderId);

			client.sendToClient(new Message("ORDER_CANCEL_RESULT", success));

		} catch (Exception e) {
			e.printStackTrace();
			client.sendToClient(new Message("ORDER_CANCEL_RESULT", false));
		}
	}
}