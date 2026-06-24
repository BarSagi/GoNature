package Strategy;

import java.util.ArrayList;

import Common.Message;
import GUI.DeptManagerApproveRejectPanelController;
import javafx.application.Platform;

/**
 * Handles the server response containing pending capacity change requests.
 * <p>
 * This strategy receives the pending requests from the server and loads them
 * into the department manager approve/reject panel.
 */
public class PendingRequestsResultStrategy implements MessageStrategy {

	/**
	 * Executes the strategy for displaying pending requests.
	 * <p>
	 * The message data is expected to contain an {@code ArrayList<ArrayList<String>>}
	 * where each inner list represents one pending request.
	 *
	 * @param message the message received from the server containing pending requests
	 */
	@Override
	public void execute(Message message) {
		@SuppressWarnings("unchecked")
		ArrayList<ArrayList<String>> data = (ArrayList<ArrayList<String>>) message.getData();

		Platform.runLater(() -> {
			if (DeptManagerApproveRejectPanelController.instance != null) {
				DeptManagerApproveRejectPanelController.instance.loadRequests(data);
			}
		});
	}
}