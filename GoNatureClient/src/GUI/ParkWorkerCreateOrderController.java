package GUI;

import java.time.LocalDate;
import java.util.ArrayList;

import Client.ClientUI;
import Common.Message;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class ParkWorkerCreateOrderController {

	@FXML
	private ComboBox<String> parkComboBox;

	@FXML
	private DatePicker visitDatePicker;

	@FXML
	private ComboBox<String> timeComboBox;

	@FXML
	private TextField visitorCountField;

	@FXML
	private TextField emailField;

	@FXML
	private TextField visitorIdField;

	@FXML
	private Label statusLabel;

	@FXML
	private ComboBox<String> paymentComboBox;

	public static ParkWorkerCreateOrderController instance;
	public static String cachedVisitorType = "Individual";

	private boolean orderCreatedSuccessfully = false;

	private String ID;
	private String numOfVisitors;
	private String lastPaymentMethod;

	@FXML
	public void initialize() {

		instance = this;

		try {
		    ClientUI.client.sendToServer(new Message("GET_ALL_PARKS", null));
		} catch (Exception e) {
		    e.printStackTrace();
		}

		timeComboBox.getItems().addAll("09:00", "10:00", "11:00", "12:00", "13:00", "14:00", "15:00", "16:00");

		paymentComboBox.getItems().addAll("Cash", "Credit Card");

		statusLabel.setText("");
	}

	@FXML
	void submitOrder(ActionEvent event) {

		try {

			String visitorId = visitorIdField.getText().trim();
			String park = parkComboBox.getValue();
			LocalDate visitDate = visitDatePicker.getValue();
			String time = timeComboBox.getValue();
			String visitorCount = visitorCountField.getText().trim();
			String email = emailField.getText().trim();
			String paymentMethod = paymentComboBox.getValue();
			numOfVisitors = visitorCount;
			ID = visitorId;

			if (visitorId.isEmpty() || park == null || visitDate == null || time == null || paymentMethod == null
					|| visitorCount.isEmpty() || email.isEmpty()) {

				statusLabel.setText("Please fill in all fields.");
				return;
			}

			if (!visitorId.matches("\\d{9}")) {
				statusLabel.setText("Visitor ID must be exactly 9 digits.");
				return;
			}

			if (visitDate.isBefore(LocalDate.now())) {
				statusLabel.setText("You cannot select a past date.");
				return;
			}

			if (!visitorCount.matches("\\d+") || Integer.parseInt(visitorCount) <= 0) {
				statusLabel.setText("Visitor count must be a positive number.");
				return;
			}

			if (!email.matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")) {
				statusLabel.setText("Please enter a valid email address.");
				return;
			}

			lastPaymentMethod = paymentMethod;

			orderCreatedSuccessfully = false;

			ArrayList<String> orderData = new ArrayList<>();
			orderData.add(visitorId);
			orderData.add(park);
			orderData.add(visitDate.toString());
			orderData.add(time);
			orderData.add(visitorCount);
			orderData.add(email);
			orderData.add(cachedVisitorType);
			orderData.add(paymentMethod);

			ClientUI.client.sendToServer(new Message("SUBMIT_NEW_ORDER", orderData));

			statusLabel.setText("Processing order...");
			
			ArrayList<String> paymentData = new ArrayList<>();
			paymentData.add(ID);
			paymentData.add(numOfVisitors);
			paymentData.add(lastPaymentMethod);

			ClientUI.client.sendToServer(new Message("CALCULATE_PRICE_PREORDER", paymentData));

			
		} catch (NumberFormatException e) {
			statusLabel.setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold;");
			statusLabel.setText("Visitor count must be a valid number.");
		} catch (Exception e) {
			statusLabel.setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold;");
			statusLabel.setText("Connection error with the server.");
			e.printStackTrace();
		}
	}

	public void handleOrderResult(boolean success, String reason) {
		if (success) {
			orderCreatedSuccessfully = true;

		    statusLabel.setStyle("-fx-text-fill: #27ae60; -fx-font-weight: bold;");
		    statusLabel.setText("Order registered successfully!");
		    visitorIdField.clear();
		    visitorCountField.clear();
		} else {
			orderCreatedSuccessfully = false;

		    statusLabel.setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold;");
		    statusLabel.setText(reason != null ? reason : "Registration failed. Park may be full.");
		}
	}

	public void handlePriceResult(double price) {

		if (!orderCreatedSuccessfully) {
			return;
		}

		Platform.runLater(() -> {

			Alert alert = new Alert(Alert.AlertType.INFORMATION);
			alert.setTitle("SIMULATION");
			alert.setHeaderText("Order Price");
			alert.setContentText("Total price: " + price + " NIS");

			alert.showAndWait();
		});
	}

	public static void handleVisitorTypeResult(String type) {
		cachedVisitorType = (type != null) ? type : "Individual";
	}

	public void loadParks(ArrayList<String> parks) {

	    Platform.runLater(() -> {

	        if (parks == null || parks.isEmpty()) {
	            statusLabel.setText("No parks available.");
	            return;
	        }

	        parkComboBox.getItems().clear();
	        parkComboBox.getItems().addAll(parks);
	    });
	}
}