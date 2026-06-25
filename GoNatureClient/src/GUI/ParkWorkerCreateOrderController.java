package GUI;

import java.time.LocalDate;
import java.util.ArrayList;
import Client.ClientUI;
import Common.Message;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

/**
 * Controller for the order creation screen. Manages the user interface for
 * inputting visit details, validating inputs, and communicating with the server
 * to submit orders and calculate pricing.
 */
public class ParkWorkerCreateOrderController {

	@FXML
	private ComboBox<String> parkComboBox;
	@FXML
	private TextField emailField;
	@FXML
	private DatePicker visitDatePicker;
	@FXML
	private ComboBox<String> timeComboBox;
	@FXML
	private TextField visitorCountField;
	@FXML
	private TextField visitorIdField;
	@FXML
	private Label statusLabel;
	@FXML
	private ComboBox<String> paymentComboBox;
	@FXML
	private Spinner<Integer> visitorsSpinner;
	@FXML
	public Button btnSubmit;

	/**
	 * Static instance of this controller for external access.
	 */
	public static ParkWorkerCreateOrderController instance;

	private String pendingVisitorId;
	private String pendingParkName;
	private String pendingDate;
	private String pendingTime;
	private String pendingVisitorCount;
	private String pendingEmail;
	private String pendingPayment;

	private boolean orderCreatedSuccessfully = false;

	/**
	 * Cached type of the visitor (e.g., "Individual" or "OrganizedGroup").
	 */
	public static String cachedVisitorType = "Individual";

	/**
	 * Initializes the controller, populates ComboBoxes, and configures the visitor
	 * spinner.
	 */
	@FXML
	public void initialize() {

		instance = this;

		try {
			// NOTE: Rule #3 - Should use ClientUI.send()
			ClientUI.send(new Message("GET_ALL_PARKS", null));
		} catch (Exception e) {
			e.printStackTrace();
		}

		timeComboBox.getItems().addAll("09:00", "10:00", "11:00", "12:00", "13:00", "14:00", "15:00", "16:00");

		paymentComboBox.getItems().addAll("Pay Now", "Pay Later");

		statusLabel.setText("");
	}

	/**
	 * Validates order inputs and initiates the order submission flow. Disables the
	 * submit button to prevent duplicate submissions.
	 *
	 * @param event The action event triggered by the submit button.
	 */
	@FXML
	void submitOrder(ActionEvent event) {
		btnSubmit.setDisable(true);
		try {
			String visitorId = visitorIdField.getText().trim();
			String email = emailField.getText().trim();
			String parkName = parkComboBox.getValue();
			LocalDate visitDate = visitDatePicker.getValue();
			String time = timeComboBox.getValue();
			String visitorCount = visitorCountField.getText().trim();
			String paymentMethod = paymentComboBox.getValue();

			if (visitorId.isEmpty() || email.isEmpty() || parkName == null || visitDate == null || time == null
					|| paymentMethod == null || visitorCount.isEmpty()) {
				statusLabel.setText("Please fill in all fields.");
				btnSubmit.setDisable(false);
				return;
			}

			if (!visitorId.matches("\\d{9}")) {
				statusLabel.setText("Visitor ID must be exactly 9 digits.");
				btnSubmit.setDisable(false);
				return;
			}

			String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.com$";

			if (!email.matches(emailRegex)) {
				statusLabel.setText("Email must be valid and end with .com");
				return;
			}

			if (visitDate.isBefore(LocalDate.now())) {
				statusLabel.setText("Cannot select past date.");
				btnSubmit.setDisable(false);
				return;
			}

			if (!visitorCount.matches("\\d+") || Integer.parseInt(visitorCount) <= 0) {
				statusLabel.setText("Invalid visitor count.");
				btnSubmit.setDisable(false);
				return;
			}

			// Save state
			pendingVisitorId = visitorId;
			pendingParkName = parkName;
			pendingEmail = email;
			pendingDate = visitDate.toString();
			pendingTime = time;
			pendingVisitorCount = visitorCount;
			pendingPayment = paymentMethod;

			statusLabel.setText("Fetching visitor type...");

			ClientUI.send(new Message("GET_VISITOR_TYPE", visitorId));

		} catch (Exception e) {
			e.printStackTrace();
			statusLabel.setText("Server connection error.");
		}
	}

