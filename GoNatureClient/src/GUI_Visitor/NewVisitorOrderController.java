package GUI_Visitor;

import Client.ClientUI;
import Common.Message;
import Common.Order;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.application.Platform;

import java.sql.Date;
import java.sql.Time;
import java.time.LocalDate;
import java.util.ArrayList;

public class NewVisitorOrderController {

	public static NewVisitorOrderController instance;

	@FXML
	private TextField idField;
	@FXML
	private TextField firstNameField;
	@FXML
	private TextField lastNameField;
	@FXML
	private TextField emailField;
	@FXML
	private TextField phoneField;

	@FXML
	private ComboBox<String> parkComboBox;
	@FXML
	private DatePicker datePicker;
	@FXML
	private ComboBox<String> timeComboBox;
	@FXML
	private Spinner<Integer> visitorsSpinner;
	@FXML
	private ComboBox<String> paymentComboBox;

	@FXML
	private Label errorLabel;

	private String pendingVisitorId;
	private String pendingParkName;
	private LocalDate pendingDate;
	private int pendingVisitorCount;
	private String pendingPaymentMethod;

	@FXML
	public void initialize() {
		instance = this;

		try {
			ClientUI.send(new Message("GET_ALL_PARKS", null));
		} catch (Exception e) {
			e.printStackTrace();
		}

		timeComboBox.setItems(FXCollections.observableArrayList("09:00", "10:00", "11:00", "12:00", "13:00", "14:00",
				"15:00", "16:00"));

		paymentComboBox.setItems(FXCollections.observableArrayList("Pay Later", "Pay Now"));

		visitorsSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 100, 1));

		errorLabel.setVisible(false);
	}

	@FXML
	void submitRegistrationAndOrder(ActionEvent event) {
		errorLabel.setVisible(false);

		String id = idField.getText().trim();
		String firstName = firstNameField.getText().trim();
		String lastName = lastNameField.getText().trim();
		String email = emailField.getText().trim();
		String phone = phoneField.getText().trim();

		String park = parkComboBox.getValue();
		LocalDate date = datePicker.getValue();
		String time = timeComboBox.getValue();
		int visitorsNum = visitorsSpinner.getValue();
		String paymentMethod = paymentComboBox.getValue();

		if (id.isEmpty() || firstName.isEmpty() || lastName.isEmpty() ||
				email.isEmpty() || phone.isEmpty() ||
				park == null || date == null || time == null || paymentMethod == null) {
			showError("Please fill in all fields.");
			return;
		}

		if (!id.matches("\\d{9}")) {
			showError("ID must be exactly 9 digits.");
			return;
		}

		if (!phone.matches("\\d{10}")) {
			showError("Phone must be exactly 10 digits.");
			return;
		}

		if (date.isBefore(LocalDate.now())) {
			showError("Cannot select past date.");
			return;
		}

		if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[a-zA-Z]{2,}$")) {
			showError("Invalid email format.");
			return;
		}

		this.pendingVisitorId = id;
		this.pendingParkName = park;
		this.pendingDate = date;
		this.pendingVisitorCount = visitorsNum;
		this.pendingPaymentMethod = paymentMethod;

		int parkId = parkComboBox.getSelectionModel().getSelectedIndex() + 1;

		ArrayList<String> visitorInfo = new ArrayList<>();
		visitorInfo.add(id);
		visitorInfo.add(firstName);
		visitorInfo.add(lastName);
		visitorInfo.add(email);
		visitorInfo.add(phone);

		Order order = new Order();
		order.setVisitorId(id);
		order.setParkId(parkId);
		order.setVisitDate(Date.valueOf(date));
		order.setVisitTime(Time.valueOf(time + ":00"));
		order.setVisitorCount(visitorsNum);
		order.setOrderType("Individual");
		order.setOrderStatus("Approved");

		ArrayList<Object> dataToServer = new ArrayList<>();
		dataToServer.add(visitorInfo);
		dataToServer.add(order);
		dataToServer.add(paymentMethod);

		try {
			ClientUI.send(new Message("REGISTER_AND_ORDER", dataToServer));
		} catch (Exception e) {
			showError("Server connection error.");
			e.printStackTrace();
		}
	}

	public void handleOrderResult(boolean success, String reason) {
		if (!success) {
			Platform.runLater(() -> showError(reason != null ? reason : "Order failed"));
			return;
		}

		Platform.runLater(() -> errorLabel.setText("Order created successfully! calculating price..."));

		new Thread(this::calculatePriceAsync).start();
	}

	private void calculatePriceAsync() {
		ArrayList<String> priceData = new ArrayList<>();
		priceData.add(pendingVisitorId);
		priceData.add(String.valueOf(pendingVisitorCount));
		priceData.add(pendingPaymentMethod);
		priceData.add(pendingParkName);
		priceData.add(pendingDate.toString());

		try {
			ClientUI.send(new Message("CALCULATE_PRICE_PREORDER", priceData));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void handlePriceResult(double price) {
		Platform.runLater(() -> {
			Alert alert = new Alert(Alert.AlertType.INFORMATION);
			alert.setTitle("Order & Price Details");
			alert.setHeaderText("Registration & Order Successful!");
			alert.setContentText("Total price to pay: " + price + " NIS\nWelcome to GoNature!");
			alert.showAndWait();

			try {
				ClientUI.changeScreen("/GUI/LoginVisitor.fxml", "GoNature - Visitor Login");
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
	}

	public void loadParks(ArrayList<String> parks) {
		Platform.runLater(() -> parkComboBox.getItems().setAll(parks));
	}

	@FXML
	void goBack(ActionEvent event) {
		try {
			ClientUI.changeScreen("/GUI/LoginVisitor.fxml", "GoNature - Visitor Login");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void showError(String msg) {
		errorLabel.setText(msg);
		errorLabel.setVisible(true);
	}
}