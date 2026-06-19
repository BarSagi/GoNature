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

	public static ArrayList<String> lastOrderData;

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

	@Override
	public void initialize(URL location, ResourceBundle resources) {

		ObservableList<String> parks = FXCollections.observableArrayList("Carmel", "Banias", "Hula");
		parkComboBox.setItems(parks);

		ObservableList<String> times = FXCollections.observableArrayList("09:00", "10:00", "11:00", "12:00", "13:00",
				"14:00", "15:00", "16:00");
		timeComboBox.setItems(times);

		paymentComboBox.setItems(FXCollections.observableArrayList("Credit Card", "Cash"));

		int maxVisitors = 100;

		if (GoNatureClient.currentVisitor != null && "Guide".equals(GoNatureClient.currentVisitor.getVisitorType())) {
			maxVisitors = 16;
		}

		visitorsSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, maxVisitors, 1));
	}

	@FXML
	void submitOrder(ActionEvent event) {

		orderCreatedSuccessfully = false;
		
		errorLabel.setVisible(false);

		String selectedPark = parkComboBox.getValue();
		LocalDate selectedDate = datePicker.getValue();
		String selectedTime = timeComboBox.getValue();
		String visitorsAmount = String.valueOf(visitorsSpinner.getValue());
		String paymentMethod = paymentComboBox.getValue();

		selectedPaymentMethod = paymentMethod;

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

		String visitorType = GoNatureClient.currentVisitor.getVisitorType();

		boolean subscriber = GoNatureClient.currentVisitor.getSubNumber() != 0 && !visitorType.equals("Guide");

		String visitType = "Guide".equals(visitorType) ? "GUIDE_PREORDER" : "REGULAR_PREORDER";

		boolean prepaid = "Guide".equals(visitorType) && "Credit Card".equals(paymentMethod);

		String visitorId = GoNatureClient.currentVisitor.getVisitorId();

		// =========================
		// 1. CREATE ORDER IMMEDIATELY
		// =========================
		ArrayList<String> newOrder = new ArrayList<>();
		newOrder.add(visitorId);
		newOrder.add(selectedPark);
		newOrder.add(selectedDate.toString());
		newOrder.add(selectedTime);
		newOrder.add(visitorsAmount);
		newOrder.add(GoNatureClient.currentVisitor.getEmail());
		newOrder.add("Individual");

		lastOrderData = newOrder;

		try {
			ClientUI.send(new Message("SUBMIT_NEW_ORDER", newOrder));
			System.out.println("Order sent immediately.");
		} catch (Exception e) {
			showError("Error sending order.");
			e.printStackTrace();
			return;
		}

		// =========================
		// 2. REQUEST PRICE ONLY AFTER ORDER EXISTS
		// =========================
		ArrayList<String> paymentData = new ArrayList<>();
		paymentData.add(visitType);
		paymentData.add(visitorsAmount);
		paymentData.add(String.valueOf(prepaid));
		paymentData.add(String.valueOf(subscriber));

		try {
			ClientUI.send(new Message("CALCULATE_PRICE_PREORDER", paymentData));
		} catch (Exception e) {
			showError("Error sending price request.");
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
}