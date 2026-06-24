package Strategy;

import java.util.ArrayList;

import Common.Message;
import OCSFUtils.ConnectionToClient;
import Server.EchoServer;

/**
 * Strategy class responsible for adding an order to the waiting list.
 * The strategy receives order data from the client, sends it to the database,
 * and returns the result back to the client.
 */
public class AddToWaitingListStrategy implements MessageStrategy {

	/**
	 * Executes the add-to-waiting-list request.
	 * The method extracts the order data from the message, tries to add the order
	 * to the waiting list, and sends a success or failure response to the client.
	 *
	 * @param msg the message received from the client
	 * @param client the client connection that sent the message
	 * @param server the server that handles the request and provides database access
	 */
	@Override
	public void execute(Message msg, ConnectionToClient client, EchoServer server) {
		try {
			@SuppressWarnings("unchecked")
			ArrayList<String> orderData = (ArrayList<String>) msg.getData();

			boolean success = server.getDatabase().addOrderToWaitingList(orderData);

			client.sendToClient(new Message("ADD_TO_WAITING_LIST_RESULT", success));

		} catch (Exception e) {
			e.printStackTrace();
			try {
				client.sendToClient(new Message("ADD_TO_WAITING_LIST_RESULT", false));
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}
}