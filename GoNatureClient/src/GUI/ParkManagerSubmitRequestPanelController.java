package GUI;

import Client.ClientUI;
import Client.GoNatureClient;
import Common.Message;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.time.LocalDate;
import java.util.ArrayList;

/**
 * Controller for the park manager's parameter change request panel. Handles the
 * submission of requests to change park parameters such as maximum capacity,
 * casual gaps, average stay duration, or new promotions.
 */
public class ParkManagerSubmitRequestPanelController {

	/**
	 * Static instance of this controller for external access.
	 */
	public static ParkManagerSubmitRequestPanelController instance;

	@FXML
	private ComboBox<String> requestTypeComboBox;

	@FXML
	private TextField oldValueField;

	@FXML
	private TextField newValueField;

	@FXML
	private Label startDateLabel;

	@FXML
	private DatePicker startDatePicker;

	@FXML
	private Label endDateLabel;

	@FXML
	private DatePicker endDatePicker;

	@FXML
	private Label statusLabel;

	/**
	 * Initializes the controller, populates the request type ComboBox, and sets up
	 * event listeners for dynamic UI updates.
	 */
	@FXML
	public void initialize() {
		instance = this;
		requestTypeComboBox.getItems().addAll("MaxCapacity", "CasualGap", "AvgStayDuration", "Promotion");

		requestTypeComboBox.setOnAction(e -> {
			loadCurrentValue();
			handlePromotionFieldsVisibility();
		});
	}

	/**
	 * Toggles the visibility of promotion-specific date fields based on the
	 * selected request type.
	 */
	private void handlePromotionFieldsVisibility() {
		String selectedType = requestTypeComboBox.getValue();
		boolean isPromotion = "Promotion".equals(selectedType);

		startDateLabel.setVisible(isPromotion);
		startDateLabel.setManaged(isPromotion);
		startDatePicker.setVisible(isPromotion);
		startDatePicker.setManaged(isPromotion);

		endDateLabel.setVisible(isPromotion);
		endDateLabel.setManaged(isPromotion);
		endDatePicker.setVisible(isPromotion);
		endDatePicker.setManaged(isPromotion);

		if (!isPromotion) {
			startDatePicker.setValue(null);
			endDatePicker.setValue(null);
		}
	}

	/**
	 * Fetches the current value for the selected parameter type from the server.
	 */
	private void loadCurrentValue() {
		try {
			String requestType = requestTypeComboBox.getValue();

			if (requestType == null || GoNatureClient.currentEmployee == null) {
				return;
			}

			ArrayList<String> data = new ArrayList<>();
			data.add(GoNatureClient.currentEmployee.getAffiliation());
			data.add(requestType);

			Message msg = new Message("GET_PARK_CURRENT_VALUE", data);
			ClientUI.send(msg);

		} catch (Exception e) {
			statusLabel.setText("Failed to load current value.");
			e.printStackTrace();
		}
	}

	/**
	 * Validates the input data and submits the parameter change request to the
	 * server.
	 *
	 * @param event The action event triggered by the submit button.
	 */
	@FXML
	void submitRequest(ActionEvent event) {
		try {
			String requestType = requestTypeComboBox.getValue();
			String oldValue = oldValueField.getText().trim();
			String newValue = newValueField.getText().trim();

			if (requestType.equals("AvgStayDuration") || requestType.equals("Promotion")) {
				try {
					if (Double.parseDouble(newValue) <= 0) {
						statusLabel.setText("can't be less than 0 or equal to 0.");
						return;
					}
				} catch (Exception e) {
					statusLabel.setText("Must be a number.");
					return;
				}
			} else {
				try {
					if (Integer.parseInt(newValue) <= 0) {
						statusLabel.setText("can't be less than 0 or equal to 0.");
						return;
					}
				} catch (Exception e) {
					statusLabel.setText("Must be an integer.");
					return;
				}
			}

			if (requestType == null || oldValue.isEmpty() || newValue.isEmpty()) {
				statusLabel.setText("Please fill in all fields.");
				return;
			}

			if (GoNatureClient.currentEmployee == null) {
				statusLabel.setText("No logged-in employee found.");
				return;
			}

			ArrayList<String> data = new ArrayList<>();
			data.add(GoNatureClient.currentEmployee.getAffiliation());
			data.add(requestType);
			data.add(oldValue);
			data.add(newValue);

			if ("Promotion".equals(requestType)) {
				LocalDate startDate = startDatePicker.getValue();
				LocalDate endDate = endDatePicker.getValue();

				if (startDate == null || endDate == null) {
					statusLabel.setText("Please select both start and end dates for Promotion.");
					return;
				}

				if (endDate.isBefore(startDate)) {
					statusLabel.setText("End date cannot be before start date.");
					return;
				}

				data.add(startDate.toString());
				data.add(endDate.toString());
			}

			Message msg = new Message("SUBMIT_PARK_REQUEST", data);
			ClientUI.send(msg);

		} catch (Exception e) {
			statusLabel.setText("Failed to send request.");
			e.printStackTrace();
		}
	}

	/**
	 * Displays status messages on the GUI.
	 *
	 * @param text The status message to display.
	 */
	public void showStatus(String text) {
		statusLabel.setText(text);
	}

	/**
	 * Sets the current value field from server-side retrieved data.
	 *
	 * @param value The value to display.
	 */
	public void setCurrentValue(String value) {
		oldValueField.setText(value);
	}
}