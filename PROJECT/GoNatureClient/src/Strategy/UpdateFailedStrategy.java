package Strategy;

import Common.Message;
import GUI.EditOrderController;

/**
 * Handles the server response when an order update fails.
 * <p>
 * This strategy displays a warning alert to the user and updates the edit order
 * screen with a failure message.
 */
public class UpdateFailedStrategy implements MessageStrategy {
	
	/**
	 * Executes the strategy for handling a failed order update.
	 * <p>
	 * The failure is usually caused by lack of available park capacity for the
	 * requested date, time, or number of visitors.
	 *
	 * @param message the message received from the server
	 */
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