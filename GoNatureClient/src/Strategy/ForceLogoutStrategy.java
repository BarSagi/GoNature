package Strategy;

import Common.Message;
import Client.ClientUI;
import Client.GoNatureClient;
import javafx.application.Platform;
import javafx.scene.control.Alert;

/**
 * Strategy responsible for handling forced logout commands initiated by the server.
 * <p>
 * This strategy clears local session data, displays a notification alert to the 
 * user regarding inactivity, and routes the application interface back to the 
 * login screen.
 */
public class ForceLogoutStrategy implements MessageStrategy {

	/**
	 * Executes the logout process.
	 * <p>
	 * Clears the current visitor session, displays an information alert, and
	 * navigates the application back to the login screen.
	 *
	 * @param message The message received from the server triggering the force logout.
	 */
	@Override
	public void execute(Message message) {
		// 1. Clear local session data
		GoNatureClient.currentVisitor = null;
		// GoNatureClient.currentEmployee = null;

		// 2. Perform UI updates on the JavaFX Thread
		Platform.runLater(() -> {
			Alert alert = new Alert(Alert.AlertType.INFORMATION);
			alert.setTitle("Session Expired");
			alert.setHeaderText("Logged Out Due to Inactivity");
			alert.setContentText(
					"You have been idle for over 15 minutes. For your security, you have been logged out.");
			alert.showAndWait();

			// 3. Route back to login
			ClientUI.changeScreen("/GUI/LoginRoute.fxml", "GoNature - Welcome");
		});
	}
}