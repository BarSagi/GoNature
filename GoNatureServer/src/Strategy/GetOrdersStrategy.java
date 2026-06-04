package Strategy;

import java.util.ArrayList;

import Common.Message;
import OCSFUtils.ConnectionToClient;
import Server.EchoServer;

public class GetOrdersStrategy implements MessageStrategy {

	@Override
	public void execute(Message message, ConnectionToClient client, EchoServer server) {

		try {

			ArrayList<ArrayList<String>> orders = server.getDatabase().getAllOrders();

			client.sendToClient(orders);

		} catch (Exception e) {

			e.printStackTrace();

			try {
				client.sendToClient("ERROR: Could not fetch orders");
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}
}