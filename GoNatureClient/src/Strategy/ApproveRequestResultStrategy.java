package Strategy;

import Common.Message;
import GUI.DeptManagerApproveRejectPanelController;
import javafx.application.Platform;

public class ApproveRequestResultStrategy implements MessageStrategy {

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