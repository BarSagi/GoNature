package GUI;

import java.time.LocalDate;
import java.util.ArrayList;

import Client.ClientUI;
import Common.Message;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

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
        timeComboBox.getItems().addAll(
                "09:00", "10:00", "11:00", "12:00",
                "13:00", "14:00", "15:00", "16:00");
        statusLabel.setText("");
    }

    @FXML
    void submitOrder(ActionEvent event) {
        try {
            String visitorId = visitorIdField.getText().trim();
            String park = parkComboBox.getValue();
            LocalDate visitDate = visitDatePicker.getValue();
            String time = timeComboBox.getValue();
            String visitorCount = visitorCountField.getText().trim();
            String email = emailField.getText().trim();

            if (visitorId.isEmpty() || park == null || visitDate == null || time == null
                    || visitorCount.isEmpty() || email.isEmpty()) {
                statusLabel.setText("Please fill in all fields.");
                return;
            }

            if (!visitorId.matches("\\d{9}")) {
                statusLabel.setText("Visitor ID must be exactly 9 digits.");
                return;
            }

            if (visitDate.isBefore(LocalDate.now())) {
                statusLabel.setText("You cannot select a past date.");
                return;
            }

            if (!visitorCount.matches("\\d+") || Integer.parseInt(visitorCount) <= 0) {
                statusLabel.setText("Visitor count must be a positive number.");
                return;
            }

            if (!email.matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")) {
                statusLabel.setText("Please enter a valid email address.");
                return;
            }

            ArrayList<String> orderData = new ArrayList<>();
            orderData.add(visitorId);
            orderData.add(park);
            orderData.add(visitDate.toString());
            orderData.add(time);
            orderData.add(visitorCount);
            orderData.add(email);
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
