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
 * Controller for the visitor's personal details panel.
 * <p>
 * This controller allows every visitor to view their personal details. If the
 * visitor is a subscriber, the fields are editable and the visitor can update
 * their personal information. Non-subscriber visitors can only view their
 * details.
 */
public class VisitorMyDetailsPanelController {

	/**
	 * Static instance of this controller, used by client strategies to access the
	 * currently loaded details panel.
	 */
	public static VisitorMyDetailsPanelController instance;

	/**
	 * Indicates whether the current visitor is a subscriber. Subscribers are
	 * allowed to edit their details, while other visitors have view-only access.
	 */
	private boolean isSubscriber = false;

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
	 * Initializes the controller.
	 * <p>
	 * The method saves the current controller instance and requests the current
	 * visitor's details from the server, using the visitor ID stored in the client
	 * session.
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
	 * Loads the visitor details into the screen fields.
	 * <p>
	 * All visitors can view their details. If the visitor type is Subscriber, the
	 * editable fields remain enabled. Otherwise, the fields are set to view-only
	 * mode.
	 *
	 * @param visitorDetails an ArrayList containing the visitor's details returned
	 *                       from the server
	 */
	public void loadVisitorDetails(ArrayList<String> visitorDetails) {
		if (visitorDetails == null || visitorDetails.isEmpty()) {
			statusLabel.setText("Could not load visitor details.");
			return;
		}

		firstNameField.setText(visitorDetails.get(1));
		lastNameField.setText(visitorDetails.get(2));
		phoneField.setText(visitorDetails.get(3));
		emailField.setText(visitorDetails.get(4));
		visitorTypeField.setText(visitorDetails.get(5));
		creditCardField.setText(visitorDetails.get(8));

		if (visitorDetails.size() > 8 && visitorDetails.get(8) != null) {
			creditCardField.setText(visitorDetails.get(8));
		} else {
			creditCardField.setText("");
		}

		// Check if the current visitor is a subscriber
		isSubscriber = "Subscriber".equalsIgnoreCase(visitorDetails.get(5));

		// Everyone can view details, but only subscribers can edit
		firstNameField.setEditable(isSubscriber);
		lastNameField.setEditable(isSubscriber);
		phoneField.setEditable(isSubscriber);
		emailField.setEditable(isSubscriber);
		creditCardField.setEditable(isSubscriber);

		// Visitor type should never be edited
		visitorTypeField.setEditable(false);

		if (isSubscriber) {
			statusLabel.setStyle("-fx-text-fill: #27ae60;");
			statusLabel.setText("You can update your personal details.");
		} else {
			statusLabel.setStyle("-fx-text-fill: #7f8c8d;");
			statusLabel.setText("View only. Only subscribers can update personal details.");
		}
	}

	/**
	 * Handles the save button action.
	 * <p>
	 * Only subscribers are allowed to update their personal details. The method
	 * validates the updated input fields and sends an update request to the server
	 * if all values are valid.
	 * 
	 * @param event The action event triggered by the save changes button
	 */
	@FXML
	void saveChanges(ActionEvent event) {
		if (!isSubscriber) {
			statusLabel.setStyle("-fx-text-fill: #e74c3c;");
			statusLabel.setText("Only subscribers can update personal details.");
			return;
		}
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

		if (!creditCard.matches("\\d{16}")) {
			statusLabel.setStyle("-fx-text-fill: #e74c3c;");
			statusLabel.setText("Credit card must be exactly 16 digits");
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
	 * Handles the update result returned from the server.
	 * <p>
	 * If the update succeeded, the screen displays a success message and updates
	 * the current visitor object stored in the client session. If the update
	 * failed, an error message is displayed.
	 *
	 * @param success true if the update was completed successfully; false otherwise
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

	@FXML
	void goBack(ActionEvent event) {
		Message msg = new Message("FETCH_VISITOR_ORDERS", GoNatureClient.currentVisitor.getVisitorId());

		try {
			ClientUI.send(msg);
		} catch (Exception e) {
			System.out.println("Error sending message to server");
			e.printStackTrace();
		}
	}
}