package GUI;

import Client.ClientUI;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

/**
 * Controller for the role selection screen. Provides navigation options for
 * users to choose between visitor or employee login.
 */
public class LoginRouteController {

	/**
	 * Button to navigate to the visitor login screen.
	 */
	@FXML
	private Button btnVisitor;

	/**
	 * Button to navigate to the employee login screen.
	 */
	@FXML
	private Button btnEmployee;

	/**
	 * Switches the active screen to the visitor login view.
	 *
	 * @param event The action event triggered by the button click.
	 */
	@FXML
	public void openVisitorLogin(ActionEvent event) {
		// Switch to the Visitor screen
		ClientUI.changeScreen("/GUI/LoginVisitor.fxml", "GoNature - Enter ID");
	}

	/**
	 * Switches the active screen to the employee login view.
	 *
	 * @param event The action event triggered by the button click.
	 */
	@FXML
	public void openEmployeeLogin(ActionEvent event) {
		// Switch to the Employee screen
		ClientUI.changeScreen("/GUI/LoginEmployee.fxml", "GoNature - Employee Login");
	}
}