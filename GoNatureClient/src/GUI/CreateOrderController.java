package GUI;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.ResourceBundle;

import Client.ClientUI;
import Common.Message;

public class CreateOrderController implements Initializable {

	@FXML
	private ComboBox<String> parkComboBox;

	@FXML
	private DatePicker datePicker;

	@FXML
	private ComboBox<String> timeComboBox;

	@FXML
	private TextField visitorsTextField;

	@FXML
	private TextField emailTextField;

	@FXML
	private Label errorLabel;

	@FXML
	private Button backButton;

	@FXML
	private Button createOrderButton;

	// This method runs automatically when the FXML is loaded
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		/*
		 * MAKE SURE WE CHANGE THESE LATER, ALSO NO IDEA WHAT OBSERVABLE-ARRAY-LIST
		 * MEANS OR FXCOLLECTIONS SO MAYBE ALSO CHANGE THAT.
		 */
		// 1. Populate the Parks ComboBox
		ObservableList<String> parks = FXCollections.observableArrayList("Karmel", "Banias", "Hula");
		parkComboBox.setItems(parks);

		// 2. Populate the Times ComboBox (e.g., every hour from 08:00 to 16:00)
		ObservableList<String> times = FXCollections.observableArrayList("08:00", "09:00", "10:00", "11:00", "12:00",
				"13:00", "14:00", "15:00", "16:00");
		timeComboBox.setItems(times);
	}

	@FXML
	void submitOrder(ActionEvent event) {
		// Reset error label
		errorLabel.setVisible(false);

		// 1. Gather all data from the UI
		String selectedPark = parkComboBox.getValue();
		LocalDate selectedDate = datePicker.getValue();
		String selectedTime = timeComboBox.getValue();
		String visitorsAmount = visitorsTextField.getText();
		String email = emailTextField.getText();

		// 2. Basic Validation: Check if anything is empty
		if (selectedPark == null || selectedDate == null || selectedTime == null || visitorsAmount.trim().isEmpty()
				|| email.trim().isEmpty()) {
			showError("Please fill in all fields.");
			return;
		}

		// Validate that visitors amount is a valid number
		if (!visitorsAmount.matches("\\d+") || Integer.parseInt(visitorsAmount) <= 0) {
			showError("Number of visitors must be a valid positive number.");
			return;
		}

		// 3. Gather background session data
		// Note: Replace "ClientUI.loggedInVisitorId" with exactly where you saved the
		// ID
		// during the LoginVisitorController stage!
		String visitorId = ClientUI.visitorID;

		// 4. Package data into an ArrayList of Strings (Raw Data format for the Server)
		ArrayList<String> newOrder = new ArrayList<>();
		newOrder.add(visitorId); // Index 0
		newOrder.add(selectedPark); // Index 1 (Server will need to map this to parkId INT)
		newOrder.add(selectedDate.toString()); // Index 2 (Format: YYYY-MM-DD)
		newOrder.add(selectedTime); // Index 3
		newOrder.add(visitorsAmount); // Index 4
		newOrder.add(email); // Index 5

		// If your UI doesn't have a selector for "Order Type" yet, you can default it:
		newOrder.add("Individual"); // Index 6

		// 5. Wrap in a Message object and send via ClientUI
		Message msg = new Message("SUBMIT_NEW_ORDER", newOrder);

		try {
		    ClientUI.send(msg);

		    System.out.println("Valid data! Sent SUBMIT_NEW_ORDER to server for "
		            + visitorsAmount + " visitors at " + selectedPark);

		} catch (Exception e) {
		    showError("Connection error: Could not send order to server.");
		    e.printStackTrace();
		}
	}

	@FXML
	void goBack(ActionEvent event) {
		// Use your ClientUI.changeScreen method to go back to the previous screen
		ClientUI.changeScreen("/GUI/LoginVisitor.fxml", "GoNature - Visitor Login");
	}

	private void showError(String message) {
		errorLabel.setText(message);
		errorLabel.setVisible(true);
	}
}