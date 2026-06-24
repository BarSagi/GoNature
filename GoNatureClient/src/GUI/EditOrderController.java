package GUI;

import Client.ClientUI;
import Client.GoNatureClient;
import Common.Message;
import Common.Order;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import java.sql.Date;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Controller for the order editing screen. Allows visitors to modify the date,
 * time, and number of visitors for an existing order and sends the updated
 * order details to the server.
 */
public class EditOrderController {

	/**
	 * Static instance of this controller for external access.
	 */
	public static EditOrderController instance;

	@FXML
	private Label orderIdLabel;
	@FXML
	private DatePicker datePicker;
	@FXML
	private ComboBox<String> timeComboBox;
	@FXML
	private TextField visitorsField;
	@FXML
	private Label statusLabel;

	/**
	 * The Order entity currently being edited.
	 */
	private Order currentOrder;

	/**
	 * Initializes the controller and populates the time selection dropdown.
	 */
	@FXML
	public void initialize() {
		instance = this;
		timeComboBox.getItems().addAll("09:00", "10:00", "11:00", "12:00", "13:00", "14:00", "15:00", "16:00");
	}

	/**
	 * Populates the UI fields with data from the order to be edited.
	 *
	 * @param order The order object containing current details.
	 */
	public void setOrderData(Order order) {
		this.currentOrder = order;

		orderIdLabel.setText("Editing Order ID: #" + order.getOrderId());

		if (order.getVisitDate() != null) {
			datePicker.setValue(order.getVisitDate().toLocalDate());
		}

		if (order.getVisitTime() != null) {
			String timeStr = order.getVisitTime().toString().substring(0, 5);
			timeComboBox.setValue(timeStr);
		}

		visitorsField.setText(String.valueOf(order.getVisitorCount()));
	}

	/**
	 * Validates the edited fields (including date and time constraints) and sends
	 * the updated order object to the server.
	 *
	 * @param event The action event triggered by the save button.
	 */
	@FXML
	void saveOrder(ActionEvent event) {
		statusLabel.setText("");
		statusLabel.setStyle("-fx-text-fill: red; -fx-font-size: 12px;");

		LocalDate selectedDate = datePicker.getValue();
		LocalTime selectedTime = LocalTime.parse(timeComboBox.getValue());

		if (datePicker.getValue() == null || timeComboBox.getValue() == null
				|| visitorsField.getText().trim().isEmpty()) {
			statusLabel.setText("Error: Please fill in all required fields.");
			return;
		}

		// Ensure date is not in the past
		if (selectedDate.isBefore(LocalDate.now())) {
			statusLabel.setText("Error: Visit date cannot be in the past.");
			return;
		}

		// Ensure that for today, the time slot is not in the past
		if (selectedDate.isEqual(LocalDate.now()) && selectedTime.isBefore(LocalTime.now())) {
			statusLabel.setText("Error: You cannot select a time that has already passed.");
			return;
		}

		int newVisitorCount;
		try {
			newVisitorCount = Integer.parseInt(visitorsField.getText().trim());
			if (newVisitorCount <= 0) {
				statusLabel.setText("Error: Number of visitors must be at least 1.");
				return;
			} else if (newVisitorCount > 100) {
				statusLabel.setText("Error: Number of visitors too large.");
				return;
			} else if ((newVisitorCount > 15 || newVisitorCount < 1)
					&& GoNatureClient.currentVisitor.getVisitorType() == "Guide") {
				statusLabel.setText("Error: Guide can only orders for 1 to 15 visitors.");
				return;
			}
		} catch (NumberFormatException e) {
			statusLabel.setText("Error: Visitors field must be a valid number.");
			return;
		}

		// Updating the Order object
		currentOrder.setVisitDate(Date.valueOf(datePicker.getValue()));

		String timeString = timeComboBox.getValue() + ":00";
		currentOrder.setVisitTime(Time.valueOf(timeString));

		currentOrder.setVisitorCount(newVisitorCount);

		// Sending the object to server
		Message msg = new Message("UPDATE_ORDER", currentOrder);

		try {
			ClientUI.send(msg);

			statusLabel.setStyle("-fx-text-fill: green; -fx-font-size: 12px;");
			statusLabel.setText("Sending update to server...");

		} catch (Exception e) {
			statusLabel.setText("Error: Could not connect to server.");
			e.printStackTrace();
		}
	}

	/**
	 * Updates the status label in the GUI with the provided message.
	 *
	 * @param msg The message to display.
	 */
	public void editStatusLabel(String msg) {
		statusLabel.setStyle("-fx-text-fill: red; -fx-font-size: 14px; -fx-font-weight: bold;");
		statusLabel.setText(msg);
	}

	/**
	 * Cancels the edit operation and returns the user to the visitor orders view.
	 *
	 * @param event The action event triggered by the cancel button.
	 */
	@FXML
	void cancelEdit(ActionEvent event) {
		Message msg = new Message("FETCH_VISITOR_ORDERS", GoNatureClient.currentVisitor.getVisitorId());

		try {
			ClientUI.send(msg);
		} catch (Exception e) {
			System.out.println("Error sending message to server");
			e.printStackTrace();
		}
	}
}