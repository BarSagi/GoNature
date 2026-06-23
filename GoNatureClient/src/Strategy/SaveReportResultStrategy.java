package Strategy;

import Common.Message;
import javafx.application.Platform;
import javafx.scene.control.Alert;

public class SaveReportResultStrategy implements MessageStrategy {

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