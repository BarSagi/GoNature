package GUI;

import java.util.ArrayList;

import Client.ClientUI;
import Common.Message;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class ServiceRepRegisterSubscriberPanelController {
	
	public static ServiceRepRegisterSubscriberPanelController instance;
	
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
    private TextField familyMembersField;

    @FXML
    private ComboBox<String> paymentMethodComboBox;

    @FXML
    private TextField creditCardField;

    @FXML
    private Label statusLabel;

    @FXML
    public void initialize() {
    	instance = this;
        paymentMethodComboBox.getItems().addAll("Credit Card", "Cash");
        paymentMethodComboBox.setValue("Credit Card");

        paymentMethodComboBox.setOnAction(e -> {
            String paymentMethod = paymentMethodComboBox.getValue();
            boolean isCash = "Cash".equals(paymentMethod);
            creditCardField.setDisable(isCash);
            if (isCash) {
                creditCardField.clear();
            }
        });
    }

    @FXML
    void registerSubscriber(ActionEvent event) {
        try {
            String firstName = firstNameField.getText().trim();
            String lastName = lastNameField.getText().trim();
            String id = idField.getText().trim();
            String phone = phoneField.getText().trim();
            String email = emailField.getText().trim();
            String familyMembers = familyMembersField.getText().trim();
            String paymentMethod = paymentMethodComboBox.getValue();
            String creditCard = creditCardField.getText().trim();

            if (firstName.isEmpty() || lastName.isEmpty() || id.isEmpty() ||
                phone.isEmpty() || email.isEmpty() || familyMembers.isEmpty()) {
                statusLabel.setText("Please fill in all required fields.");
                return;
            }

            if ("Credit Card".equals(paymentMethod) && creditCard.isEmpty()) {
                statusLabel.setText("Please enter credit card number.");
                return;
            }

            ArrayList<String> data = new ArrayList<>();
            data.add(id);
            data.add(firstName);
            data.add(lastName);
            data.add(phone);
            data.add(email);
            data.add(familyMembers);
            data.add("Cash".equals(paymentMethod) ? null : creditCard);

            Message msg = new Message("REGISTER_FAMILY_SUBSCRIBER", data);
            ClientUI.send(msg);

        } catch (Exception e) {
            statusLabel.setText("Failed to send subscriber registration.");
            e.printStackTrace();
        }
    }
    
    public void showStatus(String text) {
        statusLabel.setText(text);
    }
}