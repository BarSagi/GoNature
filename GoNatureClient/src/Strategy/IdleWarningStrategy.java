package Strategy;

import Common.Message;
import javafx.application.Platform;
import javafx.scene.control.Alert;

/**
 * Strategy responsible for handling idle warning notifications sent from the
 * server. When executed, it displays a non-blocking alert to the user,
 * notifying them that their session is nearing expiration due to inactivity.
 */
public class IdleWarningStrategy implements MessageStrategy {

	/**
	 * Displays an inactivity warning alert to the user.
	 *
	 * @param message The message containing details about the idle warning.
	 */
	@Override
	public void execute(Message message) {
		Platform.runLater(() -> {
			Alert alert = new Alert(Alert.AlertType.WARNING);
			alert.setTitle("Idle Warning");
			alert.setHeaderText("Session Expiring Soon");
			alert.setContentText("You have been idle for 13 minutes. You will be logged out in 2 minutes.");
			alert.show();
		});
	}
}