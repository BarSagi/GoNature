package GUI;

import java.net.URL;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.ResourceBundle;

import Client.ClientUI;
import Client.GoNatureClient;
import Common.Message;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

/**
 * Controller class for the order creation screen. Manages the user interface
 * for inputting visit details, validating inputs, and communicating with the
 * server to submit orders and calculate pricing.
 */
public class CreateOrderController implements Initializable {

	@FXML
	private ComboBox<String> parkComboBox;
	@FXML
	private DatePicker datePicker;
	@FXML
	private ComboBox<String> timeComboBox;
	@FXML
	private Spinner<Integer> visitorsSpinner;
	@FXML
	private Label errorLabel;
	@FXML
	private ComboBox<String> paymentComboBox;
	@FXML
	public Button createOrderButton;

	/**
	 * Static instance of this controller for external access.
	 */
	public static CreateOrderController instance;

	private boolean orderCreatedSuccessfully = false;

	private String visitorId;
	private String selectedPark;
	private String selectedDate;
	private String selectedTime;
	private String visitorsAmount;
	private String paymentMethod;

	/**
	 * Cached type of the visitor (e.g., "Individual" or "OrganizedGroup").
	 */
	public static String cachedVisitorType = "Individual";

	/**
	 * Initializes the controller, populates ComboBoxes, and configures the visitor
	 * spinner.
	 */
	@Override
	public void initialize(URL location, ResourceBundle resources) {

		instance = this;

		try {
			ClientUI.send(new Message("GET_ALL_PARKS", null));
		} catch (Exception e) {
			e.printStackTrace();
		}

		ObservableList<String> times = FXCollections.observableArrayList("09:00", "10:00", "11:00", "12:00", "13:00",
				"14:00", "15:00", "16:00");

		timeComboBox.setItems(times);
		paymentComboBox.setItems(FXCollections.observableArrayList("Pay Later", "Pay Now"));

		int maxVisitors = 100;
		int minVisitors = 1;

		if (GoNatureClient.currentVisitor != null && "Guide".equals(GoNatureClient.currentVisitor.getVisitorType())) {
			maxVisitors = 15;
			minVisitors = 1;
		}

		visitorsSpinner.setValueFactory(
				new SpinnerValueFactory.IntegerSpinnerValueFactory(minVisitors, maxVisitors, minVisitors));
	}

	/**
	 * Validates order inputs and initiates the order submission flow.
	 *
	 * @param event The action event triggered by the submit button.
	 */
	@FXML
	void submitOrder(ActionEvent event) {
		createOrderButton.setDisable(true);

		try {
			errorLabel.setVisible(false);

			selectedPark = parkComboBox.getValue();
			LocalDate date = datePicker.getValue();
			selectedTime = timeComboBox.getValue();
			visitorsAmount = String.valueOf(visitorsSpinner.getValue());
			paymentMethod = paymentComboBox.getValue();

			if (selectedPark == null || date == null || selectedTime == null || paymentMethod == null
					|| visitorsAmount.trim().isEmpty()) {
				showError("Please fill in all fields.");
				createOrderButton.setDisable(false);
				return;
			}

			if (date.isBefore(LocalDate.now())) {
				showError("You cannot select a past date.");
				createOrderButton.setDisable(false);
				return;
			}

			LocalTime chosenTime = LocalTime.parse(selectedTime);
			if (date.equals(LocalDate.now()) && chosenTime.isBefore(LocalTime.now())) {
				showError("You cannot select a past time.");
				createOrderButton.setDisable(false);
				return;
			}

			visitorId = GoNatureClient.currentVisitor.getVisitorId();
			selectedDate = date.toString();

			ClientUI.send(new Message("GET_VISITOR_TYPE", visitorId));

		} catch (Exception e) {
			e.printStackTrace();
			showError("Submit failed");
		}
	}

	/**
	 * Handles the result of the visitor type check and proceeds with order
	 * creation.
	 *
	 * @param type The visitor type string returned by the server.
	 */
	public void handleVisitorTypeResult(String type) {

		if (type.equals("Guide"))
			cachedVisitorType = "OrganizedGroup";
		else
			cachedVisitorType = "Individual";

		Platform.runLater(() -> errorLabel.setText("Creating order..."));

		submitOrderToServer();
	}

	/**
	 * Packages order data and sends it to the server.
	 */
	private void submitOrderToServer() {

		ArrayList<String> newOrder = new ArrayList<>();
		newOrder.add(visitorId);
		newOrder.add(selectedPark);
		newOrder.add(selectedDate);
		newOrder.add(selectedTime);
		newOrder.add(visitorsAmount);
		newOrder.add(cachedVisitorType);
		newOrder.add(GoNatureClient.currentVisitor.getEmail());
		newOrder.add(paymentMethod);

		try {
			ClientUI.send(new Message("SUBMIT_NEW_ORDER", newOrder));
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
			Platform.runLater(() -> showError(reason != null ? reason : "Order failed"));
			createOrderButton.setDisable(false);
			return;
		}

		orderCreatedSuccessfully = true;

		Platform.runLater(() -> {
			errorLabel.setStyle("-fx-text-fill: #27ae60;");
			errorLabel.setText("Order created successfully!");
		});

		calculatePrice();
	}

	/**
	 * Triggers the pricing calculation process.
	 */
	private void calculatePrice() {

		ArrayList<String> paymentData = new ArrayList<>();
		paymentData.add(visitorId);
		paymentData.add(visitorsAmount);
		paymentData.add(paymentMethod);
		paymentData.add(selectedPark);
		paymentData.add(LocalDate.now().toString());

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
				showError("No parks available");
				return;
			}

			parkComboBox.getItems().setAll(parks);
		});
	}

	/**
	 * Displays an error message on the GUI.
	 *
	 * @param msg The error message text.
	 */
	private void showError(String msg) {
		errorLabel.setStyle("-fx-text-fill: #e74c3c;");
		errorLabel.setText(msg);
		errorLabel.setVisible(true);
	}

	/**
	 * Navigates the user back to the visitor orders screen.
	 *
	 * @param event The action event triggered by the back button.
	 */
	@FXML
	void goBack(ActionEvent event) {
		try {
			if (GoNatureClient.currentVisitor != null) {
				ClientUI.send(new Message("FETCH_VISITOR_ORDERS", GoNatureClient.currentVisitor.getVisitorId()));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			instance = null;
		}
	}
}