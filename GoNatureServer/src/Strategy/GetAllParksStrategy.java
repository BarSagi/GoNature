package Strategy;

import java.util.ArrayList;

import Common.Message;
import OCSFUtils.ConnectionToClient;
import Server.EchoServer;

/**
 * Strategy class responsible for fetching all park names.
 * The strategy retrieves the list of parks from the database
 * and sends it back to the client.
 */
public class GetAllParksStrategy implements MessageStrategy {

	/**
	 * Executes the get all parks command.
	 * The method retrieves all park names from the database
	 * and sends the list back to the client.
	 *
	 * @param message the message received from the client
	 * @param client the client connection that sent the message
	 * @param server the server that handles the request and provides database access
	 */
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