package Strategy;

import java.util.ArrayList;

import Common.Message;
import OCSFUtils.ConnectionToClient;
import Server.EchoServer;

public class UpdateVisitorDetailsStrategy implements MessageStrategy {

	@SuppressWarnings("unchecked")
	@Override
	public void execute(Message msg, ConnectionToClient client, EchoServer server) throws Exception {
		try {
			ArrayList<String> data = (ArrayList<String>) msg.getData();

			String visitorId = data.get(0);
			String firstName = data.get(1);
			String lastName = data.get(2);
			String phone = data.get(3);
			String email = data.get(4);
			String creditCard = data.get(5);

			boolean success = server.getDatabase().updateVisitorDetails(visitorId, firstName, lastName, phone, email,
					creditCard);

			client.sendToClient(new Message("UPDATE_VISITOR_DETAILS_RESULT", success));

		} catch (Exception e) {
			e.printStackTrace();
			client.sendToClient(new Message("UPDATE_VISITOR_DETAILS_RESULT", false));
		}
	}
}