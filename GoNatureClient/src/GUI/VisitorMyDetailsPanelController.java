package GUI;

import Client.ClientUI;
import Client.GoNatureClient;
import Common.Message;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import java.util.ArrayList;

/**
 * Controller for the visitor's personal details panel. Enables visitors to view
 * and update their profile information, including contact details and payment
 * methods.
 */
public class VisitorMyDetailsPanelController {

	/**
	 * Static instance of this controller for external access.
	 */
	public static VisitorMyDetailsPanelController instance;

	@FXML
	private TextField firstNameField;

	@FXML
	private TextField lastNameField;

	@FXML
	private TextField visitorTypeField;

	@FXML
	private TextField phoneField;

	@FXML
	private TextField emailField;

	@FXML
	private TextField creditCardField;

	@FXML
	private Label statusLabel;

	/**
	 * Initializes the controller and requests the current visitor's details from
	 * the server.
	 */
	@FXML
	public void initialize() {
		instance = this;

		if (GoNatureClient.currentVisitor != null) {
			try {
				ClientUI.send(new Message("GET_VISITOR_DETAILS", GoNatureClient.currentVisitor.getVisitorId()));
			} catch (Exception e) {
				statusLabel.setText("Failed to load visitor details.");
				e.printStackTrace();
			}
		}
	}

	/**
	 * Populates the UI fields with the visitor's information retrieved from the
	 * server.
	 *
	 * @param visitorDetails An ArrayList containing the visitor's details.
	 */
	public void loadVisitorDetails(ArrayList<String> visitorDetails) {
		if (visitorDetails == null || visitorDetails.isEmpty()) {
			statusLabel.setText("Could not load visitor details.");
			return;
		}

		firstNameField.setText(visitorDetails.get(1));
		lastNameField.setText(visitorDetails.get(2));
		emailField.setText(visitorDetails.get(4));
		phoneField.setText(visitorDetails.get(3));
		visitorTypeField.setText(visitorDetails.get(5));
		creditCardField.setText(visitorDetails.get(8));
	}

	/**
	 * Validates the updated fields and sends a request to update the visitor's
	 * details on the server.
	 *
	 * @param event The action event triggered by the save changes button.
	 */
	@FXML
	void saveChanges(ActionEvent event) {
		String firstName = firstNameField.getText().trim();
		String lastName = lastNameField.getText().trim();
		String phone = phoneField.getText().trim();
		String email = emailField.getText().trim();
		String creditCard = creditCardField.getText().trim();

		if (firstName.isEmpty() || lastName.isEmpty() || phone.isEmpty() || email.isEmpty() || creditCard.isEmpty()) {
			statusLabel.setStyle("-fx-text-fill: #e74c3c;");
			statusLabel.setText("Please fill in all fields.");
			return;
		}

		if (!firstName.matches("[a-zA-Z ]+")) {
			statusLabel.setText("First name must contain letters only.");
			return;
		}

		if (!lastName.matches("[a-zA-Z ]+")) {
			statusLabel.setText("Last name must contain letters only.");
			return;
		}

		if (!phone.matches("\\d{10}")) {
			statusLabel.setStyle("-fx-text-fill: #e74c3c;");
			statusLabel.setText("Phone number must be exactly 10 digits.");
			return;
		}

		String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.com$";
		if (!email.matches(emailRegex)) {
			statusLabel.setStyle("-fx-text-fill: #e74c3c;");
			statusLabel.setText("Please enter a valid email address.");
			return;
		}

		if (!creditCard.matches("\\d{4}-\\d{4}-\\d{4}-\\d{4}")) {
			statusLabel.setStyle("-fx-text-fill: #e74c3c;");
			statusLabel.setText("Credit card must be exactly 16 digits, in this format: 1234-5678-9098-7654");
			return;
		}

		try {
			ArrayList<String> data = new ArrayList<>();
			data.add(GoNatureClient.currentVisitor.getVisitorId());
			data.add(firstName);
			data.add(lastName);
			data.add(phone);
			data.add(email);
			data.add(creditCard);

			ClientUI.send(new Message("UPDATE_VISITOR_DETAILS", data));

		} catch (Exception e) {
			statusLabel.setStyle("-fx-text-fill: #e74c3c;");
			statusLabel.setText("Failed to send update request.");
			e.printStackTrace();
		}
	}

	/**
	 * Handles the outcome of the update request and updates the local session data
	 * if successful.
	 *
	 * @param success Indicates if the update was successful.
	 */
	public void handleUpdateResult(boolean success) {
		if (success) {
			statusLabel.setStyle("-fx-text-fill: #27ae60;");
			statusLabel.setText("Details updated successfully.");

			if (GoNatureClient.currentVisitor != null) {
				GoNatureClient.currentVisitor.setFirstName(firstNameField.getText().trim());
				GoNatureClient.currentVisitor.setLastName(lastNameField.getText().trim());
				GoNatureClient.currentVisitor.setPhone(phoneField.getText().trim());
				GoNatureClient.currentVisitor.setEmail(emailField.getText().trim());
			}
		} else {
			statusLabel.setStyle("-fx-text-fill: #e74c3c;");
			statusLabel.setText("Failed to update details.");
		}
	}
}