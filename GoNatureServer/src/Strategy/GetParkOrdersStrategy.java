package Strategy;

import java.util.ArrayList;

import Common.Message;
import Common.Order;
import OCSFUtils.ConnectionToClient;
import Server.EchoServer;

public class GetParkOrdersStrategy implements MessageStrategy {

	@Override
	public void execute(Message msg, ConnectionToClient client, EchoServer server) throws Exception {
		String parkName = (String) msg.getData();

		ArrayList<Order> orders = server.getDatabase().getOrdersByParkName(parkName);

		client.sendToClient(new Message("RETURN_PARK_ORDERS", orders));
	}
}