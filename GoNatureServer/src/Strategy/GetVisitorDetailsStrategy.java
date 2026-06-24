package Strategy;

import java.util.ArrayList;

import Common.Message;
import OCSFUtils.ConnectionToClient;
import Server.EchoServer;

public class GetVisitorDetailsStrategy implements MessageStrategy {

	@Override
	public void execute(Message msg, ConnectionToClient client, EchoServer server) throws Exception {
		try {
			String visitorId = (String) msg.getData();

			ArrayList<String> visitorDetails = server.getDatabase().fetchVisitor(visitorId);

			client.sendToClient(new Message("VISITOR_DETAILS_RESULT", visitorDetails));

		} catch (Exception e) {
			e.printStackTrace();
			client.sendToClient(new Message("VISITOR_DETAILS_RESULT", null));
		}
	}
}