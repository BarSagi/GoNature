package Strategy;

import Common.Message;
import OCSFUtils.ConnectionToClient;
import Server.EchoServer;

public class GetVisitorEmailStrategy implements MessageStrategy {

	@Override
	public void execute(Message message, ConnectionToClient client, EchoServer server) throws Exception {

		String visitorId = (String) message.getData();

		try {
			String email = server.getDatabase().getVisitorEmailById(visitorId);

			client.sendToClient(new Message("VISITOR_EMAIL_RESULT", email));

		} catch (Exception e) {
			e.printStackTrace();

			client.sendToClient(new Message("VISITOR_EMAIL_RESULT", null));
		}
	}
}