package Strategy;

import Common.Message;
import GUI.ParkWorkerExitVisitorController;
import javafx.application.Platform;

public class ExitVisitorResultStrategy implements MessageStrategy {

	@Override
	public void execute(Message message) {
		boolean success = (boolean) message.getData();

		Platform.runLater(() -> {
			if (ParkWorkerExitVisitorController.instance != null) {
				if (success) {
					ParkWorkerExitVisitorController.instance.showStatus("Visitor exited successfully.");
				} else {
					ParkWorkerExitVisitorController.instance.showStatus("Failed to exit visitor.");
				}
			}
		});
	}
}
