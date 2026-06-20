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

	private boolean orderCreatedSuccessfully = false;
	public static double lastCalculatedPrice;
	public static String selectedPaymentMethod;
	public static String cachedVisitorType = "Individual";

	public static CreateOrderController instance;

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

		paymentComboBox.setItems(FXCollections.observableArrayList("Credit Card", "Cash"));

		int maxVisitors = 100;
		int minVisitors = 1;

		if (GoNatureClient.currentVisitor != null && "Guide".equals(GoNatureClient.currentVisitor.getVisitorType())) {
			maxVisitors = 16;
			minVisitors = 2;
		}

		visitorsSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(minVisitors, maxVisitors, minVisitors));

	}

	@FXML
	void submitOrder(ActionEvent event) {

		try {
			errorLabel.setVisible(false);

			String selectedPark = parkComboBox.getValue();
			LocalDate selectedDate = datePicker.getValue();
			String selectedTime = timeComboBox.getValue();
			String visitorsAmount = String.valueOf(visitorsSpinner.getValue());
			String paymentMethod = paymentComboBox.getValue();

			if (selectedPark == null || selectedDate == null || selectedTime == null || paymentMethod == null
					|| visitorsAmount.trim().isEmpty()) {

				showError("Please fill in all fields.");
				return;
			}

			if (selectedDate.isBefore(LocalDate.now())) {
				showError("You cannot select a past date.");
				return;
			}

			if (selectedDate.equals(LocalDate.now())) {
				LocalTime chosenTime = LocalTime.parse(selectedTime);
				if (chosenTime.isBefore(LocalTime.now())) {
					showError("You cannot select a time that has already passed.");
					return;
				}
			}

			String visitorId = GoNatureClient.currentVisitor.getVisitorId();

			ArrayList<String> newOrder = new ArrayList<>();
			newOrder.add(visitorId);
			newOrder.add(selectedPark);
			newOrder.add(selectedDate.toString());
			newOrder.add(selectedTime);
			newOrder.add(visitorsAmount);
			newOrder.add(GoNatureClient.currentVisitor.getEmail());
			newOrder.add(cachedVisitorType);

			ClientUI.send(new Message("SUBMIT_NEW_ORDER", newOrder));

			ArrayList<String> paymentData = new ArrayList<>();
			paymentData.add(visitorId);
			paymentData.add(visitorsAmount);
			paymentData.add(paymentMethod);

			ClientUI.send(new Message("CALCULATE_PRICE_PREORDER", paymentData));

		} catch (NumberFormatException e) {
			e.printStackTrace();

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void handleOrderResult(boolean success, String reason) {

		if (success) {
			orderCreatedSuccessfully = true;

			errorLabel.setStyle("-fx-text-fill: #27ae60;");
			errorLabel.setText("Order created successfully!");
		} else {
			orderCreatedSuccessfully = false;

			errorLabel.setStyle("-fx-text-fill: #e74c3c;");
			errorLabel.setText(reason != null ? reason : "Order failed.");

			return;
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
			alert.setContentText("Total price: " + price + " NIS\n");

			alert.showAndWait();
		});
	}

	public void loadParks(ArrayList<String> parks) {

		Platform.runLater(() -> {

			if (parks == null || parks.isEmpty())
				return;

			parkComboBox.getItems().clear();
			parkComboBox.getItems().addAll(parks);
		});
	}

	private void showError(String message) {
		errorLabel.setText(message);
		errorLabel.setVisible(true);
	}

	@FXML
	void goBack(ActionEvent event) {
		if (GoNatureClient.currentVisitor != null) {
			try {
				ClientUI.send(new Message("FETCH_VISITOR_ORDERS", GoNatureClient.currentVisitor.getVisitorId()));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public static void handleVisitorTypeResult(String type) {
		cachedVisitorType = (type != null) ? type : "Individual";
	}
}