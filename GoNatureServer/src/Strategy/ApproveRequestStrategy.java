package Strategy;

import Common.Message;
import OCSFUtils.ConnectionToClient;
import Server.EchoServer;

/**
 * Strategy class responsible for approving a manager request.
 * The strategy receives a request ID from the client, approves it in the database,
 * and sends the approval result back to the client.
 */
public class ApproveRequestStrategy implements MessageStrategy {

	/**
	 * Executes the approve request command.
	 * The method extracts the request ID from the message, updates the request status
	 * in the database, and sends the result back to the client.
	 *
	 * @param msg the message received from the client
	 * @param client the client connection that sent the message
	 * @param server the server that handles the request and provides database access
	 * @throws Exception if an error occurs while approving the request or sending the response
	 */
	@Override
	public void execute(Message msg, ConnectionToClient client, EchoServer server) throws Exception {
		int requestId = (int) msg.getData();
		boolean success = server.getDatabase().approveRequest(requestId);
		client.sendToClient(new Message("APPROVE_REQUEST_RESULT", success));
	}
}