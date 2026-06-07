package GUI;

import java.util.ArrayList;

import Client.ClientUI;
import Common.Message;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class LoginEmployeeController {
	
	public static LoginEmployeeController instance;
	
    @FXML
    private PasswordField passwordField;

    @FXML
    private TextField usernameField;
    
    @FXML
    private Label errorLabel;
    
    @FXML
    public void initialize() {
        instance = this;
        // System.out.println("Controller initialized"); put this as a comment - dont think we need this, gal.
    }
    
    @FXML
	void goBack(ActionEvent event) {
		ClientUI.changeScreen("/GUI/LoginRoute.fxml", "GoNature - Choose Role");
	}

    @FXML
    public void loginEmployee(ActionEvent event) {
        String userName = usernameField.getText().trim();
        String password = passwordField.getText().trim();

        if (userName.isEmpty() || password.isEmpty()) {
            showError("Please fill in all fields.");
            return;
        }

        if (!userName.matches("^[a-zA-Z0-9]+$")) {
            showError("Username must contain only English letters and numbers.");
            return;
        }

        // Validate password: English letters, numbers, and allowed symbols
        if (!password.matches("^[a-zA-Z0-9!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?]+$")) {
            showError("Password can only contain English letters, numbers, and symbols.");
            return;
        }

        // If validation passes - proceed to send data to the server
        ArrayList<String> employeeData = new ArrayList<>();
        employeeData.add(userName);
        employeeData.add(password);
        
        Message msg = new Message("CHECK_EMPLOYEE_INFO", employeeData);
        
        try {
            ClientUI.client.sendToServer(msg);
            showError(""); // Clear any previous error messages on success
        } catch (Exception e) {
            System.out.println("Error sending message to server");
            e.printStackTrace();
            showError("Server connection error.");
        }
    }
    
    public void showError(String message) {
        errorLabel.setText(message);
    }
   
}
