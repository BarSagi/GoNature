package GUI;

import java.sql.Date;
import java.sql.Time;
import java.util.ArrayList;

import Client.ClientUI;
import Client.GoNatureClient;
import Common.Message;
import Common.Order;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

public class ParkManagerOrdersPanelController {

    public static ParkManagerOrdersPanelController instance;

    @FXML
    private TableView<Order> ordersTable;

    @FXML
    private TableColumn<Order, Integer> colOrderId;
    @FXML
    private TableColumn<Order, String> colVisitorId;
    @FXML
    private TableColumn<Order, Date> colDate;
    @FXML
    private TableColumn<Order, Time> colTime;
    @FXML
    private TableColumn<Order, Integer> colVisitors;
    @FXML
    private TableColumn<Order, String> colType;
    @FXML
    private TableColumn<Order, String> colStatus;

    @FXML
    private Label statusLabel;

    private final ObservableList<Order> tableData = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        instance = this;

        colOrderId.setCellValueFactory(new PropertyValueFactory<>("orderId"));
        colVisitorId.setCellValueFactory(new PropertyValueFactory<>("visitorId"));
        colDate.setCellValueFactory(new PropertyValueFactory<>("visitDate"));
        colTime.setCellValueFactory(new PropertyValueFactory<>("visitTime"));
        colVisitors.setCellValueFactory(new PropertyValueFactory<>("visitorCount"));
        colType.setCellValueFactory(new PropertyValueFactory<>("orderType"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("orderStatus"));

        ordersTable.setItems(tableData);

        refreshOrders(null);
    }

    @FXML
    void refreshOrders(ActionEvent event) {
        statusLabel.setText("");

        try {
            String parkName = GoNatureClient.currentEmployee.getAffiliation();
            ClientUI.send(new Message("GET_PARK_ORDERS", parkName));
        } catch (Exception e) {
            statusLabel.setText("Failed to load park orders.");
            e.printStackTrace();
        }
    }

    public void loadOrders(ArrayList<Order> orders) {
        tableData.clear();
        tableData.addAll(orders);
    }

    public void showStatus(String text) {
        statusLabel.setText(text);
    }
}
