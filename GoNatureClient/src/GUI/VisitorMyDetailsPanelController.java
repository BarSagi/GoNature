package GUI;

import java.util.ArrayList;

import Client.ClientUI;
import Client.GoNatureClient;
import Common.Message;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class VisitorMyDetailsPanelController {

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

		if (!phone.matches("\\d{10}")) {
			statusLabel.setStyle("-fx-text-fill: #e74c3c;");
			statusLabel.setText("Phone number must be exactly 10 digits.");
			return;
		}

		String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[a-zA-Z]{2,}$";
		if (!email.matches(emailRegex)) {
			statusLabel.setStyle("-fx-text-fill: #e74c3c;");
			statusLabel.setText("Please enter a valid email address.");
			return;
		}

		if (!creditCard.matches("\\d{16}")) {
			statusLabel.setStyle("-fx-text-fill: #e74c3c;");
			statusLabel.setText("Credit card must be exactly 16 digits.");
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