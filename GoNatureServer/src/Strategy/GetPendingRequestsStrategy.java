package Strategy;

import java.util.ArrayList;

import Common.Message;
import OCSFUtils.ConnectionToClient;
import Server.EchoServer;

public class GetPendingRequestsStrategy implements MessageStrategy {

	@Override
	public void execute(Message msg, ConnectionToClient client, EchoServer server) throws Exception {
		ArrayList<ArrayList<String>> requests = server.getDatabase().getPendingRequests();
		client.sendToClient(new Message("RETURN_PENDING_REQUESTS", requests));
	}
}
