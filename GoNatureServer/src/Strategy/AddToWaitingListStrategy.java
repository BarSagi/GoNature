package Strategy;

import java.util.ArrayList;

import Common.Message;
import OCSFUtils.ConnectionToClient;
import Server.EchoServer;

public class AddToWaitingListStrategy implements MessageStrategy {

	@Override
	public void execute(Message msg, ConnectionToClient client, EchoServer server) {
		try {
			@SuppressWarnings("unchecked")
			ArrayList<String> orderData = (ArrayList<String>) msg.getData();

			boolean success = server.getDatabase().addOrderToWaitingList(orderData);

			client.sendToClient(new Message("ADD_TO_WAITING_LIST_RESULT", success));

		} catch (Exception e) {
			e.printStackTrace();
			try {
				client.sendToClient(new Message("ADD_TO_WAITING_LIST_RESULT", false));
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}
}