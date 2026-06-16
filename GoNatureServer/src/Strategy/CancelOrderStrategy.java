package Strategy;

import Common.Message;
import OCSFUtils.ConnectionToClient;
import Server.EchoServer;

public class CancelOrderStrategy implements MessageStrategy {

	@Override
	public void execute(Message msg, ConnectionToClient client, EchoServer server) throws Exception {
		try {
			int orderId = (int) msg.getData();

			boolean success = server.getDatabase().cancelOrder(orderId);

			client.sendToClient(new Message("ORDER_CANCEL_RESULT", success));

		} catch (Exception e) {
			e.printStackTrace();
			client.sendToClient(new Message("ORDER_CANCEL_RESULT", false));
		}
	}
}