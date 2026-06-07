package GUI;

import Client.ClientUI;
import Common.Message;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;

import java.util.ArrayList;

public class ParkWorkerViewOrdersController {

    public static ParkWorkerViewOrdersController instance;

    @FXML
    private ListView<String> ordersListView;

    @FXML
    private Label statusLabel;

    @FXML
    public void initialize() {
        instance = this;
    }

    @FXML
    void goBack(ActionEvent event) {
        ClientUI.changeScreen("/GUI/ParkWorker.fxml", "Park Worker");
    }

    @FXML
    void loadOrders(ActionEvent event) {
        try {
            Message msg = new Message("GET_ORDERS", null);
            ClientUI.client.sendToServer(msg);
            statusLabel.setText("Orders request sent.");
        } catch (Exception e) {
            statusLabel.setText("Failed to load orders.");
            e.printStackTrace();
        }
    }

    public void showOrders(ArrayList<ArrayList<String>> orders) {
        ordersListView.getItems().clear();

        for (ArrayList<String> order : orders) {
            ordersListView.getItems().add(order.toString());
        }

        statusLabel.setText("Orders loaded successfully.");
    }
}