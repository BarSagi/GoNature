package GUI_Visitor;

import Client.ClientUI;
import Common.Message;
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
		ObservableList<String> parks = FXCollections.observableArrayList("Karmel", "Banias", "Niagara");
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
	@FXML
	void submitRegistrationAndOrder(ActionEvent event) {
		errorLabel.setVisible(false); // Reset error label on each attempt

		// 1. Gather all inputs
		String id = idField.getText();
		String firstName = firstNameField.getText();
		String lastName = lastNameField.getText();
		String email = emailField.getText();
		String phone = phoneField.getText();

		String park = parkComboBox.getValue();
		LocalDate date = datePicker.getValue();
		String time = timeComboBox.getValue();
		String visitorsCount = visitorsCountField.getText();

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

		if (!visitorsCount.matches("\\d+") || Integer.parseInt(visitorsCount) <= 0) {
			showError("Number of visitors must be a valid positive number.");
			return;
		}

		if (date.isBefore(LocalDate.now())) {
			showError("Visit date cannot be in the past.");
			return;
		}

		// 3. Package data into an ArrayList
		ArrayList<String> dataToServer = new ArrayList<>();
		// Visitor Data
		dataToServer.add(id);
		dataToServer.add(firstName);
		dataToServer.add(lastName);
		dataToServer.add(email);
		dataToServer.add(phone);
		// Order Data
		dataToServer.add(park);
		dataToServer.add(date.toString());
		dataToServer.add(time);
		dataToServer.add(visitorsCount);

		// 4. Send Message to Server
		// We use the new command "REGISTER_AND_ORDER" which your Server Strategy will
		// catch
		Message message = new Message("REGISTER_AND_ORDER", dataToServer);

		try {
			ClientUI.send(message);
			// Note: In a full implementation, you'd wait for a server response here
			// using Platform.runLater() inside your handleMessageFromServer method
			// before showing a success popup or changing the screen.
			System.out.println("Data sent to server successfully!");
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