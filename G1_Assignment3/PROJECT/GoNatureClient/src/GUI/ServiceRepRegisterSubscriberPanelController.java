package GUI;

import Client.ClientUI;
import Common.Message;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import java.util.ArrayList;

/**
 * Controller for the service representative's subscriber registration panel.
 * Handles the input validation and submission of new family subscriber
 * registrations to the server, including payment method processing.
 */
public class ServiceRepRegisterSubscriberPanelController {

	/**
	 * Static instance of this controller for external access.
	 */
	public static ServiceRepRegisterSubscriberPanelController instance;

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
	private TextField familyMembersField;

	@FXML
	private ComboBox<String> paymentMethodComboBox;

	@FXML
	private TextField creditCardField;

	@FXML
	private Label statusLabel;

	/**
	 * Initializes the controller, populates the payment method dropdown, and
	 * configures the listener to toggle credit card field availability.
	 */
	@FXML
	public void initialize() {
		instance = this;
		paymentMethodComboBox.getItems().addAll("Yes", "No");
		paymentMethodComboBox.setValue("Please Choose...");

		paymentMethodComboBox.setOnAction(e -> {
			String paymentMethod = paymentMethodComboBox.getValue();
			boolean isCash = "No".equals(paymentMethod);
			creditCardField.setDisable(isCash);
			if (isCash) {
				creditCardField.clear();
			}
		});
	}

	/**
	 * Validates all subscriber input fields and submits the registration request to
	 * the server.
	 *
	 * @param event The action event triggered by the register button.
	 */
	@FXML
	void registerSubscriber(ActionEvent event) {
		try {
			String firstName = firstNameField.getText().trim();
			String lastName = lastNameField.getText().trim();
			String id = idField.getText().trim();
			String phone = phoneField.getText().trim();
			String email = emailField.getText().trim();
			String familyMembers = familyMembersField.getText().trim();
			String paymentMethod = paymentMethodComboBox.getValue();
			String creditCard = creditCardField.getText().trim();

			if (firstName.isEmpty() || lastName.isEmpty() || id.isEmpty() || phone.isEmpty() || email.isEmpty()
					|| familyMembers.isEmpty()) {
				statusLabel.setText("Please fill in all required fields.");
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

			if (!familyMembers.matches("\\d+") || Integer.parseInt(familyMembers) <= 0) {
				statusLabel.setText("Family members must be a positive number.");
				return;
			}

			if ("Yes".equals(paymentMethod)) {
				if (creditCard.isEmpty()) {
					statusLabel.setText("Please enter credit card number.");
					return;
				}

				if (!creditCard.matches("\\d{16}")) {
					statusLabel.setText("Credit card must be exactly 16 digits.");
					return;
				}
			}

			ArrayList<String> data = new ArrayList<>();
			data.add(id);
			data.add(firstName);
			data.add(lastName);
			data.add(phone);
			data.add(email);
			data.add(familyMembers);
			data.add("No".equals(paymentMethod) ? null : creditCard);

			Message msg = new Message("REGISTER_FAMILY_SUBSCRIBER", data);
			ClientUI.send(msg);

		} catch (Exception e) {
			statusLabel.setText("Failed to send subscriber registration.");
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