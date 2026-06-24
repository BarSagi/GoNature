package Strategy;

import Common.Message;
import OCSFUtils.ConnectionToClient;
import Server.EchoServer;

/**
 * Strategy class responsible for fetching a visitor's email address.
 * The strategy receives a visitor ID from the client, retrieves the matching
 * email address from the database, and sends the result back to the client.
 */
public class GetVisitorEmailStrategy implements MessageStrategy {

	/**
	 * Executes the get visitor email command.
	 * The method extracts the visitor ID from the message, fetches the visitor's
	 * email address from the database, and sends the result back to the client.
	 *
	 * @param message the message received from the client
	 * @param client the client connection that sent the message
	 * @param server the server that handles the request and provides database access
	 * @throws Exception if an error occurs while fetching or sending the email address
	 */
	@Override
	public void execute(Message message, ConnectionToClient client, EchoServer server) throws Exception {

		String visitorId = (String) message.getData();

		try {
			String email = server.getDatabase().getVisitorEmailById(visitorId);

			client.sendToClient(new Message("VISITOR_EMAIL_RESULT", email));

		} catch (Exception e) {
			e.printStackTrace();

			client.sendToClient(new Message("VISITOR_EMAIL_RESULT", null));
		}
	}
}