	/**
	 * Handles the result of the visitor type check and proceeds with order
	 * creation.
	 *
	 * @param type The visitor type string returned by the server.
	 */
	public void handleVisitorTypeResult(String type) {

		if (type.equals("Guide")) {
			cachedVisitorType = "OrganizedGroup";

			if (Integer.parseInt(pendingVisitorCount) < 1 || Integer.parseInt(pendingVisitorCount) > 15) {
				Platform.runLater(() -> statusLabel.setText("Invalid visitor count for guide. "));
				btnSubmit.setDisable(false);
				return;
			}
		} else {
			cachedVisitorType = "Individual";
			if (Integer.parseInt(pendingVisitorCount) > 100 || Integer.parseInt(pendingVisitorCount) < 1) {
				Platform.runLater(() -> statusLabel.setText("Invalid visitor count. "));
				btnSubmit.setDisable(false);
				return;
			}
		}

		Platform.runLater(() -> statusLabel.setText("Fetching email..."));

		try {
			ClientUI.send(new Message("GET_VISITOR_EMAIL", pendingVisitorId));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Handles the result of the visitor email check and proceeds with order
	 * creation.
	 *
	 * @param emailFromServer The email address retrieved from the server.
	 */
	public void handleVisitorEmailResult(String emailFromServer) {

		if (emailFromServer != null && !emailFromServer.isEmpty()) {
			pendingEmail = emailFromServer;
		} else {
			Platform.runLater(() -> statusLabel.setText("Failed to fetch email.\nVisitor might not be registered."));
			btnSubmit.setDisable(false);
			return;
		}

		Platform.runLater(() -> statusLabel.setText("Creating order..."));

		createOrder();
	}

	/**
	 * Packages order data and sends it to the server.
	 */
	private void createOrder() {

		ArrayList<String> orderData = new ArrayList<>();
		orderData.add(pendingVisitorId);
		orderData.add(pendingParkName);
		orderData.add(pendingDate);
		orderData.add(pendingTime);
		orderData.add(pendingVisitorCount);
		orderData.add(cachedVisitorType);
		orderData.add(pendingEmail);
		orderData.add(pendingPayment);

		try {
			ClientUI.send(new Message("SUBMIT_NEW_ORDER", orderData));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Handles the outcome of the order submission.
	 *
	 * @param success Whether the submission was successful.
	 * @param reason  The error reason if submission failed.
	 */
	public void handleOrderResult(boolean success, String reason) {

		if (!success) {
			Platform.runLater(() -> statusLabel.setText(reason != null ? reason : "Order failed"));
			btnSubmit.setDisable(false);
			return;
		}

		orderCreatedSuccessfully = true;

		Platform.runLater(() -> {
			statusLabel.setStyle("-fx-text-fill: #27ae60;");
			statusLabel.setText("Order created successfully!");
		});

		new Thread(this::calculatePriceAsync).start();
	}

	/**
	 * Triggers the pricing calculation process.
	 */
	private void calculatePriceAsync() {

		ArrayList<String> paymentData = new ArrayList<>();
		paymentData.add(pendingVisitorId);
		paymentData.add(pendingVisitorCount);
		paymentData.add(pendingPayment);
		paymentData.add(pendingParkName);
		paymentData.add(pendingDate);

		try {
			ClientUI.send(new Message("CALCULATE_PRICE_PREORDER", paymentData));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Displays the calculated price to the user.
	 *
	 * @param price The final price to display.
	 */
	public void handlePriceResult(double price) {

		if (!orderCreatedSuccessfully)
			return;

		Platform.runLater(() -> {
			Alert alert = new Alert(Alert.AlertType.INFORMATION);
			alert.setTitle("SIMULATION");
			alert.setHeaderText("Order Price");
			alert.setContentText("Total price: " + price + " NIS");
			alert.showAndWait();
		});
	}

	/**
	 * Updates the park selection ComboBox with available parks retrieved from the
	 * server.
	 *
	 * @param parks An ArrayList of available park names.
	 */
	public void loadParks(ArrayList<String> parks) {

		Platform.runLater(() -> {

			if (parks == null || parks.isEmpty()) {
				statusLabel.setText("No parks available.");
				return;
			}

			parkComboBox.getItems().setAll(parks);
		});
	}
}