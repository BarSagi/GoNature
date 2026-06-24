package Strategy;

import Common.Message;
import OCSFUtils.ConnectionToClient;
import Server.EchoServer;

/**
 * Strategy class responsible for handling client logout requests.
 * The strategy removes the client from the server's logged-in users list.
 */
public class ClientLogoutStrategy implements MessageStrategy {

	/**
	 * Executes the client logout command.
	 * The method logs out the connected client from the server.
	 *
	 * @param message the message received from the client
	 * @param client the client connection that sent the message
	 * @param server the server that handles the logout request
	 * @throws Exception if an error occurs during logout
	 */
	@Override
	public void execute(Message message, ConnectionToClient client, EchoServer server) throws Exception {
		server.logoutUser(client);

	}

}