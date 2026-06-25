package Strategy;

import Common.Message;

/**
 * Defines a strategy for handling messages received from the server.
 * <p>
 * Each class that implements this interface provides its own behavior
 * for processing a specific type of message.
 */
public interface MessageStrategy {

	/**
	 * Executes the message handling strategy.
	 *
	 * @param message the message received from the server
	 */
	void execute(Message message);
}