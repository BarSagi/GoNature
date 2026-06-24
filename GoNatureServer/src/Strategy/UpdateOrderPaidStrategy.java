package Strategy;

import Common.Message;
import OCSFUtils.ConnectionToClient;
import Server.EchoServer;

/**
 * Strategy class responsible for updating an order payment status.
 * The strategy receives an order ID from the client, marks the order as paid
 * in the database, and sends the result back to the client.
 */
public class UpdateOrderPaidStrategy implements MessageStrategy {

	/**
	 * Executes the update order paid command.
	 * The method extracts the order ID from the message, updates the payment status
	 * in the database, and sends a success or failure message back to the client.
	 *
	 * @param message the message received from the client
	 * @param client the client connection that sent the message
	 * @param server the server that handles the request and provides database access
	 */
	@Override
	public void execute(Message message, ConnectionToClient client, EchoServer server) {
		try {
			String orderIdStr = (String) message.getData();
			int orderId = Integer.parseInt(orderIdStr);

			boolean success = server.getDatabase().updateOrderPaidStatus(orderId);

			if (success) {
				client.sendToClient(new Message("ENTER_VISITOR_RESULT", "Payment Successful"));
			} else {
				client.sendToClient(new Message("ENTER_VISITOR_RESULT", "Failed to update payment in DB."));
			}

		} catch (Exception e) {
			e.printStackTrace();
			try {
				client.sendToClient(new Message("ENTER_VISITOR_RESULT", "Server error during payment update."));
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}
}