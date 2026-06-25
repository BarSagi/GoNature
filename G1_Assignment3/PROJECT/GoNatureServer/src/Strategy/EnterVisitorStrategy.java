package Strategy;

import java.util.ArrayList;

import Common.Message;
import OCSFUtils.ConnectionToClient;
import Server.EchoServer;

/**
 * Strategy class responsible for handling visitor entrance.
 * The strategy receives a visitor ID from the client, checks the entrance
 * status in the database, and sends the result back to the client.
 */
public class EnterVisitorStrategy implements MessageStrategy {

	/**
	 * Executes the enter visitor command.
	 * The method extracts the visitor ID from the message, calls the database
	 * entrance logic, and sends the entrance result back to the client.
	 *
	 * @param message the message received from the client
	 * @param client the client connection that sent the message
	 * @param server the server that handles the request and provides database access
	 */
	@Override
	public void execute(Message message, ConnectionToClient client, EchoServer server) {
		try {
			@SuppressWarnings("unchecked")
			ArrayList<String> data = (ArrayList<String>) message.getData();

			String visitorId = data.get(0);

			String resultStatus = server.getDatabase().enterVisitor(visitorId);

			client.sendToClient(new Message("ENTER_VISITOR_RESULT", resultStatus));

		} catch (Exception e) {
			e.printStackTrace();
			try {
				client.sendToClient(new Message("ENTER_VISITOR_RESULT", "Server error occurred."));
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}
}