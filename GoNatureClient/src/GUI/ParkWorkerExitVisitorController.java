package GUI;

import Client.ClientUI;
import Client.GoNatureClient;
import Common.Message;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.application.Platform;
import java.util.ArrayList;
import javafx.scene.control.Alert;

/**
 * Controller for the visitor exit registration screen. Handles the registration
 * of visitors leaving the park by capturing the visitor's identifier and number
 * of people exiting, then communicating with the server to update the park
 * status.
 */
public class ParkWorkerExitVisitorController {

	/**
	 * Static instance of this controller for external access.
	 */
	public static ParkWorkerExitVisitorController instance;

	@FXML
	private TextField identifierField;

	@FXML
	private TextField exitAmountField;

	@FXML
	private Label statusLabel;

	/**
	 * Initializes the controller and clears the status label.
	 */
	@FXML
	public void initialize() {
		instance = this;
		statusLabel.setText("");
	}

	/**
	 * Validates the visitor identifier and exit amount, then sends an exit request
	 * to the server.
	 *
	 * @param event The action event triggered by the confirm button.
	 */
	@FXML
	void confirmExit(ActionEvent event) {

		String identifier = identifierField.getText().trim();
		String amountStr = exitAmountField.getText().trim();

		// Check that neither field is empty
		if (identifier.isEmpty() || amountStr.isEmpty()) {
			statusLabel.setText("Please enter identifier and amount.");
			statusLabel.setStyle("-fx-text-fill: red;");
			return;
		}

		if (!identifier.matches("^[A-Z0-9]{1,9}$")) {
			statusLabel.setText("Invalid identifier format.");
			statusLabel.setStyle("-fx-text-fill: red;");
			return;
		}

		// Validate amount (Ensure it's a valid positive number starting from 1)
		if (!amountStr.matches("^[1-9][0-9]*$")) {
			statusLabel.setText("Amount must be a valid number greater than 0.");
			statusLabel.setStyle("-fx-text-fill: red;");
			return;
		}

		try {
			ArrayList<String> data = new ArrayList<>();
			data.add(identifier);

			String currentParkId = GoNatureClient.currentEmployee.getAffiliation();
			data.add(currentParkId);

			data.add(amountStr);

			Message msg = new Message("EXIT_VISITOR", data);
			ClientUI.send(msg);

			statusLabel.setText("Exit request sent to server...");
			statusLabel.setStyle("-fx-text-fill: blue;");
		} catch (Exception e) {
			statusLabel.setText("Failed to send exit request.");
			statusLabel.setStyle("-fx-text-fill: red;");
			e.printStackTrace();
		}
	}

	/**
	 * Displays the status of the visitor exit request. Handles both successful
	 * exits and cases where payment collection is still required.
	 *
	 * @param text The status message returned from the server.
	 */
	public void showStatus(String text) {
		Platform.runLater(() -> {
			if (text.startsWith("Success_Pay_")) {
				statusLabel.setStyle("-fx-text-fill: green;");
				statusLabel.setText("Visitor entered successfully! Payment required.");
				identifierField.clear();

				String[] parts = text.split("_");
				String priceStr = parts[2];
				String orderIdStr = parts[3];

				Alert alert = new Alert(Alert.AlertType.WARNING);
				alert.setTitle("Collection Required");
				alert.setHeaderText("Unpaid Order - Collect Payment Now");
				alert.setContentText("The visitor has not paid yet.\nTotal price to collect: " + priceStr + " NIS");
				alert.showAndWait();

				try {
					statusLabel.setText("Recording payment in database...");
					Message updatePaidMsg = new Message("UPDATE_ORDER_PAID", orderIdStr);

					ClientUI.send(updatePaidMsg);
				} catch (Exception e) {
					statusLabel.setText("Failed to send payment update to server.");
					statusLabel.setStyle("-fx-text-fill: red;");
					e.printStackTrace();
				}

			} else if (text.equals("Success")) {
				statusLabel.setStyle("-fx-text-fill: green;");
				statusLabel.setText("Visitor entered successfully! (Paid)");
				identifierField.clear();

				Alert alert = new Alert(Alert.AlertType.INFORMATION);
				alert.setTitle("Information Dialog");
				alert.setHeaderText(null);
				alert.setContentText("Client has already paid!");
				alert.showAndWait();
			} else {
				statusLabel.setStyle("-fx-text-fill: red;");
				statusLabel.setText(text);
			}
		});
	}
}