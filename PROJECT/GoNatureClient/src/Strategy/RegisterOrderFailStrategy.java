package Strategy;

import Common.Message;
import javafx.application.Platform;
import javafx.scene.control.Alert;

/**
 * Handles the server response when registration and order creation fail.
 * <p>
 * This strategy receives a specific error message from the server and displays
 * it to the user in an error alert.
 */
public class RegisterOrderFailStrategy implements MessageStrategy {

	/**
	 * Executes the strategy for handling a failed registration and order creation result.
	 * <p>
	 * The message data is expected to contain a string that describes the reason
	 * for the failure.
	 *
	 * @param message the message received from the server containing the error message
	 */
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