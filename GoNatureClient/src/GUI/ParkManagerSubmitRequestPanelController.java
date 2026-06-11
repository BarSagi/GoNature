package GUI;

import Client.ClientUI;
import Client.GoNatureClient;
import Common.Message;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.util.ArrayList;

public class ParkManagerSubmitRequestPanelController {

    public static ParkManagerSubmitRequestPanelController instance;

    @FXML
    private ComboBox<String> requestTypeComboBox;

    @FXML
    private TextField oldValueField;

    @FXML
    private TextField newValueField;

    @FXML
    private Label statusLabel;

    @FXML
    public void initialize() {
        instance = this;
        requestTypeComboBox.getItems().addAll("MaxCapacity", "CasualGap", "AvgStayDuration", "Promotion");

        requestTypeComboBox.setOnAction(e -> loadCurrentValue());
    }

    private void loadCurrentValue() {
        try {
            String requestType = requestTypeComboBox.getValue();

            if (requestType == null || GoNatureClient.currentEmployee == null) {
                return;
            }

            ArrayList<String> data = new ArrayList<>();
            data.add(GoNatureClient.currentEmployee.getAffiliation()); // park name
            data.add(requestType);

            Message msg = new Message("GET_PARK_CURRENT_VALUE", data);
            ClientUI.send(msg);

        } catch (Exception e) {
            statusLabel.setText("Failed to load current value.");
            e.printStackTrace();
        }
    }

    @FXML
    void submitRequest(ActionEvent event) {
        try {
            String requestType = requestTypeComboBox.getValue();
            String oldValue = oldValueField.getText().trim();
            String newValue = newValueField.getText().trim();

            if (requestType == null || oldValue.isEmpty() || newValue.isEmpty()) {
                statusLabel.setText("Please fill in all fields.");
                return;
            }

            if (GoNatureClient.currentEmployee == null) {
                statusLabel.setText("No logged-in employee found.");
                return;
            }

            ArrayList<String> data = new ArrayList<>();
            data.add(GoNatureClient.currentEmployee.getAffiliation());
            data.add(requestType);
            data.add(oldValue);
            data.add(newValue);

            Message msg = new Message("SUBMIT_PARK_REQUEST", data);
            ClientUI.send(msg);

        } catch (Exception e) {
            statusLabel.setText("Failed to send request.");
            e.printStackTrace();
        }
    }

    public void showStatus(String text) {
        statusLabel.setText(text);
    }

    public void setCurrentValue(String value) {
        oldValueField.setText(value);
    }
}