package Strategy;

import java.util.ArrayList;

import Common.Message;
import GUI.VisitorMyDetailsPanelController;
import javafx.application.Platform;

/**
 * Handles the server response containing visitor details.
 * <p>
 * This strategy receives the visitor details from the server and loads them
 * into the visitor details screen.
 */
public class VisitorDetailsResultStrategy implements MessageStrategy {

	/**
	 * Executes the strategy for handling visitor details.
	 * <p>
	 * The message data is expected to contain an {@code ArrayList<String>}
	 * with the visitor information.
	 *
	 * @param message the message received from the server containing visitor details
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void execute(Message message) {
		ArrayList<String> visitorDetails = (ArrayList<String>) message.getData();

		Platform.runLater(() -> {
			if (VisitorMyDetailsPanelController.instance != null) {
				VisitorMyDetailsPanelController.instance.loadVisitorDetails(visitorDetails);
			}
		});
	}
}