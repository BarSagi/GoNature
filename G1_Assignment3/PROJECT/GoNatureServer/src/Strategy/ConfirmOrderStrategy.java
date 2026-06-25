package Strategy;

import Common.Message;
import OCSFUtils.ConnectionToClient;
import Server.EchoServer;

/**
 * Strategy class responsible for confirming an existing order.
 * The strategy receives an order ID from the client, confirms the order
 * in the database, and sends the confirmation result back to the client.
 */
public class ConfirmOrderStrategy implements MessageStrategy {

	/**
	 * Executes the confirm order command.
	 * The method extracts the order ID from the message, confirms the order
	 * in the database, and sends a success or failure response to the client.
	 *
	 * @param msg the message received from the client
	 * @param client the client connection that sent the message
	 * @param server the server that handles the request and provides database access
	 * @throws Exception if an error occurs while confirming the order or sending the response
	 */
	@Override
	public void execute(Message msg, ConnectionToClient client, EchoServer server) throws Exception {
		int orderId = (int) msg.getData();
		boolean success = server.getDatabase().confirmOrder(orderId);
		client.sendToClient(new Message("CONFIRM_ORDER_RESULT", success));
	}
}