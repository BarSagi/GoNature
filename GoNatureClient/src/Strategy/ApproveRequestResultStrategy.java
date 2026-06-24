package Strategy;

import Common.Message;
import GUI.DeptManagerApproveRejectPanelController;
import javafx.application.Platform;

/**
 * Handles the server response after a department manager approves a capacity change request.
 * <p>
 * This strategy updates the approve/reject panel according to the result
 * received from the server.
 */
public class ApproveRequestResultStrategy implements MessageStrategy {

	/**
	 * Executes the strategy for handling the approval request result.
	 * <p>
	 * The message data is expected to contain a boolean value that indicates
	 * whether the approval operation was successful.
	 *
	 * @param message the message received from the server containing the approval result
	 */
	@Override
	public void execute(Message message) {
		boolean success = (boolean) message.getData();

		Platform.runLater(() -> {
			if (DeptManagerApproveRejectPanelController.instance != null) {
				if (success) {
					DeptManagerApproveRejectPanelController.instance.showStatus("Request approved successfully.");
					DeptManagerApproveRejectPanelController.instance.refreshRequests(null);
				} else {
					DeptManagerApproveRejectPanelController.instance.showStatus("Approval failed. Check the requested capacity.");
				}
			}
		});
	}
}