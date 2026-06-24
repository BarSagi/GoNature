package Strategy;

import Common.Message;
import OCSFUtils.ConnectionToClient;
import Server.EchoServer;

/**
 * Strategy class responsible for rejecting a pending request.
 * The strategy receives a request ID from the client, rejects the request
 * in the database, and sends the result back to the client.
 */
public class RejectRequestStrategy implements MessageStrategy {

	/**
	 * Executes the reject request command.
	 * The method extracts the request ID from the message, updates the request status
	 * in the database, and sends a success or failure response back to the client.
	 *
	 * @param msg the message received from the client
	 * @param client the client connection that sent the message
	 * @param server the server that handles the request and provides database access
	 * @throws Exception if an error occurs while rejecting the request or sending the response
	 */
	@Override
	public void execute(Message msg, ConnectionToClient client, EchoServer server) throws Exception {
		int requestId = (int) msg.getData();
		boolean success = server.getDatabase().rejectRequest(requestId);
		client.sendToClient(new Message("REJECT_REQUEST_RESULT", success));
	}
}