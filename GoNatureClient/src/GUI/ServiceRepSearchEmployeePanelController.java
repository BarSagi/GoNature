package GUI;

import Client.ClientUI;
import Common.Message;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import java.util.ArrayList;

/**
 * Controller for the service representative's employee search panel. Enables
 * searching for park employees by ID and displays their detailed profile
 * information upon a successful search.
 */
public class ServiceRepSearchEmployeePanelController {

	/**
	 * Static instance of this controller for external access.
	 */
	public static ServiceRepSearchEmployeePanelController instance;

	@FXML
	private TextField employeeIdField;

	@FXML
	private TextField firstNameField;

	@FXML
	private TextField lastNameField;

	@FXML
	private TextField employeeIdResultField;

	@FXML
	private TextField emailField;

	@FXML
	private TextField roleField;

	@FXML
	private TextField affiliationField;

	@FXML
	private Label statusLabel;

	/**
	 * Initializes the controller and sets the status label to empty.
	 */
	@FXML
	public void initialize() {
		instance = this;
	}

	/**
	 * Validates the employee ID input and sends a request to the server to retrieve
	 * the employee's details.
	 *
	 * @param event The action event triggered by the search button.
	 */
	@FXML
	void searchEmployee(ActionEvent event) {
		String employeeId = employeeIdField.getText().trim();

		if (employeeId.isEmpty()) {
			statusLabel.setText("Please enter an employee ID.");
			return;
		}

		if (!employeeId.matches("\\d+")) {
			statusLabel.setText("Employee ID must contain numbers only.");
			return;
		}

		try {
			// NOTE: Rule #3 - Should use ClientUI.send()
			ClientUI.send(new Message("GET_EMPLOYEE_DETAILS", employeeId));
		} catch (Exception e) {
			statusLabel.setText("Failed to send request.");
			e.printStackTrace();
		}
	}

	/**
	 * Populates the UI fields with the employee details retrieved from the server.
	 *
	 * @param employeeInfo An ArrayList containing the employee's profile data.
	 */
	public void loadEmployeeDetails(ArrayList<String> employeeInfo) {
		if (employeeInfo == null || employeeInfo.isEmpty()) {
			statusLabel.setText("No employee found.");
			return;
		}

		firstNameField.setText(employeeInfo.get(1));
		lastNameField.setText(employeeInfo.get(2));
		employeeIdResultField.setText(employeeInfo.get(0));
		emailField.setText(employeeInfo.get(3));
		roleField.setText(employeeInfo.get(4));
		affiliationField.setText(employeeInfo.get(5));

		statusLabel.setStyle("-fx-text-fill: #27ae60;");
		statusLabel.setText("Employee found.");
	}
}