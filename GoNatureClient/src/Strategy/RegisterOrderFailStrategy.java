package Strategy;

import Common.Message;
import javafx.application.Platform;
import javafx.scene.control.Alert;

public class RegisterOrderFailStrategy implements MessageStrategy {

	@Override
	public void execute(Message message) {

		// The server sent us a specific error reason as a String
		String errorMessage = (String) message.getData();

		Platform.runLater(() -> {
			Alert alert = new Alert(Alert.AlertType.ERROR);
			alert.setTitle("Registration Failed");
			alert.setHeaderText("Could not complete order");
			alert.setContentText(errorMessage);
			alert.showAndWait();
		});

	}
}