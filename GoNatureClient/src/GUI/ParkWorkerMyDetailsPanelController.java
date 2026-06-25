package GUI;

import Client.GoNatureClient;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

/**
 * Controller for the park worker's personal details panel. Populates and
 * displays the logged-in employee's profile information.
 */
public class ParkWorkerMyDetailsPanelController {

	@FXML
	private TextField firstNameField;

	@FXML
	private TextField lastNameField;

	@FXML
	private TextField employeeIdField;

	@FXML
	private TextField emailField;

	@FXML
	private TextField roleField;

	@FXML
	private TextField affiliationField;

	/**
	 * Initializes the view by populating the text fields with the details of the
	 * currently logged-in employee.
	 */
	@FXML
	public void initialize() {
		if (GoNatureClient.currentEmployee != null) {
			firstNameField.setText(GoNatureClient.currentEmployee.getFirstName());
			lastNameField.setText(GoNatureClient.currentEmployee.getLastName());
			employeeIdField.setText(GoNatureClient.currentEmployee.getEmployeeId());
			emailField.setText(GoNatureClient.currentEmployee.getEmail());
			roleField.setText(GoNatureClient.currentEmployee.getRole());
			affiliationField.setText(GoNatureClient.currentEmployee.getAffiliation());
		}
	}
	
	
}