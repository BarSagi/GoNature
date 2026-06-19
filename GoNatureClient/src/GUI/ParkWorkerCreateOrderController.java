package GUI;

import java.io.IOException;
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

	// ======================
	// STATE
	// ======================
	public static String cachedVisitorType = "Individual";
	public static ParkWorkerCreateOrderController instance;

	private boolean orderCreatedSuccessfully = false;
	private double lastCalculatedPrice = 0;
	private String selectedPaymentMethod;

	// ======================
	// INIT
	// ======================
	@FXML
	public void initialize() {
		instance=this;
		parkComboBox.getItems().addAll("Banias", "Ein Gedi", "Yehudia");

		timeComboBox.getItems().addAll(
				"09:00", "10:00", "11:00", "12:00",
				"13:00", "14:00", "15:00", "16:00"
		);

		paymentComboBox.getItems().addAll("Cash", "Credit Card");

		statusLabel.setText("");
	}

	// ======================
	// SUBMIT ORDER
	// ======================
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

			selectedPaymentMethod = paymentMethod;

			// reset state before request
			orderCreatedSuccessfully = false;
			lastCalculatedPrice = 0;

			// ======================
			// VALIDATION
			// ======================
			if (visitorId.isEmpty() || park == null || visitDate == null ||
					time == null || paymentMethod == null ||
					visitorCount.isEmpty() || email.isEmpty()) {

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

			// ======================
			// CREATE ORDER
			// ======================
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

		} catch (Exception e) {
			statusLabel.setText("Failed to submit order.");
			e.printStackTrace();
		}
	}

	// ======================
	// CALLBACK FROM SERVER
	// ======================
	public void handleOrderResult(boolean success, double price) {

		if (!success) {
			orderCreatedSuccessfully = false;
			lastCalculatedPrice = 0;

			Platform.runLater(() ->
					statusLabel.setText("Order failed.")
			);
			return;
		}

		// order succeeded
		orderCreatedSuccessfully = true;
		lastCalculatedPrice = price;

		Platform.runLater(() -> {

			Alert alert = new Alert(Alert.AlertType.INFORMATION);
			alert.setTitle("SIMULATION");
			alert.setHeaderText("Order Approved");
			alert.setContentText("Total price: " + lastCalculatedPrice + " NIS\n");

			alert.showAndWait();
		});
	}

	// ======================
	// VISITOR TYPE CALLBACK
	// ======================
	public static void handleVisitorTypeResult(String type) {
	    cachedVisitorType = (type != null) ? type : "Individual";

	    if (instance != null) {
	        instance.continueSubmitOrder();
	    }
	}
	
	public void continueSubmitOrder() {

	    ArrayList<String> orderData = new ArrayList<>();
	    orderData.add(visitorIdField.getText().trim());
	    orderData.add(parkComboBox.getValue());
	    orderData.add(visitDatePicker.getValue().toString());
	    orderData.add(timeComboBox.getValue());
	    orderData.add(visitorCountField.getText().trim());
	    orderData.add(emailField.getText().trim());
	    orderData.add(cachedVisitorType);
	    orderData.add(paymentComboBox.getValue());

	    try {
			ClientUI.client.sendToServer(new Message("SUBMIT_NEW_ORDER", orderData));
		} catch (IOException e) {
			e.printStackTrace();
		}

	    statusLabel.setText("Processing order...");
	}
}