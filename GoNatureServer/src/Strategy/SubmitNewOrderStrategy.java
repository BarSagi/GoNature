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

			String dbResult = server.getDatabase().createNewOrder(orderData);

			boolean success = "Approved".equalsIgnoreCase(dbResult);

			if (success) {
				client.sendToClient(new Message("ORDER_CREATION_RESULT", true));
			} else {
				client.sendToClient(new Message("ORDER_CREATION_RESULT", false));
			}

			System.out.println("Server: Order creation for visitor " + orderData.get(0) + " result: " + dbResult);

		} catch (Exception e) {

			System.out.println("Server Error: SubmitNewOrderStrategy failed");
			e.printStackTrace();

			try {
				client.sendToClient(new Message("ORDER_CREATION_RESULT", false));
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}
}