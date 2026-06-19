package GUI;

import java.time.LocalDate;
import java.util.ArrayList;

import Client.ClientUI;
import Common.Message;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

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
    private ComboBox<String> paymentComboBox;

    public static ParkWorkerCreateOrderController instance;
    public static String cachedVisitorType = "Individual";

    private boolean orderCreatedSuccessfully = false;

    private String lastVisitorCount;
    private String lastPaymentMethod;

    @FXML
    public void initialize() {

        instance = this;

        parkComboBox.getItems().addAll("Banias", "Ein Gedi", "Yehudia");

        timeComboBox.getItems().addAll(
                "09:00", "10:00", "11:00", "12:00",
                "13:00", "14:00", "15:00", "16:00"
        );

        paymentComboBox.getItems().addAll("Cash", "Credit Card");

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
            String paymentMethod = paymentComboBox.getValue();


            if (visitorId.isEmpty() || park == null || visitDate == null ||
                    time == null || paymentMethod == null ||
                    visitorCount.isEmpty() || email.isEmpty()) {

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

            lastVisitorCount = visitorCount;
            lastPaymentMethod = paymentMethod;

            orderCreatedSuccessfully = false;

            ArrayList<String> orderData = new ArrayList<>();
            orderData.add(visitorId);
            orderData.add(park);
            orderData.add(visitDate.toString());
            orderData.add(time);
            orderData.add(visitorCount);
            orderData.add(email);
            orderData.add(cachedVisitorType);
            orderData.add(paymentMethod);

            ClientUI.client.sendToServer(new Message("SUBMIT_NEW_ORDER", orderData));

            statusLabel.setText("Processing order...");

        } catch (Exception e) {
            statusLabel.setText("Failed to submit order.");
            e.printStackTrace();
        }
    }

    public void handleOrderResult(boolean success, String reason) {

        if (!success) {
            orderCreatedSuccessfully = false;

            Platform.runLater(() ->
                    statusLabel.setText("Order failed: " + reason)
            );
            return;
        }

        orderCreatedSuccessfully = true;

        Platform.runLater(() -> {
            statusLabel.setText("Order created.");
        });

        try {

            ArrayList<String> paymentData = new ArrayList<>();
            paymentData.add(cachedVisitorType); 
            paymentData.add(lastVisitorCount);

            boolean prepaid = "Credit Card".equals(lastPaymentMethod);
            boolean subscriber = false; 

            paymentData.add(String.valueOf(prepaid));
            paymentData.add(String.valueOf(subscriber));

            ClientUI.client.sendToServer(
                    new Message("CALCULATE_PRICE_PREORDER", paymentData)
            );

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void handlePriceResult(double price) {

        if (!orderCreatedSuccessfully) {
            return;
        }

        Platform.runLater(() -> {

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("SIMULATION");
            alert.setHeaderText("Order Price");
            alert.setContentText("Total price: " + price + " NIS");

            alert.showAndWait();
        });
    }

    public static void handleVisitorTypeResult(String type) {
        cachedVisitorType = (type != null) ? type : "Individual";
    }
    
    public void loadParks(ArrayList<String> parks) {

        if (parks == null || parks.isEmpty()) {
            statusLabel.setText("No parks available.");
            return;
        }

        parkComboBox.getItems().clear();
        parkComboBox.getItems().addAll(parks);
    }
}