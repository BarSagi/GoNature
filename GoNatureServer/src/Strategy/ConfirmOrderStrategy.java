package Strategy;

import Common.Message;
import OCSFUtils.ConnectionToClient;
import Server.EchoServer;

public class ConfirmOrderStrategy implements MessageStrategy {

	@Override
	public void execute(Message msg, ConnectionToClient client, EchoServer server) throws Exception {
		int orderId = (int) msg.getData();
		boolean success = server.getDatabase().confirmOrder(orderId);
		client.sendToClient(new Message("CONFIRM_ORDER_RESULT", success));
	}
}
