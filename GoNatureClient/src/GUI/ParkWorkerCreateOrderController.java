package GUI;

import Client.ClientUI;
import Common.Message;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.util.ArrayList;

public class ParkWorkerCreateOrderController {

    @FXML
    private ComboBox<String> parkComboBox;

    @FXML
    private DatePicker visitDatePicker;

    @FXML
    private ComboBox<String> timeComboBox;

    @FXML
    private TextField visitorCountField;

    @FXML
    private TextField emailField;

    @FXML
    private TextField visitorIdField;

    @FXML
    private Label statusLabel;

    @FXML
    public void initialize() {
        parkComboBox.getItems().addAll("Banias", "Ein Gedi", "Yehudia");
        timeComboBox.getItems().addAll("08:00", "10:00", "12:00", "14:00", "16:00");
    }

    @FXML
    void submitOrder(ActionEvent event) {
        try {
            ArrayList<String> orderData = new ArrayList<>();
            orderData.add(visitorIdField.getText().trim());
            orderData.add(parkComboBox.getValue());
            orderData.add(visitDatePicker.getValue().toString());
            orderData.add(timeComboBox.getValue());
            orderData.add(visitorCountField.getText().trim());
            orderData.add(emailField.getText().trim());
            orderData.add("Individual");

            Message msg = new Message("SUBMIT_NEW_ORDER", orderData);
            ClientUI.client.sendToServer(msg);

            statusLabel.setText("Order request sent.");
        } catch (Exception e) {
            statusLabel.setText("Failed to submit order.");
            e.printStackTrace();
        }
    }
}
