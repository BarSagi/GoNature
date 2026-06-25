package GUI;

import Client.ClientUI;
import Common.Message;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import java.util.ArrayList;

/**
 * Controller for the service representative's guide registration panel. Handles
 * the input validation and submission of new group guide registrations to the
 * server.
 */
public class ServiceRepRegisterGuidePanelController {

	/**
	 * Static instance of this controller for external access.
	 */
	public static ServiceRepRegisterGuidePanelController instance;

	@FXML
	private TextField firstNameField;

	@FXML
	private TextField lastNameField;

	@FXML
	private TextField idField;

	@FXML
	private TextField phoneField;

	@FXML
	private TextField emailField;

	@FXML
	private Label statusLabel;

	/**
	 * Initializes the controller and sets the status label to empty.
	 */
	@FXML
	public void initialize() {
		instance = this;
		statusLabel.setText("");
	}

	/**
	 * Validates the guide registration input fields and sends the registration data
	 * to the server.
	 *
	 * @param event The action event triggered by the register button.
	 */
	@FXML
	void registerGuide(ActionEvent event) {
		try {
			String firstName = firstNameField.getText().trim();
			String lastName = lastNameField.getText().trim();
			String id = idField.getText().trim();
			String phone = phoneField.getText().trim();
			String email = emailField.getText().trim();

			if (firstName.isEmpty() || lastName.isEmpty() || id.isEmpty() || phone.isEmpty() || email.isEmpty()) {
				statusLabel.setText("Please fill in all fields.");
				return;
			}

			if (!firstName.matches("[a-zA-Z ]+") || !lastName.matches("[a-zA-Z ]+")) {
				statusLabel.setText("First and last name must contain letters only.");
				return;
			}

			if (!id.matches("\\d{9}")) {
				statusLabel.setText("ID must be exactly 9 digits.");
				return;
			}

			if (!phone.matches("\\d{10}")) {
				statusLabel.setText("Phone number must be exactly 10 digits.");
				return;
			}

			if (!email.matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")) {
				statusLabel.setText("Please enter a valid email address.");
				return;
			}

			ArrayList<String> data = new ArrayList<>();
			data.add(id);
			data.add(firstName);
			data.add(lastName);
			data.add(phone);
			data.add(email);

			Message msg = new Message("REGISTER_GROUP_GUIDE", data);
			ClientUI.send(msg);

		} catch (Exception e) {
			statusLabel.setText("Failed to send guide registration.");
			e.printStackTrace();
		}
	}

	/**
	 * Displays a status message on the GUI.
	 *
	 * @param text The message to display.
	 */
	public void showStatus(String text) {
		statusLabel.setText(text);
	}
}