package GUI;

import Client.ClientUI;
import Client.GoNatureClient;
import Common.Message;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import java.time.LocalDate;
import java.util.ArrayList;
import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Controller for the park worker's casual visit registration screen. Manages
 * the entry of casual visitors by validating their information and
 * communicating with the server to register the visit and calculate the price.
 */
public class ParkWorkerCreateCasualVisitController {

	/**
	 * Static instance of this controller for external access.
	 */
	public static ParkWorkerCreateCasualVisitController instance;

	
	@FXML
	private TextField visitorIdField;

	@FXML
	private TextField visitorCountField;

	@FXML
	private Label parkLabel;

	@FXML
	private Label statusLabel;

	@FXML
	private ComboBox<String> paymentComboBox;

	/**
	 * Flag indicating whether the visit was successfully created.
	 */
	private boolean visitCreatedSuccessfully = false;

	/**
	 * Initializes the controller, populates the payment method ComboBox, and sets
	 * the park label based on the current employee's affiliation.
	 */
	@FXML
	public void initialize() {
		instance = this;
		paymentComboBox.setItems(FXCollections.observableArrayList("Cash", "Credit Card"));
		parkLabel.setText(GoNatureClient.currentEmployee.getAffiliation());
	}

	/**
	 * Validates the visitor information and submits the casual visit registration
	 * to the server.
	 *
	 * @param event The action event triggered by the submit button.
	 */
	@FXML
	void submitOrder(ActionEvent event) {
		String visitorId = visitorIdField.getText().trim();
		String countStr = visitorCountField.getText().trim();
		String paymentMethod = paymentComboBox.getValue();

		// Check park opening hours according to the computer clock
		LocalTime now = LocalTime.now();
		LocalTime openingTime = LocalTime.of(9, 0);
		LocalTime closingTime = LocalTime.of(16, 0);

		if (now.isBefore(openingTime) || !now.isBefore(closingTime)) {
			statusLabel.setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold;");
			statusLabel.setText("Casual visits are allowed only between 09:00 and 16:00.");
			return;
		}

		// Reset label color to neutral gray during processing
		statusLabel.setStyle("-fx-text-fill: #7f8c8d; -fx-font-weight: normal;");

		// Basic input validation
		if (visitorId.isEmpty() || countStr.isEmpty()) {
			statusLabel.setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold;");
			statusLabel.setText("Please fill in all fields.");
			return;}
		}

	/**
	 * Callback method called by the client architecture when server confirmation
	 * arrives.
	 *
	 * @param success Indicates if the registration was successful.
	 * @param reason  The error reason if registration failed.
	 */
	public void handleRegistrationResult(boolean success, String reason) {
		if (success) {
			visitCreatedSuccessfully = true;

			statusLabel.setStyle("-fx-text-fill: #27ae60; -fx-font-weight: bold;");
			statusLabel.setText("Casual visit registered successfully!");
			visitorIdField.clear();
			visitorCountField.clear();
		} else {
			visitCreatedSuccessfully = false;

			statusLabel.setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold;");
			statusLabel.setText(reason != null ? reason : "Registration failed. Park may be full.");
		}
	}

	/**
	 * Callback method called when the calculated price is returned from the server.
	 *
	 * @param price The total price to be displayed.
	 */
	public void handlePriceResult(double price) {

		if (!visitCreatedSuccessfully) {
			return;
		}
		Platform.runLater(() -> {

			Alert alert = new Alert(Alert.AlertType.INFORMATION);
			alert.setTitle("SIMULATION");
			alert.setHeaderText("Casual Visit Price");
			alert.setContentText("Total price: " + price + " NIS\n");

			alert.showAndWait();
		});
	}
}