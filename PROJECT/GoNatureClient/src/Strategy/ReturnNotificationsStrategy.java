package Strategy;

import Common.Message;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import java.util.ArrayList;

/**
 * Handles the server response containing unread notifications.
 * <p>
 * This strategy receives a list of notifications from the server and displays
 * each one to the user as an information alert.
 */
public class ReturnNotificationsStrategy implements MessageStrategy {

	/**
	 * Executes the strategy for displaying unread notifications.
	 * <p>
	 * The message data is expected to contain an {@code ArrayList<String>} where
	 * each notification may include a contact method and message content, separated
	 * by the "|" delimiter.
	 *
	 * @param message the message received from the server containing notifications
	 */
	@Override
	public void execute(Message message) {

		@SuppressWarnings("unchecked")
		ArrayList<String> notifications = (ArrayList<String>) message.getData();

		// If there are no notifications, do nothing!
		if (notifications == null || notifications.isEmpty()) {
			return;
		}

		Platform.runLater(() -> {
			// Loop through all unread messages and show an alert for each
			for (String notifData : notifications) {

				// Extract the method and the message using the "|" delimiter
				String[] parts = notifData.split("\\|", 2);
				String method = "Notification";
				String content = notifData; // Default to full string just in case

				if (parts.length == 2) {
					method = parts[0];
					content = parts[1];
				}

				Alert alert = new Alert(Alert.AlertType.INFORMATION);

				// Make the title dynamic based on the contact method!
				if ("SMS".equalsIgnoreCase(method)) {
					alert.setTitle("[SIMULATION] New SMS Received on Phone");
				} else if ("Email".equalsIgnoreCase(method)) {
					alert.setTitle("[SIMULATION] New Email in Inbox");
				} else {
					alert.setTitle("[SIMULATION] New Notification");
				}

				alert.setHeaderText("GoNature System Alert");
				alert.setContentText(content);

				// showAndWait pauses the loop until the user clicks "OK"
				alert.show();
			}
		});
	}
}