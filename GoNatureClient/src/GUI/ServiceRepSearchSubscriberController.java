package GUI;

import Client.ClientUI;
import Common.Message;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.util.ArrayList;

/**
 * Controller for the service representative's subscriber search interface.
 * Facilitates searching for specific subscribers by ID and displays their
 * account details upon successful retrieval from the server.
 */
public class ServiceRepSearchSubscriberController {

	/**
	 * Static instance of this controller for external access.
	 */
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

	/**
	 * Default constructor that initializes the singleton instance.
	 */
	public ServiceRepSearchSubscriberController() {
		instance = this;
	}

	/**
	 * Returns the singleton instance of the controller.
	 *
	 * @return The current ServiceRepSearchSubscriberController instance.
	 */
	public static ServiceRepSearchSubscriberController getInstance() {
		return instance;
	}

	/**
	 * Initiates a search for a subscriber by sending their ID to the server.
	 */
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

	/**
	 * Updates the UI components with the subscriber information retrieved from the
	 * server.
	 *
	 * @param info An ArrayList containing the subscriber's detailed information.
	 */
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

	/**
	 * Displays an alert message when a subscriber cannot be found.
	 */
	public void showSubscriberNotFound() {
		showAlert("Subscriber not found");
	}

	/**
	 * Displays an alert dialog with the provided message.
	 *
	 * @param msg The message to display in the alert.
	 */
	private void showAlert(String msg) {
		Alert alert = new Alert(Alert.AlertType.WARNING);
		alert.setContentText(msg);
		alert.showAndWait();
	}
}