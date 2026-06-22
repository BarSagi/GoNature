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

	public static CreateOrderController instance;

	private boolean orderCreatedSuccessfully = false;

	// state for async flow
	private String visitorId;
	private String selectedPark;
	private String selectedDate;
	private String selectedTime;
	private String visitorsAmount;
	private String paymentMethod;

	public static String cachedVisitorType = "Individual";

	@Override
	public void initialize(URL location, ResourceBundle resources) {

		instance = this;

		try {
			ClientUI.client.sendToServer(new Message("GET_ALL_PARKS", null));
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
			maxVisitors = 16;
			minVisitors = 2;
		}

		visitorsSpinner.setValueFactory(
				new SpinnerValueFactory.IntegerSpinnerValueFactory(minVisitors, maxVisitors, minVisitors));
	}

	@FXML
	void submitOrder(ActionEvent event) {

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
				return;
			}

			if (date.isBefore(LocalDate.now())) {
				showError("You cannot select a past date.");
				return;
			}

			LocalTime chosenTime = LocalTime.parse(selectedTime);
			if (date.equals(LocalDate.now()) && chosenTime.isBefore(LocalTime.now())) {
				showError("You cannot select a past time.");
				return;
			}

			visitorId = GoNatureClient.currentVisitor.getVisitorId();
			selectedDate = date.toString();

			// STEP 1 async
			ClientUI.client.sendToServer(new Message("GET_VISITOR_TYPE", visitorId));

		} catch (Exception e) {
			e.printStackTrace();
			showError("Connection error");
		}
	}

	public void handleVisitorTypeResult(String type) {

		if (type.equals("Guide")) {
			cachedVisitorType = "OrganizedGroup";
		} else if (type.equals("SmallGroup")) {
			cachedVisitorType = "SmallGroup";
		} else {
			cachedVisitorType = "Individual";
		}

		Platform.runLater(() -> errorLabel.setText("Creating order..."));

		submitOrderToServer();
	}

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

	public void handleOrderResult(boolean success, String reason) {

		if (!success) {
			Platform.runLater(() -> showError(reason != null ? reason : "Order failed"));
			return;
		}

		orderCreatedSuccessfully = true;

		Platform.runLater(() -> {
			errorLabel.setStyle("-fx-text-fill: #27ae60;");
			errorLabel.setText("Order created successfully!");
		});

		calculatePrice();
	}

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

	public void loadParks(ArrayList<String> parks) {

		Platform.runLater(() -> {
			if (parks == null || parks.isEmpty()) {
				showError("No parks available");
				return;
			}

			parkComboBox.getItems().setAll(parks);
		});
	}

	private void showError(String msg) {
		errorLabel.setStyle("-fx-text-fill: #e74c3c;");
		errorLabel.setText(msg);
		errorLabel.setVisible(true);
	}

	@FXML
	void goBack(ActionEvent event) {
		try {
			if (GoNatureClient.currentVisitor != null) {
				ClientUI.send(new Message("FETCH_VISITOR_ORDERS", GoNatureClient.currentVisitor.getVisitorId()));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}