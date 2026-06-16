package Strategy;

import Common.Message;
import javafx.application.Platform;
import javafx.scene.control.Alert;

public class AddToWaitingListResultStrategy implements MessageStrategy {

	@Override
	public void execute(Message message) {
		boolean success = (boolean) message.getData();

		Platform.runLater(() -> {
			Alert alert;

			if (success) {
				alert = new Alert(Alert.AlertType.INFORMATION);
				alert.setTitle("Waiting List");
				alert.setHeaderText(null);
				alert.setContentText("You have been added to the waiting list successfully.");
			} else {
				alert = new Alert(Alert.AlertType.ERROR);
				alert.setTitle("Waiting List Failed");
				alert.setHeaderText(null);
				alert.setContentText("Could not add you to the waiting list.");
			}

			alert.showAndWait();
		});
	}
}
