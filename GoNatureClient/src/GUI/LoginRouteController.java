package GUI;

import Client.ClientUI;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class LoginRouteController {

	@FXML
	private Button btnVisitor;

	@FXML
	private Button btnEmployee;

	@FXML
	public void openVisitorLogin(ActionEvent event) {
		// Switch to the Visitor screen
		ClientUI.changeScreen("/GUI/LoginVisitor.fxml", "GoNature - Enter ID");
	}

	@FXML
	public void openEmployeeLogin(ActionEvent event) {
		// Switch to the Employee screen
		ClientUI.changeScreen("/GUI/LoginEmployee.fxml", "GoNature - Employee Login");
	}
}