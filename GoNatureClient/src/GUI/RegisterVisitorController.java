package GUI;

import Common.Message;
import Client.ClientUI;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import java.util.ArrayList;

public class RegisterVisitorController {

	@FXML
	private TextField idField;

	@FXML
	private TextField firstNameField;

	@FXML
	private TextField lastNameField;

	@FXML
	private TextField phoneField;

	@FXML
	private TextField emailField;

	@FXML
	private Label statusLabel;

	@FXML
	void registerVisitor(ActionEvent event) {
		statusLabel.setVisible(false);

		String id = idField.getText().trim();
		String firstName = firstNameField.getText().trim();
		String lastName = lastNameField.getText().trim();
		String phone = phoneField.getText().trim();
		String email = emailField.getText().trim();

		// 1. Basic Validation
		if (id.isEmpty() || firstName.isEmpty() || lastName.isEmpty() || phone.isEmpty() || email.isEmpty()) {
			showError("Please fill in all fields.");
			return;
		}

		if (!id.matches("\\d+")) {
			showError("ID must contain only numbers.");
			return;
		}
		
		if (!firstName.matches("[a-zA-Z ]+") || !lastName.matches("[a-zA-Z ]+")) {
			showError("First and last name must contain letters only.");
			return;
		}

		if (!phone.matches("\\d{10}")) {
			showError("Phone number must be exactly 10 digits.");
			return;
		}

		if (!email.matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")) {
			showError("Please enter a valid email address.");
			return;
		}

		// 2. Package the data for the server
		ArrayList<String> registrationData = new ArrayList<>();
		registrationData.add(id);
		registrationData.add(firstName);
		registrationData.add(lastName);
		registrationData.add(phone);
		registrationData.add(email);

		// 3. Send to Server
		Message msg = new Message("REGISTER_NEW_VISITOR", registrationData);
		try {
			ClientUI.send(msg);
			System.out.println("Client: Sent registration request to server.");
			// Note: Once you get a successful response back from the server,
			// you can save the ID to ClientUI.loggedInVisitorId and change to the
			// OrderCreationScreen!
		} catch (Exception e) {
			showError("Connection error: Could not contact server.");
			e.printStackTrace();
		}
	}

	@FXML
	void goBack(ActionEvent event) {
		// Assume you have your changeScreen method in ClientUI
		try {
			ClientUI.changeScreen("/GUI/LoginVisitor.fxml", "GoNature - Visitor Login");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void showError(String msg) {
		statusLabel.setText(msg);
		statusLabel.setStyle("-fx-text-fill: red;");
		statusLabel.setVisible(true);
	}
}