package Strategy;

import Client.ClientUI;
import Client.GoNatureClient;
import Common.Message;
import javafx.application.Platform;
import javafx.scene.control.Alert;

/**
 * Handles the force logout process when a client is deemed idle by the server.
 * Triggers a UI alert and routes the user back to the login screen.
 */
public class ForceLogoutStrategy implements MessageStrategy {

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