package Strategy;

import Common.Message;
import GUI.VisitorMyDetailsPanelController;
import javafx.application.Platform;

/**
 * Handles the server response after attempting to update visitor details.
 * <p>
 * This strategy receives the update result from the server and passes it
 * to the visitor details screen.
 */
public class UpdateVisitorDetailsResultStrategy implements MessageStrategy {

	/**
	 * Executes the strategy for handling the visitor details update result.
	 * <p>
	 * The message data is expected to contain a boolean value that indicates
	 * whether the visitor details were updated successfully.
	 *
	 * @param message the message received from the server containing the update result
	 */
	@Override
	public void execute(Message message) {
		boolean success = (boolean) message.getData();

		Platform.runLater(() -> {
			if (VisitorMyDetailsPanelController.instance != null) {
				VisitorMyDetailsPanelController.instance.handleUpdateResult(success);
			}
		});
	}
}