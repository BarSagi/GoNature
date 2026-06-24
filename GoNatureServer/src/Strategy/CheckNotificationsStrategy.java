package Strategy;

import Common.Message;
import OCSFUtils.ConnectionToClient;
import Server.EchoServer;
import java.util.ArrayList;

/**
 * Strategy class responsible for checking unread notifications for a user.
 * The strategy receives the user's email, gets unread notifications from the database,
 * marks them as read, and sends them back to the client.
 */
public class CheckNotificationsStrategy implements MessageStrategy {

	/**
	 * Executes the check notifications command.
	 * The method gets unread notifications by email, marks them as read if needed,
	 * and sends the notification list back to the client.
	 *
	 * @param msg the message received from the client
	 * @param client the client connection that sent the message
	 * @param server the server that handles the request and provides database access
	 */
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