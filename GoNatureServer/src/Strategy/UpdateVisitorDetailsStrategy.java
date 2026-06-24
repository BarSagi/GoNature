package Strategy;

import java.util.ArrayList;

import Common.Message;
import OCSFUtils.ConnectionToClient;
import Server.EchoServer;

/**
 * Strategy class responsible for updating visitor details. The strategy
 * receives updated visitor information from the client, updates the visitor
 * details in the database, and sends the result back to the client.
 */
public class UpdateVisitorDetailsStrategy implements MessageStrategy {

	/**
	 * Executes the update visitor details command. The method extracts the visitor
	 * details from the message, updates the matching visitor record in the
	 * database, and sends a success or failure result back to the client.
	 *
	 * @param msg    the message received from the client
	 * @param client the client connection that sent the message
	 * @param server the server that handles the request and provides database
	 *               access
	 * @throws Exception if an error occurs while updating or sending the response
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void execute(Message msg, ConnectionToClient client, EchoServer server) throws Exception {
		try {
			ArrayList<String> data = (ArrayList<String>) msg.getData();

			String visitorId = data.get(0);
			String firstName = data.get(1);
			String lastName = data.get(2);
			String phone = data.get(3);
			String email = data.get(4);
			String creditCard = data.get(5);

			boolean success = server.getDatabase().updateVisitorDetails(visitorId, firstName, lastName, phone, email,
					creditCard);

			client.sendToClient(new Message("UPDATE_VISITOR_DETAILS_RESULT", success));

		} catch (Exception e) {
			e.printStackTrace();
			client.sendToClient(new Message("UPDATE_VISITOR_DETAILS_RESULT", false));
		}
	}
}