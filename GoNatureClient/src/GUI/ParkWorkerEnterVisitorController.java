package GUI;

import Client.ClientUI;
import Client.GoNatureClient;
import Common.Message;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.util.ArrayList;

public class ParkWorkerEnterVisitorController {

    public static ParkWorkerEnterVisitorController instance;

    @FXML
    private TextField identifierField; 

    @FXML
    private Label statusLabel;

    @FXML
    public void initialize() {
        instance = this;
        statusLabel.setText("");
    }

    @FXML
    void confirmEntry(ActionEvent event) {
        String identifier = identifierField.getText().trim();

        if (identifier.isEmpty()) {
            statusLabel.setText("Please enter Order ID, Visitor ID, or QR Code.");
            return;
        }

        if (!identifier.matches("^[A-Z0-9]{1,9}$")) {
            statusLabel.setText("Invalid input: Max 9 uppercase alphanumeric characters only.");
            statusLabel.setStyle("-fx-text-fill: red;");
            return;
        }

        try {
            ArrayList<String> data = new ArrayList<>();
            data.add(identifier);
            
            String currentParkId = GoNatureClient.currentEmployee.getAffiliation();
            data.add(currentParkId); 

            Message msg = new Message("ENTER_VISITOR", data);
            ClientUI.client.sendToServer(msg);

            statusLabel.setText("Entry request sent to server...");
            statusLabel.setStyle("-fx-text-fill: blue;"); 
        } catch (Exception e) {
            statusLabel.setText("Failed to send entry request.");
            statusLabel.setStyle("-fx-text-fill: red;");
            e.printStackTrace();
        }
    }

    public void showStatus(String text) {
        if (text.startsWith("Success")) {
            statusLabel.setStyle("-fx-text-fill: green;");
            identifierField.clear(); 
        } else {
            statusLabel.setStyle("-fx-text-fill: red;");
        }
        statusLabel.setText(text);
    }
}