package Strategy;

import Common.Message;
import GUI.ParkWorkerEnterVisitorController;
import javafx.application.Platform;

public class EnterVisitorResultStrategy implements MessageStrategy {

	@Override
	public void execute(Message message) {
		boolean success = (boolean) message.getData();

		Platform.runLater(() -> {
			if (ParkWorkerEnterVisitorController.instance != null) {
				if (success) {
					ParkWorkerEnterVisitorController.instance.showStatus("Visitor entered successfully.");
				} else {
					ParkWorkerEnterVisitorController.instance.showStatus(
									"Entry Denied!\n\n"
									+ "Please verify the following:\n"
									+ "• The ID number was entered correctly.\n"
									+ "• The order status is set to 'Approved'.\n"
									+ "• The current time is within 30 minutes of the scheduled visit.");
									
				}
			}
		});
	}
}