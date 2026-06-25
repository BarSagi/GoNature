package Strategy;

import java.util.ArrayList;

import Common.Message;
import GUI.ServiceRepSearchSubscriberController;
import javafx.application.Platform;

/**
 * Handles the server response containing subscriber details.
 * <p>
 * This strategy receives subscriber information from the server and updates
 * the service representative subscriber search screen. If no subscriber data
 * is found, a not found message is displayed.
 */
public class SubscriberDetailsResultStrategy implements MessageStrategy {

	/**
	 * Executes the strategy for handling subscriber details.
	 * <p>
	 * The message data is expected to contain an {@code ArrayList<String>}
	 * with the subscriber information, or an empty list if the subscriber
	 * was not found.
	 *
	 * @param message the message received from the server containing subscriber details
	 */
	@Override
	public void execute(Message message) {
		@SuppressWarnings("unchecked")
		ArrayList<String> subscriberInfo = (ArrayList<String>) message.getData();

		Platform.runLater(() -> {
			ServiceRepSearchSubscriberController controller = ServiceRepSearchSubscriberController.getInstance();

			if (subscriberInfo == null || subscriberInfo.isEmpty()) {
				controller.showSubscriberNotFound();
			} else {
				controller.displaySubscriberInfo(subscriberInfo);
			}
		});
	}
}