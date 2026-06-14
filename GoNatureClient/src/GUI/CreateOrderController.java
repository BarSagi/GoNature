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
import Client.GoNatureClient;
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

	// Notice: emailTextField is GONE! Better UX!

	@FXML
	private Label errorLabel;
	@FXML
	private Button backButton;
	@FXML
	private Button createOrderButton;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// Populate the Parks ComboBox
		ObservableList<String> parks = FXCollections.observableArrayList("Karmel", "Banias", "Hula");
		parkComboBox.setItems(parks);

		// Populate the Times ComboBox
		ObservableList<String> times = FXCollections.observableArrayList("08:00", "09:00", "10:00", "11:00", "12:00",
				"13:00", "14:00", "15:00", "16:00");
		timeComboBox.setItems(times);
	}

	@FXML
	void submitOrder(ActionEvent event) {
		errorLabel.setVisible(false);

		// 1. Gather all data from the UI (No email needed!)
		String selectedPark = parkComboBox.getValue();
		LocalDate selectedDate = datePicker.getValue();
		String selectedTime = timeComboBox.getValue();
		String visitorsAmount = visitorsTextField.getText();

		// 2. Basic Validation
		if (selectedPark == null || selectedDate == null || selectedTime == null || visitorsAmount.trim().isEmpty()) {
			showError("Please fill in all fields.");
			return;
		}

		if (!visitorsAmount.matches("\\d+") || Integer.parseInt(visitorsAmount) <= 0) {
			showError("Number of visitors must be a valid positive number.");
			return;
		}

		// 3. Gather background session data
		// We use the ID of the person who logged in.
		// The Server will use this ID to find their email in the Database!
		String visitorId = GoNatureClient.currentVisitor.getVisitorId();

		// 4. Package data into an ArrayList
		ArrayList<String> newOrder = new ArrayList<>();
		newOrder.add(visitorId); // Index 0
		newOrder.add(selectedPark); // Index 1
		newOrder.add(selectedDate.toString()); // Index 2
		newOrder.add(selectedTime); // Index 3
		newOrder.add(visitorsAmount); // Index 4
		newOrder.add(GoNatureClient.currentVisitor.getEmail());
		newOrder.add("Individual"); // Index 6 (Order Type)

		// 5. Send to Server
		Message msg = new Message("SUBMIT_NEW_ORDER", newOrder);

		try {
			ClientUI.send(msg);
			System.out.println("Valid data! Sent SUBMIT_NEW_ORDER to server.");
		} catch (Exception e) {
			showError("Connection error: Could not send order to server.");
			e.printStackTrace();
		}
	}

	@FXML
	void goBack(ActionEvent event) {
		// if current visitor isnt null it means we were at the visitor orders screen.
		if (GoNatureClient.currentVisitor != null) {
			Message msg = new Message("FETCH_VISITOR_ORDERS", GoNatureClient.currentVisitor.getVisitorId());

			try {
				ClientUI.send(msg);
			} catch (Exception e) {
				System.out.println("Error sending message to server");
				e.printStackTrace();
			}
		} else
			ClientUI.changeScreen("/GUI/LoginVisitor.fxml", "GoNature - Visitor Login");
	}

	private void showError(String message) {
		errorLabel.setText(message);
		errorLabel.setVisible(true);
	}
}