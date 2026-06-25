package GUI;

import Client.ClientUI;
import Common.Message;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import java.util.ArrayList;

/**
 * Controller for the employee login screen. Handles user authentication by
 * capturing credentials and sending a validation request to the server.
 */
public class LoginEmployeeController {

	/**
	 * Static instance of this controller for external access.
	 */
	public static LoginEmployeeController instance;

	@FXML
	private PasswordField passwordField;

	@FXML
	private TextField usernameField;

	@FXML
	private Label errorLabel;

	/**
	 * Initializes the controller and clears the error label.
	 */
	@FXML
	public void initialize() {
		instance = this;
		errorLabel.setText("");
	}

	/**
	 * Navigates back to the role selection screen. * @param event The action event
	 * triggered by the back button.
	 */
	@FXML
	void goBack(ActionEvent event) {
		ClientUI.changeScreen("/GUI/LoginRoute.fxml", "GoNature - Choose Role");
	}

	/**
	 * Authenticates the employee using the provided username and password. Packages
	 * the credentials into an ArrayList and sends them to the server. * @param
	 * event The action event triggered by the login button.
	 */
	@FXML
	public void loginEmployee(ActionEvent event) {
		String userName = usernameField.getText().trim();
		String password = passwordField.getText().trim();

		errorLabel.setText("");

		if (userName.isEmpty() || password.isEmpty()) {
			errorLabel.setText("Please enter both username and password.");
			return;
		}

		ArrayList<String> employeeData = new ArrayList<>();
		employeeData.add(userName);
		employeeData.add(password);

		Message msg = new Message("CHECK_EMPLOYEE_INFO", employeeData);

		try {
			if (ClientUI.client == null) {
				errorLabel.setText("Client is not connected");
				return;
			}

			ClientUI.send(msg);
			System.out.println("CHECK_EMPLOYEE_INFO sent");
		} catch (Exception e) {
			System.out.println("Error sending message to server");
			e.printStackTrace();
			errorLabel.setText("Failed to send request");
		}
	}

	/**
	 * Displays an error message on the GUI. * @param message The error message to
	 * be displayed.
	 */
	public void showError(String message) {
		errorLabel.setText(message);
	}
}