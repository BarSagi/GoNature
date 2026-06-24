package Strategy;

import Common.Message;
import javafx.application.Platform;
import javafx.scene.control.Alert;

/**
 * Handles the server response after attempting to save a report.
 * <p>
 * This strategy receives the save result from the server and displays
 * a success or failure alert to the user.
 */
public class SaveReportResultStrategy implements MessageStrategy {

	/**
	 * Executes the strategy for handling the save report result.
	 * <p>
	 * The message data is expected to contain a boolean value that indicates
	 * whether the report was saved successfully.
	 *
	 * @param message the message received from the server containing the save result
	 */
	@Override
	public void execute(Message message) {

		boolean success = (boolean) message.getData();

		Platform.runLater(() -> {

			Alert alert = new Alert(Alert.AlertType.INFORMATION);

			if (success) {
				alert.setTitle("Success");
				alert.setHeaderText(null);
				alert.setContentText("Report saved successfully!");
			} else {
				alert.setTitle("Error");
				alert.setHeaderText(null);
				alert.setContentText("Failed to save report.");
			}

			alert.showAndWait();
		});
	}
}