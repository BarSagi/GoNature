package Strategy;

import java.util.ArrayList;

import Common.Message;
import Common.Order;
import OCSFUtils.ConnectionToClient;
import Server.EchoServer;

public class GetOrdersStrategy implements MessageStrategy {
	// Reusing for park worker and manager "view orders" screen 
	@Override
	public void execute(Message message, ConnectionToClient client, EchoServer server) {
		
		try {
			String parkName = message.getData().toString();
			ArrayList<Order> orders = server.getDatabase().getAllParkOrders(parkName);

			client.sendToClient(new Message ("PARK_ORDERS_RESULT", orders));

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