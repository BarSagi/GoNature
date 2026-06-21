package GUI;

import Client.ClientUI;
import Client.GoNatureClient;
import Common.Message;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.util.ArrayList;

public class ParkWorkerExitVisitorController {

    public static ParkWorkerExitVisitorController instance;

    @FXML
    private TextField identifierField; 

    @FXML
    private TextField exitAmountField; 

    @FXML
    private Label statusLabel;

    @FXML
    public void initialize() {
        instance = this;
        statusLabel.setText("");
    }

    @FXML
    void confirmExit(ActionEvent event) {
      
        String identifier = identifierField.getText().trim();
        String amountStr = exitAmountField.getText().trim();

        //  Check that neither field is empty
        if (identifier.isEmpty() || amountStr.isEmpty()) {
            statusLabel.setText("Please enter identifier and amount.");
            statusLabel.setStyle("-fx-text-fill: red;");
            return;
        }

       
        if (!identifier.matches("^[A-Z0-9]{1,9}$")) {
            statusLabel.setText("Invalid identifier format.");
            statusLabel.setStyle("-fx-text-fill: red;");
            return;
        }

        // Validate amount (Ensure it's a valid positive number starting from 1)
        if (!amountStr.matches("^[1-9][0-9]*$")) {
            statusLabel.setText("Amount must be a valid number greater than 0.");
            statusLabel.setStyle("-fx-text-fill: red;");
            return;
        }

        try {
            ArrayList<String> data = new ArrayList<>();
            data.add(identifier);
            
            data.add(GoNatureClient.currentEmployee.getAffiliation());
            
            data.add(amountStr);

            Message msg = new Message("EXIT_VISITOR", data);
            ClientUI.client.sendToServer(msg);

            statusLabel.setText("Exit request sent to server...");
            statusLabel.setStyle("-fx-text-fill: blue;");
        } catch (Exception e) {
            statusLabel.setText("Failed to send exit request.");
            statusLabel.setStyle("-fx-text-fill: red;");
            e.printStackTrace();
        }
    }

    public void showStatus(String text) {
        if (text.startsWith("Success")) {
            statusLabel.setStyle("-fx-text-fill: green;");
            identifierField.clear();
            exitAmountField.clear();
        } else {
            statusLabel.setStyle("-fx-text-fill: red;");
        }
        statusLabel.setText(text);
    }
}