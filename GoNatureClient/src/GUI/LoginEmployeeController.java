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
        errorLabel.setText("");
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

            ClientUI.client.sendToServer(msg);
            System.out.println("CHECK_EMPLOYEE_INFO sent");
        } catch (Exception e) {
            System.out.println("Error sending message to server");
            e.printStackTrace();
            errorLabel.setText("Failed to send request");
        }
    }
    
    public void showError(String message) {
        errorLabel.setText(message);
    }
   
}
