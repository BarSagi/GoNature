package Strategy;

import java.util.ArrayList;

import Common.Message;
import GUI.DeptManagerApproveRejectPanelController;
import javafx.application.Platform;

public class PendingRequestsResultStrategy implements MessageStrategy {

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