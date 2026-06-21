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

public class ParkWorkerCreateCasualVisitController {

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
	
	
	private boolean visitCreatedSuccessfully = false;

	@FXML
	public void initialize() {
		instance = this;
		paymentComboBox.setItems(FXCollections.observableArrayList("Cash", "Credit Card"));
		parkLabel.setText(GoNatureClient.currentEmployee.getAffiliation());
	}

	@FXML
	void submitOrder(ActionEvent event) {
		String visitorId = visitorIdField.getText().trim();
		String countStr = visitorCountField.getText().trim();
		String paymentMethod = paymentComboBox.getValue();

		// Reset label color to neutral gray during processing
		statusLabel.setStyle("-fx-text-fill: #7f8c8d; -fx-font-weight: normal;");

		// Basic input validation
		if (visitorId.isEmpty() || countStr.isEmpty()) {
			statusLabel.setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold;");
			statusLabel.setText("Please fill in all fields.");
			return;
		}

		if (!visitorId.matches("\\d{9}")) {
			statusLabel.setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold;");
			statusLabel.setText("Visitor ID must be exactly 9 digits.");
			return;
		}
		if (paymentMethod == null) {
		    statusLabel.setText("Please select payment method.");
		    return;
		}

		try {
			int visitorCount = Integer.parseInt(countStr);
			if (visitorCount <= 0) {
				statusLabel.setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold;");
				statusLabel.setText("Visitor count must be greater than 0.");
				return;
			}

			String parkName = GoNatureClient.currentEmployee.getAffiliation();

			// Pack data into an ArrayList to transfer safely across the network stream
			ArrayList<String> data = new ArrayList<>();
			data.add(parkName);
			data.add(visitorId);
			data.add(String.valueOf(visitorCount));

			// Send standard dynamic server message request
			Message msg = new Message("CREATE_CASUAL_VISIT", data);
			ClientUI.client.sendToServer(msg);
			statusLabel.setText("Processing casual entry...");

			ArrayList<String> priceData = new ArrayList<>();
			priceData.add(visitorId);
			priceData.add(String.valueOf(visitorCount));
			priceData.add(parkName);
			priceData.add(LocalDate.now().toString());

			Message priceMsg = new Message("CALCULATE_PRICE_CASUAL", priceData);
			ClientUI.client.sendToServer(priceMsg);

		} catch (NumberFormatException e) {
			statusLabel.setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold;");
			statusLabel.setText("Visitor count must be a valid number.");
		} catch (Exception e) {
			statusLabel.setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold;");
			statusLabel.setText("Connection error with the server.");
			e.printStackTrace();
		}
	}

	/**
	 * Callback method called by the client architecture when server confirmation
	 * arrives
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