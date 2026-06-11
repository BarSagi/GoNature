package GUI;

import Client.ClientUI;
import Common.Message;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.util.ArrayList;

public class ParkWorkerEnterVisitorController {

    public static ParkWorkerEnterVisitorController instance;

    @FXML
    private TextField visitorIdField;

    @FXML
    private Label statusLabel;

    @FXML
    public void initialize() {
        instance = this;
    }

    @FXML
    void confirmEntry(ActionEvent event) {
        String visitorId = visitorIdField.getText().trim();

        if (visitorId.isEmpty()) {
            statusLabel.setText("Please enter visitor ID.");
            return;
        }

        try {
            ArrayList<String> data = new ArrayList<>();
            data.add(visitorId);

            Message msg = new Message("ENTER_VISITOR", data);
            ClientUI.client.sendToServer(msg);

            statusLabel.setText("Entry request sent.");
        } catch (Exception e) {
            statusLabel.setText("Failed to send entry request.");
            e.printStackTrace();
        }
    }

    public void showStatus(String text) {
        statusLabel.setText(text);
    }
}