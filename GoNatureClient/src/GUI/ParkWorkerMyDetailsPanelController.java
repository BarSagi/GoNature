package GUI;

import Client.GoNatureClient;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

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