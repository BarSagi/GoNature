package Strategy;

import Common.Message;
import javafx.application.Platform;
import javafx.scene.control.Alert;

// Assuming your client-side strategy interface is called ClientMessageStrategy
public class AlreadyLoggedInStrategy implements MessageStrategy {

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