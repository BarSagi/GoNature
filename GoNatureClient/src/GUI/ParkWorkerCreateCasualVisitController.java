package GUI;

import Client.ClientUI;
import Client.GoNatureClient;
import Common.Message;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.util.ArrayList;

public class ParkWorkerCreateCasualVisitController {

    public static ParkWorkerCreateCasualVisitController instance;

    @FXML
    private TextField visitorIdField;

    @FXML
    private TextField visitorCountField;

    @FXML
    private Label parkLabel;

    @FXML
    private Label statusLabel;

    @FXML
    public void initialize() {
        instance = this;
        
        // Display the current park name based on the employee's logged-in affiliation
        if (GoNatureClient.currentEmployee != null) {
            parkLabel.setText("Park Location: " + GoNatureClient.currentEmployee.getAffiliation());
        } else {
            parkLabel.setText("Park Location: Unknown");
        }
    }

    @FXML
    void handleRegisterEntry(ActionEvent event) {
        String visitorId = visitorIdField.getText().trim();
        String countStr = visitorCountField.getText().trim();

        // Reset label color to neutral gray during processing
        statusLabel.setStyle("-fx-text-fill: #7f8c8d; -fx-font-weight: normal;");

        // Basic input validation
        if (visitorId.isEmpty() || countStr.isEmpty()) {
            statusLabel.setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold;");
            statusLabel.setText("Please fill in all fields.");
            return;
        }

        try {
            int visitorCount = Integer.parseInt(countStr);
            if (visitorCount <= 0) {
                statusLabel.setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold;");
                statusLabel.setText("Visitor count must be greater than 0.");
                return;
            }

            String parkName = GoNatureClient.currentEmployee.getAffiliation();

            // Pack data into an ArrayList to transfer safely across the network stream
            ArrayList<String> data = new ArrayList<>();
            data.add(parkName);
            data.add(visitorId);
            data.add(String.valueOf(visitorCount));

            // Send standard dynamic server message request
            Message msg = new Message("CREATE_CASUAL_VISIT", data);
            ClientUI.client.sendToServer(msg);
            statusLabel.setText("Processing casual entry...");

        } catch (NumberFormatException e) {
            statusLabel.setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold;");
            statusLabel.setText("Visitor count must be a valid number.");
        } catch (Exception e) {
            statusLabel.setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold;");
            statusLabel.setText("Connection error with the server.");
            e.printStackTrace();
        }
    }

    /**
     * Callback method called by the client architecture when server confirmation arrives
     */
    public void handleRegistrationResult(boolean success, String reason) {
        if (success) {
            statusLabel.setStyle("-fx-text-fill: #27ae60; -fx-font-weight: bold;");
            statusLabel.setText("Casual visit registered successfully!");
            visitorIdField.clear();
            visitorCountField.clear();
        } else {
            statusLabel.setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold;");
            statusLabel.setText(reason != null ? reason : "Registration failed. Park may be full.");
        }
    }
}