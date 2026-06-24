package GUI;

import java.util.ArrayList;

import Client.ClientUI;
import Common.Message;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class ServiceRepSearchSubscriberController {

	private static ServiceRepSearchSubscriberController instance;

	@FXML
	private TextField subscriberIdField;

	@FXML
	private Label idLabel;

	@FXML
	private Label firstNameLabel;

	@FXML
	private Label lastNameLabel;

	@FXML
	private Label phoneLabel;

	@FXML
	private Label emailLabel;

	@FXML
	private Label typeLabel;

	@FXML
	private Label subscriptionNumberLabel;

	@FXML
	private Label familyMembersLabel;

	public ServiceRepSearchSubscriberController() {
		instance = this;
	}

	public static ServiceRepSearchSubscriberController getInstance() {
		return instance;
	}

	@FXML
	private void searchSubscriber() {
		String subscriberId = subscriberIdField.getText().trim();

		if (subscriberId.isEmpty()) {
			showAlert("Please enter subscriber ID");
			return;
		}

		try {
			ClientUI.send(new Message("FETCH_SUBSCRIBER_BY_ID", subscriberId));
		} catch (Exception e) {
			showAlert("Failed to send request to server.");
			e.printStackTrace();
		}
	}

	public void displaySubscriberInfo(ArrayList<String> info) {
		idLabel.setText(info.get(0));
		firstNameLabel.setText(info.get(1));
		lastNameLabel.setText(info.get(2));
		phoneLabel.setText(info.get(3));
		emailLabel.setText(info.get(4));
		typeLabel.setText(info.get(5));
		subscriptionNumberLabel.setText(info.get(6));
		familyMembersLabel.setText(info.get(7));
	}

	public void showSubscriberNotFound() {
		showAlert("Subscriber not found");
	}

	private void showAlert(String msg) {
		Alert alert = new Alert(Alert.AlertType.WARNING);
		alert.setContentText(msg);
		alert.showAndWait();
	}
}