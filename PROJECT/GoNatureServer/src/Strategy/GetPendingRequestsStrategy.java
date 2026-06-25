package Strategy;

import java.util.ArrayList;

import Common.Message;
import OCSFUtils.ConnectionToClient;
import Server.EchoServer;

/**
 * Strategy class responsible for fetching all pending requests.
 * The strategy retrieves pending requests from the database
 * and sends them back to the client.
 */
public class GetPendingRequestsStrategy implements MessageStrategy {

	/**
	 * Executes the get pending requests command.
	 * The method retrieves all pending requests from the database
	 * and sends the request list back to the client.
	 *
	 * @param msg the message received from the client
	 * @param client the client connection that sent the message
	 * @param server the server that handles the request and provides database access
	 * @throws Exception if an error occurs while fetching or sending the requests
	 */
	@Override
	public void execute(Message msg, ConnectionToClient client, EchoServer server) throws Exception {
		ArrayList<ArrayList<String>> requests = server.getDatabase().getPendingRequests();
		client.sendToClient(new Message("RETURN_PENDING_REQUESTS", requests));
	}
}