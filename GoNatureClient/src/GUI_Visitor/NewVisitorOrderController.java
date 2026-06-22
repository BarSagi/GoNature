package GUI_Visitor;

import Client.ClientUI;
import Common.Message;
import Common.Order;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.application.Platform;
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

	@FXML
	public void initialize() {
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

		SpinnerValueFactory<Integer> valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 100, 1);
		visitorsSpinner.setValueFactory(valueFactory);

		errorLabel.setVisible(false);
	}

	public void loadParks(ArrayList<String> parks) {
		if (parks != null) {
			Platform.runLater(() -> {
				parkComboBox.getItems().setAll(parks);
			});
		}
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

		if (id.isEmpty() || firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() || phone.isEmpty()
				|| park == null || date == null || time == null || paymentMethod == null) {
			showError("Please fill in all fields.");
			return;
		}

		if (!id.matches("\\d+") || !phone.matches("\\d+")) {
			showError("ID and Phone must contain only numbers.");
			return;
		}

		if (!id.matches("\\d{9}")) {
			showError("ID must be exactly 9 digits.");
			return;
		}

		if (!phone.matches("\\d{10}")) {
			showError("Phone number must be exactly 10 digits.");
			return;
		}

		String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[a-zA-Z]{2,}$";
		if (!email.matches(emailRegex)) {
			showError("Please enter a valid email address (e.g., example@domain.com).");
			return;
		}

		int selectedIndex = parkComboBox.getSelectionModel().getSelectedIndex();
		int parkId = selectedIndex + 1;

		ArrayList<String> visitorInfo = new ArrayList<>();
		visitorInfo.add(id);
		visitorInfo.add(firstName);
		visitorInfo.add(lastName);
		visitorInfo.add(email);
		visitorInfo.add(phone);

		Order newOrder = new Order();
		newOrder.setVisitorId(id);
		newOrder.setParkId(parkId);
		newOrder.setVisitDate(java.sql.Date.valueOf(date));
		newOrder.setVisitTime(java.sql.Time.valueOf(time + ":00"));
		newOrder.setVisitorCount(visitorsNum);
		newOrder.setOrderType("Individual");
		newOrder.setOrderStatus("Approved");

		ArrayList<Object> dataToServer = new ArrayList<>();
		dataToServer.add(visitorInfo);
		dataToServer.add(newOrder);
		dataToServer.add(paymentMethod); // הוספת שיטת התשלום לרשימה כדי לפתור את חוסר הנתונים בשרת

		Message message = new Message("REGISTER_AND_ORDER", dataToServer);

		try {
			ClientUI.send(message);
			System.out.println("Hybrid data sent to server successfully!");
		} catch (Exception e) {
			showError("Lost connection to the server.");
			e.printStackTrace();
		}
	}

	@FXML
	void goBack(ActionEvent event) {
		try {
			ClientUI.changeScreen("/GUI/LoginVisitor.fxml", "GoNature - Visitor Login");
		} catch (Exception e) {
			System.out.println("Error loading the Visitor Login screen.");
			e.printStackTrace();
		}
	}

	private void showError(String message) {
		errorLabel.setText(message);
		errorLabel.setVisible(true);
	}
}