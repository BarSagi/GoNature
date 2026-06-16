package Strategy;

import java.util.ArrayList;
import Common.Message;
import OCSFUtils.ConnectionToClient;
import Server.EchoServer;

public class SubmitNewOrderStrategy implements MessageStrategy {

	@Override
	public void execute(Message msg, ConnectionToClient client, EchoServer server) {
		try {
			@SuppressWarnings("unchecked")
			ArrayList<String> orderData = (ArrayList<String>) msg.getData();

			String result = server.getDatabase().createNewOrder(orderData);

			client.sendToClient(new Message("ORDER_CREATION_RESULT", result));

			System.out.println(
					"Server: Order creation attempt for visitor " + orderData.get(0) + " resulted in: " + result);

		} catch (Exception e) {
			System.out.println("Server Error: Failed to execute SubmitNewOrderStrategy.");
			e.printStackTrace();

			try {
				client.sendToClient(new Message("ORDER_CREATION_RESULT", "Failed"));
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}
}