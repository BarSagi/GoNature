package Strategy;

import java.util.ArrayList;

import Common.Message;
import OCSFUtils.ConnectionToClient;
import Server.EchoServer;

/**
 * Strategy class responsible for fetching visitor details by visitor ID.
 * The strategy receives a visitor ID from the client, retrieves the visitor
 * information from the database, and sends the result back to the client.
 */
public class GetVisitorDetailsStrategy implements MessageStrategy {

	/**
	 * Executes the get visitor details command.
	 * The method extracts the visitor ID from the message, fetches the matching
	 * visitor details from the database, and sends the result back to the client.
	 *
	 * @param msg the message received from the client
	 * @param client the client connection that sent the message
	 * @param server the server that handles the request and provides database access
	 * @throws Exception if an error occurs while fetching the visitor details or sending the response
	 */
	@Override
	public void execute(Message msg, ConnectionToClient client, EchoServer server) throws Exception {
		try {
			String visitorId = (String) msg.getData();

			ArrayList<String> visitorDetails = server.getDatabase().fetchVisitor(visitorId);

			client.sendToClient(new Message("VISITOR_DETAILS_RESULT", visitorDetails));

		} catch (Exception e) {
			e.printStackTrace();
			client.sendToClient(new Message("VISITOR_DETAILS_RESULT", null));
		}
	}
}