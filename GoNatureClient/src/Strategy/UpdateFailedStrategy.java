package Strategy;

import Common.Message;
import GUI.EditOrderController;

public class UpdateFailedStrategy implements MessageStrategy {
	@Override
	public void execute(Message message) {
		javafx.application.Platform.runLater(() -> {
			javafx.scene.control.Alert alert = new javafx.scene.control.Alert(
					javafx.scene.control.Alert.AlertType.WARNING);
			alert.setTitle("Capacity Error");
			alert.setHeaderText("Update Failed");
			alert.setContentText("We're sorry, but the park does not have enough available space for "
					+ "that amount of visitors at the requested date and time. Please select a different time or reduce the number of visitors.");
			alert.showAndWait();

			if (EditOrderController.instance != null) {
				EditOrderController.instance.editStatusLabel("Update failed! Please check capacity.");
			}
		});
	}
}