package Strategy;

import Common.Message;
import GUI.DeptManagerApproveRejectPanelController;
import javafx.application.Platform;

/**
 * Handles the server response after a department manager rejects a capacity change request.
 * <p>
 * This strategy updates the approve/reject panel according to the result
 * received from the server.
 */
public class RejectRequestResultStrategy implements MessageStrategy {

	/**
	 * Executes the strategy for handling the rejection request result.
	 * <p>
	 * The message data is expected to contain a boolean value that indicates
	 * whether the rejection operation was successful.
	 *
	 * @param message the message received from the server containing the rejection result
	 */
	@Override
	public void execute(Message message) {
		boolean success = (boolean) message.getData();

		Platform.runLater(() -> {
			if (DeptManagerApproveRejectPanelController.instance != null) {
				if (success) {
					DeptManagerApproveRejectPanelController.instance.showStatus("Request rejected successfully.");
					DeptManagerApproveRejectPanelController.instance.refreshRequests(null);
				} else {
					DeptManagerApproveRejectPanelController.instance.showStatus("Rejection failed.");
				}
			}
		});
	}
}