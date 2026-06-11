package Strategy; // (Put this in your Client's Strategy package)

import Client.ClientUI;
import Client.GoNatureClient;
import Common.Message;
import Entity.Visitor;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import java.util.ArrayList;

public class RegisterOrderSuccessStrategy implements MessageStrategy {

	@Override
	public void execute(Message message) {

		// 1. Extract the visitor data sent back from the server
		@SuppressWarnings("unchecked")
		ArrayList<String> visitorData = (ArrayList<String>) message.getData();

		// 3. Update the UI safely
		Platform.runLater(() -> {
			// Optional: Show a quick success popup
			Alert alert = new Alert(Alert.AlertType.INFORMATION);
			alert.setTitle("Registration Successful");
			alert.setHeaderText(null);
			alert.setContentText("Welcome to GoNature! Your order has been placed.");
			alert.showAndWait();

			// 4. Move to the Dashboard
			try {
				ClientUI.changeScreen("/GUI/LoginVisitor.fxml", "GoNature - Visitor Login");
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
	}
}