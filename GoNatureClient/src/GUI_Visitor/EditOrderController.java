package GUI_Visitor;

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

public class EditOrderController {

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

	private Order currentOrder;


	@FXML
	public void initialize() {
		instance = this;
		timeComboBox.getItems().addAll("08:00", "09:00", "10:00", "11:00", "12:00", "13:00", "14:00", "15:00", "16:00");
	}

	// --- UPDATE THIS METHOD to receive the original node ---
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

	@FXML
	void saveOrder(ActionEvent event) {
		statusLabel.setText("");
		statusLabel.setStyle("-fx-text-fill: red; -fx-font-size: 12px;");

		// --- 1. VALIDATION ---
		if (datePicker.getValue() == null || timeComboBox.getValue() == null
				|| visitorsField.getText().trim().isEmpty()) {
			statusLabel.setText("Error: Please fill in all required fields.");
			return;
		}

		if (datePicker.getValue().isBefore(java.time.LocalDate.now())) {
			statusLabel.setText("Error: Visit date cannot be in the past.");
			return;
		}

		int newVisitorCount;
		try {
			newVisitorCount = Integer.parseInt(visitorsField.getText().trim());
			if (newVisitorCount <= 0) {
				statusLabel.setText("Error: Number of visitors must be at least 1.");
				return;
			}
		} catch (NumberFormatException e) {
			statusLabel.setText("Error: Visitors field must be a valid number.");
			return;
		}

		// --- 2. UPDATING THE ORDER OBJECT ---
		// Convert LocalDate from the DatePicker to java.sql.Date
		currentOrder.setVisitDate(java.sql.Date.valueOf(datePicker.getValue()));

		// Convert String from ComboBox to java.sql.Time (Requires HH:mm:ss format)
		String timeString = timeComboBox.getValue() + ":00";
		currentOrder.setVisitTime(java.sql.Time.valueOf(timeString));

		currentOrder.setVisitorCount(newVisitorCount);

		// --- 3. SENDING THE OBJECT TO SERVER ---
		// We pass the actual Order object inside the Message!
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

	public void editStatusLabel(String msg) {
		statusLabel.setStyle("-fx-text-fill: red; -fx-font-size: 14px; -fx-font-weight: bold;");
		statusLabel.setText(msg);
	}

	@FXML
	void cancelEdit(ActionEvent event) {
		// Instantly swap the original table back into the center! No loading required!
		Message msg = new Message("FETCH_VISITOR_ORDERS", GoNatureClient.currentVisitor.getVisitorId());

		try {
			ClientUI.send(msg);
		} catch (Exception e) {
			System.out.println("Error sending message to server");
			e.printStackTrace();
		}
	}
}