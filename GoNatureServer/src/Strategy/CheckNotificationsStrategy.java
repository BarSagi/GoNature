package Strategy;

import Common.Message;
import OCSFUtils.ConnectionToClient;
import Server.EchoServer;
import java.util.ArrayList;

public class CheckNotificationsStrategy implements MessageStrategy {

	@Override
	public void execute(Message msg, ConnectionToClient client, EchoServer server) {
		try {
			String email = (String) msg.getData();

			// 1. Get the unread messages
			ArrayList<String> unreadMessages = server.getDatabase().getUnreadNotifications(email);

			// 2. Mark them as read in the database
			if (!unreadMessages.isEmpty()) {
				server.getDatabase().markNotificationsAsRead(email);
			}

			// 3. Send them back to the client UI
			client.sendToClient(new Message("RETURN_NOTIFICATIONS", unreadMessages));

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}