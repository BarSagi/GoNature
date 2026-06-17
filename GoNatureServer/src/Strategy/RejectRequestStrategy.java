package Strategy;

import Common.Message;
import OCSFUtils.ConnectionToClient;
import Server.EchoServer;

public class RejectRequestStrategy implements MessageStrategy {

	@Override
	public void execute(Message msg, ConnectionToClient client, EchoServer server) throws Exception {
		int requestId = (int) msg.getData();
		boolean success = server.getDatabase().rejectRequest(requestId);
		client.sendToClient(new Message("REJECT_REQUEST_RESULT", success));
	}
}
