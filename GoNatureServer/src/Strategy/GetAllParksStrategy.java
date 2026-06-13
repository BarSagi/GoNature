package Strategy;

import java.util.ArrayList;

import Common.Message;
import OCSFUtils.ConnectionToClient;
import Server.EchoServer;

public class GetAllParksStrategy implements MessageStrategy {

	@Override
	public void execute(Message message, ConnectionToClient client, EchoServer server) {

		ArrayList<String> parks = server.getDatabase().getAllParkNames();

		try {
			client.sendToClient(new Message("ALL_PARKS_RESULT", parks));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
