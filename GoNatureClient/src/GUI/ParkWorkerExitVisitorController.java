package GUI;

import Client.ClientUI;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class ParkWorkerExitVisitorController {

    @FXML
    private TextField visitorIdField;

    @FXML
    private Label statusLabel;

    @FXML
    void goBack(ActionEvent event) {
        ClientUI.changeScreen("/GUI/ParkWorker.fxml", "Park Worker");
    }

    @FXML
    void confirmExit(ActionEvent event) {
        String visitorId = visitorIdField.getText().trim();
        if (visitorId.isEmpty()) {
            statusLabel.setText("Please enter visitor ID.");
            return;
        }

        System.out.println("Exit Visitor ID: " + visitorId);
        statusLabel.setText("Exit request sent for visitor ID: " + visitorId);
    }
}
