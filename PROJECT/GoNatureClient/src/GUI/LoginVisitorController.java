package GUI;

import Client.ClientUI;
import Common.Message;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

/**
 * Controller for the visitor login screen. Handles visitor identification by
 * capturing the visitor ID, validating the input format, and sending a request
 * to the server to check for existing orders.
 */
public class LoginVisitorController {

	@FXML
	private TextField idField;

	@FXML
	private Label errorLabel;

	/**
	 * Validates the visitor ID input and sends a request to the server to retrieve
	 * the visitor's orders.
	 *
	 * @param event The action event triggered by the login button.
	 */
	@FXML
	void loginVisitor(ActionEvent event) {
		String id = idField.getText();
		errorLabel.setVisible(false);

		// Basic validation
		if (id.isEmpty()) {
			errorLabel.setText("Please enter ID number.");
			errorLabel.setVisible(true);
			return;
		}

		// Exactly 9 numbers validation
		if (!id.matches("\\d{9}")) {
			errorLabel.setText("ID must be exactly 9 digits.");
			errorLabel.setVisible(true);
			return;
		}

		// Send a message to the server asking for this visitor's orders
		Message msg = new Message("CHECK_VISITOR_ORDERS", id);

		try {
			ClientUI.send(msg);
		} catch (Exception e) {
			System.out.println("Error sending message to server");
			e.printStackTrace();
		}
	}

	/**
	 * Navigates back to the role selection screen.
	 *
	 * @param event The action event triggered by the back button.
	 */
	@FXML
	void goBack(ActionEvent event) {
		ClientUI.changeScreen("/GUI/LoginRoute.fxml", "GoNature - Choose Role");
	}
}