package Strategy;

import Common.Message;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import java.util.ArrayList;

public class ReturnNotificationsStrategy implements MessageStrategy {

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
					alert.setTitle("New SMS Received on Phone");
				} else if ("Email".equalsIgnoreCase(method)) {
					alert.setTitle("New Email in Inbox");
				} else {
					alert.setTitle("New Notification");
				}

				alert.setHeaderText("GoNature System Alert");
				alert.setContentText(content);

				// showAndWait pauses the loop until the user clicks "OK"
				alert.showAndWait();
			}
		});
	}
}