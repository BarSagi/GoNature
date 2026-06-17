package Strategy;

import Common.Message;
import GUI.DeptManagerApproveRejectPanelController;
import javafx.application.Platform;

public class RejectRequestResultStrategy implements MessageStrategy {

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
