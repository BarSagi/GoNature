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

    @FXML private ComboBox<String> parkComboBox;
    @FXML private DatePicker visitDatePicker;
    @FXML private ComboBox<String> timeComboBox;
    @FXML private TextField visitorCountField;
    @FXML private TextField visitorIdField;
    @FXML private Label statusLabel;
    @FXML private ComboBox<String> paymentComboBox;

    public static ParkWorkerCreateOrderController instance;

    private String pendingVisitorId;
    private String pendingParkName;
    private String pendingDate;
    private String pendingTime;
    private String pendingVisitorCount;
    private String pendingEmail;
    private String pendingPayment;

    private boolean orderCreatedSuccessfully = false;

    public static String cachedVisitorType = "Individual";

    // =========================
    // INIT
    // =========================
    @FXML
    public void initialize() {

        instance = this;

        try {
            ClientUI.send(new Message("GET_ALL_PARKS", null));
        } catch (Exception e) {
            e.printStackTrace();
        }

        timeComboBox.getItems().addAll(
                "09:00","10:00","11:00","12:00",
                "13:00","14:00","15:00","16:00"
        );

        paymentComboBox.getItems().addAll("Pay Now", "Pay Later");

        statusLabel.setText("");
    }

    // =========================
    // STEP 1: submit
    // =========================
    @FXML
    void submitOrder(ActionEvent event) {

        try {
            String visitorId = visitorIdField.getText().trim();
            String parkName = parkComboBox.getValue();
            LocalDate visitDate = visitDatePicker.getValue();
            String time = timeComboBox.getValue();
            String visitorCount = visitorCountField.getText().trim();
            String paymentMethod = paymentComboBox.getValue();

            if (visitorId.isEmpty() || parkName == null || visitDate == null ||
                    time == null || paymentMethod == null || visitorCount.isEmpty()) {

                statusLabel.setText("Please fill in all fields.");
                return;
            }

            if (!visitorId.matches("\\d{9}")) {
                statusLabel.setText("Visitor ID must be exactly 9 digits.");
                return;
            }

            if (visitDate.isBefore(LocalDate.now())) {
                statusLabel.setText("Cannot select past date.");
                return;
            }

            if (!visitorCount.matches("\\d+") || Integer.parseInt(visitorCount) <= 0) {
                statusLabel.setText("Invalid visitor count.");
                return;
            }

            // save state
            pendingVisitorId = visitorId;
            pendingParkName = parkName;
            pendingDate = visitDate.toString();
            pendingTime = time;
            pendingVisitorCount = visitorCount;
            pendingPayment = paymentMethod;

            statusLabel.setText("Fetching visitor type...");

            ClientUI.send(new Message("GET_VISITOR_TYPE", visitorId));

        } catch (Exception e) {
            e.printStackTrace();
            statusLabel.setText("Server connection error.");
        }
    }

    // =========================
    // STEP 2: visitor type
    // =========================
    public void handleVisitorTypeResult(String type) {

        if (type == null) {
            cachedVisitorType = "Individual";
        } else if (type.equals("Individual") || type.equals("SmallGroup")) {
            cachedVisitorType = "SmallGroup";
        } else {
            cachedVisitorType = "OrganizedGroup";
        }

        Platform.runLater(() -> statusLabel.setText("Fetching email..."));

        try {
			ClientUI.send(new Message("GET_VISITOR_EMAIL", pendingVisitorId));
		} catch (Exception e) {
			e.printStackTrace();
		}
    }

    // =========================
    // STEP 3
    // =========================
    public void handleVisitorEmailResult(String emailFromServer) {

        if (emailFromServer != null && !emailFromServer.isEmpty()) {
            pendingEmail = emailFromServer;
        }

        Platform.runLater(() -> statusLabel.setText("Creating order..."));

        createOrder();
    }

    // =========================
    // STEP 4: create order
    // =========================
    private void createOrder() {

        ArrayList<String> orderData = new ArrayList<>();
        orderData.add(pendingVisitorId);
        orderData.add(pendingParkName);
        orderData.add(pendingDate);
        orderData.add(pendingTime);
        orderData.add(pendingVisitorCount);
        orderData.add(pendingEmail);
        orderData.add(cachedVisitorType);
        orderData.add(pendingPayment);
        
        try {
			ClientUI.send(new Message("SUBMIT_NEW_ORDER", orderData));
		} catch (Exception e) {
			e.printStackTrace();
		}
    }

    // =========================
    // STEP 5: order result
    // =========================
    public void handleOrderResult(boolean success, String reason) {

        if (!success) {
            Platform.runLater(() ->
                    statusLabel.setText(reason != null ? reason : "Order failed")
            );
            return;
        }

        orderCreatedSuccessfully = true;

        Platform.runLater(() -> {
            statusLabel.setStyle("-fx-text-fill: #27ae60;");
            statusLabel.setText("Order created successfully!");
        });

        new Thread(this::calculatePriceAsync).start();
    }

    // =========================
    // STEP 6: price
    // =========================
    private void calculatePriceAsync() {

        ArrayList<String> paymentData = new ArrayList<>();
        paymentData.add(pendingVisitorId);
        paymentData.add(pendingVisitorCount);
        paymentData.add(pendingPayment);
        paymentData.add(pendingParkName);
        paymentData.add(LocalDate.now().toString());

        try {
			ClientUI.send(new Message("CALCULATE_PRICE_PREORDER", paymentData));
		} catch (Exception e) {
			e.printStackTrace();
		}
    }

    public void handlePriceResult(double price) {

        if (!orderCreatedSuccessfully) return;

        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("SIMULATION");
            alert.setHeaderText("Order Price");
            alert.setContentText("Total price: " + price + " NIS");
            alert.showAndWait();
        });
    }

    // =========================
    // LOAD PARKS
    // =========================
    public void loadParks(ArrayList<String> parks) {

        Platform.runLater(() -> {

            if (parks == null || parks.isEmpty()) {
                statusLabel.setText("No parks available.");
                return;
            }

            parkComboBox.getItems().setAll(parks);
        });
    }
}