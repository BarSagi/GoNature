package Strategy;

import Common.Message;
import OCSFUtils.ConnectionToClient;
import Server.EchoServer;

public class UpdateOrderPaidStrategy implements MessageStrategy {

	@Override
	public void execute(Message message, ConnectionToClient client, EchoServer server) {
		try {
			String orderIdStr = (String) message.getData();
			int orderId = Integer.parseInt(orderIdStr);

			boolean success = server.getDatabase().updateOrderPaidStatus(orderId);

			if (success) {
				client.sendToClient(new Message("ENTER_VISITOR_RESULT", "Payment Successful"));
			} else {
				client.sendToClient(new Message("ENTER_VISITOR_RESULT", "Failed to update payment in DB."));
			}

		} catch (Exception e) {
			e.printStackTrace();
			try {
				client.sendToClient(new Message("ENTER_VISITOR_RESULT", "Server error during payment update."));
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}
}
