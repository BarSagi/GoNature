package Strategy;

import Common.Message;
import javafx.application.Platform;
import javafx.scene.control.Alert;

// Assuming your client-side strategy interface is called ClientMessageStrategy
/**
 * Handles a server response that indicates the user is already logged in.
 * <p>
 * This strategy displays an error alert to the user and prints a message
 * to the client console.
 */
public class AlreadyLoggedInStrategy implements MessageStrategy {

	/**
	 * Executes the strategy for handling an already logged-in user response.
	 * <p>
	 * An error alert is displayed on the JavaFX application thread to inform
	 * the user that the login attempt was denied.
	 *
	 * @param message the message received from the server
	 */
	@Override
	public void execute(Message message) {
		
		Platform.runLater(() -> {
			Alert alert = new Alert(Alert.AlertType.ERROR);
			alert.setTitle("Login Error");
			alert.setHeaderText("Connection Denied");
			alert.setContentText("This user is already logged in!");
			alert.showAndWait();
		});
		
		System.out.println("Client: Login denied - User is already logged in.");
	}
}