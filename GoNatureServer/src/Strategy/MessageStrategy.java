package Strategy;

import Common.Message;
import OCSFUtils.ConnectionToClient;
import Server.EchoServer;

/**
 * Interface for handling different message commands using the Strategy pattern.
 * Each implementing class defines how a specific message should be processed.
 */
public interface MessageStrategy {

	/**
	 * Executes the strategy for a received message.
	 *
	 * @param message the message received from the client
	 * @param client the client connection that sent the message
	 * @param server the server that handles the request and provides needed services
	 * @throws Exception if an error occurs while executing the strategy
	 */
	void execute(Message message, ConnectionToClient client, EchoServer server) throws Exception;
}