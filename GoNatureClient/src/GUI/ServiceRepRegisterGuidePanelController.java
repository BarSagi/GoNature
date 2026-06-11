package GUI;

import java.util.ArrayList;

import Client.ClientUI;
import Common.Message;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class ServiceRepRegisterGuidePanelController {

	public static ServiceRepRegisterGuidePanelController instance;
	
    @FXML
    private TextField firstNameField;

    @FXML
    private TextField lastNameField;

    @FXML
    private TextField idField;

    @FXML
    private TextField phoneField;

    @FXML
    private TextField emailField;

    @FXML
    private Label statusLabel;
    
    @FXML
    public void initialize() {
        instance = this;
    }

    @FXML
    void registerGuide(ActionEvent event) {
        try {
            String firstName = firstNameField.getText().trim();
            String lastName = lastNameField.getText().trim();
            String id = idField.getText().trim();
            String phone = phoneField.getText().trim();
            String email = emailField.getText().trim();

            if (firstName.isEmpty() || lastName.isEmpty() || id.isEmpty() ||
                phone.isEmpty() || email.isEmpty()) {
                statusLabel.setText("Please fill in all fields.");
                return;
            }

            ArrayList<String> data = new ArrayList<>();
            data.add(id);
            data.add(firstName);
            data.add(lastName);
            data.add(phone);
            data.add(email);

            Message msg = new Message("REGISTER_GROUP_GUIDE", data);
            ClientUI.send(msg);

        } catch (Exception e) {
            statusLabel.setText("Failed to send guide registration.");
            e.printStackTrace();
        }
    }
    
    public void showStatus(String text) {
        statusLabel.setText(text);
    }
}