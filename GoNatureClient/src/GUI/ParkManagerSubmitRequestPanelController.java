package GUI;

import Client.ClientUI;
import Client.GoNatureClient;
import Common.Message;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.time.LocalDate;
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
    private Label startDateLabel;

    @FXML
    private DatePicker startDatePicker;

    @FXML
    private Label endDateLabel;

    @FXML
    private DatePicker endDatePicker;

    @FXML
    private Label statusLabel;

    @FXML
    public void initialize() {
        instance = this;
        requestTypeComboBox.getItems().addAll("MaxCapacity", "CasualGap", "AvgStayDuration", "Promotion");

        requestTypeComboBox.setOnAction(e -> {
            loadCurrentValue();
            handlePromotionFieldsVisibility();
        });
    }


    private void handlePromotionFieldsVisibility() {
        String selectedType = requestTypeComboBox.getValue();
        boolean isPromotion = "Promotion".equals(selectedType);

        startDateLabel.setVisible(isPromotion);
        startDateLabel.setManaged(isPromotion);
        startDatePicker.setVisible(isPromotion);
        startDatePicker.setManaged(isPromotion);

        endDateLabel.setVisible(isPromotion);
        endDateLabel.setManaged(isPromotion);
        endDatePicker.setVisible(isPromotion);
        endDatePicker.setManaged(isPromotion);
        
        if (!isPromotion) {
            startDatePicker.setValue(null);
            endDatePicker.setValue(null);
        }
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

            if ("Promotion".equals(requestType)) {
                LocalDate startDate = startDatePicker.getValue();
                LocalDate endDate = endDatePicker.getValue();

                if (startDate == null || endDate == null) {
                    statusLabel.setText("Please select both start and end dates for Promotion.");
                    return;
                }
                
                if (endDate.isBefore(startDate)) {
                    statusLabel.setText("End date cannot be before start date.");
                    return;
                }

                data.add(startDate.toString());
                data.add(endDate.toString());
            }

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