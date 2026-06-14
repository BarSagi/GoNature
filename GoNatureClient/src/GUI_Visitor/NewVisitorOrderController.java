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

import java.time.LocalDate;
import java.util.ArrayList;

public class NewVisitorOrderController {

	// --- Visitor Details ---
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

	// --- Order Details ---
	@FXML
	private ComboBox<String> parkComboBox;
	@FXML
	private DatePicker datePicker;
	@FXML
	private ComboBox<String> timeComboBox;
	@FXML
	private TextField visitorsCountField;

	// --- UI Elements ---
	@FXML
	private Label errorLabel;

	/**
	 * Initializes the ComboBoxes with options when the screen loads.
	 */
	@FXML
	public void initialize() {
		// Initialize Park Options
		ObservableList<String> parks = FXCollections.observableArrayList("Karmel", "Banias", "Yarkon");
		parkComboBox.setItems(parks);

		// Initialize Time Options (e.g., 08:00 to 18:00)
		ObservableList<String> times = FXCollections.observableArrayList("08:00", "09:00", "10:00", "11:00", "12:00",
				"13:00", "14:00", "15:00", "16:00", "17:00", "18:00");
		timeComboBox.setItems(times);

		// Hide error label initially
		errorLabel.setVisible(false);
	}

	/**
	 * Triggered when the user clicks "Complete Registration & Order"
	 */
	/**
	 * Triggered when the user clicks "Complete Registration & Order"
	 */
	@FXML
	void submitRegistrationAndOrder(ActionEvent event) {
		errorLabel.setVisible(false); // Reset error label on each attempt

		// 1. Gather all inputs (Added .trim() to prevent accidental spacebar errors)
		String id = idField.getText().trim();
		String firstName = firstNameField.getText().trim();
		String lastName = lastNameField.getText().trim();
		String email = emailField.getText().trim();
		String phone = phoneField.getText().trim();
		String park = parkComboBox.getValue();
		LocalDate date = datePicker.getValue();
		String time = timeComboBox.getValue();
		String visitorsCount = visitorsCountField.getText().trim();

		// 2. Validate inputs (Make sure nothing is empty and types are correct)
		if (id.isEmpty() || firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() || phone.isEmpty()
				|| park == null || date == null || time == null || visitorsCount.isEmpty()) {
			showError("Please fill in all fields.");
			return;
		}

		if (!id.matches("\\d+") || !phone.matches("\\d+")) {
			showError("ID and Phone must contain only numbers.");
			return;
		}

		int visitorsNum;
		try {
			visitorsNum = Integer.parseInt(visitorsCount);
			if (visitorsNum <= 0) {
				showError("Number of visitors must be a valid positive number.");
				return;
			}
		} catch (NumberFormatException e) {
			showError("Number of visitors must be a valid number.");
			return;
		}

		if (date.isBefore(LocalDate.now())) {
			showError("Visit date cannot be in the past.");
			return;
		}

		int parkId = 0;
		switch (park) {
		case "Karmel":
			parkId = 1;
			break;
		case "Banias":
			parkId = 2;
			break;
		case "Yarkon":
			parkId = 3;
			break;
		}

		// --- 3. CREATE VISITOR INFO (ArrayList of Strings) ---
		ArrayList<String> visitorInfo = new ArrayList<>();
		visitorInfo.add(id);
		visitorInfo.add(firstName);
		visitorInfo.add(lastName);
		visitorInfo.add(email);
		visitorInfo.add(phone);

		// --- 4. CREATE ORDER OBJECT ---
		Order newOrder = new Order();
		newOrder.setVisitorId(id);
		newOrder.setParkId(parkId);
		newOrder.setVisitDate(java.sql.Date.valueOf(date));
		newOrder.setVisitTime(java.sql.Time.valueOf(time + ":00"));
		newOrder.setVisitorCount(visitorsNum);
		newOrder.setOrderType("Individual"); 
		newOrder.setOrderStatus("Approved");

		// --- 5. PACKAGE BOTH INTO ArrayList<Object> ---
		ArrayList<Object> dataToServer = new ArrayList<>();
		dataToServer.add(visitorInfo); // Index 0 is the ArrayList<String>
		dataToServer.add(newOrder); // Index 1 is the Order Object

		// --- 6. SEND MESSAGE TO SERVER ---
		Message message = new Message("REGISTER_AND_ORDER", dataToServer);

		try {
			ClientUI.send(message);
			System.out.println("Hybrid data sent to server successfully!");
		} catch (Exception e) {
			showError("Lost connection to the server.");
			e.printStackTrace();
		}
	}

	/**
	 * Returns the user to the previous screen
	 */
	@FXML
	void goBack(ActionEvent event) {
		try {
			ClientUI.changeScreen("/GUI/LoginVisitor.fxml", "GoNature - Visitor Login");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Helper method to show error messages cleanly
	 */
	private void showError(String message) {
		errorLabel.setText(message);
		errorLabel.setVisible(true);
	}
}