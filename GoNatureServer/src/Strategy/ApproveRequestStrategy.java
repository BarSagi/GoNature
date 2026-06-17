package Strategy;

import Common.Message;
import OCSFUtils.ConnectionToClient;
import Server.EchoServer;

public class ApproveRequestStrategy implements MessageStrategy {

	@Override
	public void execute(Message msg, ConnectionToClient client, EchoServer server) throws Exception {
		int requestId = (int) msg.getData();
		boolean success = server.getDatabase().approveRequest(requestId);
		client.sendToClient(new Message("APPROVE_REQUEST_RESULT", success));
	}
}
