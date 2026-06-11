package Strategy;

import java.util.ArrayList;

import Common.Message;
import OCSFUtils.ConnectionToClient;
import Server.EchoServer;

public class UpdateOrderStrategy implements MessageStrategy {
	// UNUSED!!!!!!!!!!!!!!!!!!!!!!!!
	@Override
	public void execute(Message message, ConnectionToClient client, EchoServer server) {

		try {

			@SuppressWarnings("unchecked")
			ArrayList<Object> data = (ArrayList<Object>) message.getData();

			int orderNumber = (int) data.get(0);
			String date = (String) data.get(1);
			int visitors = (int) data.get(2);

			boolean success = server.getDatabase().updateOrder(orderNumber, date, visitors);

			client.sendToClient(success);

		} catch (Exception e) {

			e.printStackTrace();

			try {
				client.sendToClient(false);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}
}