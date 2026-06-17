package GUI;

import java.util.ArrayList;

import Client.ClientUI;
import Common.Message;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

public class DeptManagerApproveRejectPanelController {

    public static DeptManagerApproveRejectPanelController instance;

    @FXML
    private TableView<PendingRequestRow> requestsTable;

    @FXML
    private TableColumn<PendingRequestRow, Integer> colRequestId;
    @FXML
    private TableColumn<PendingRequestRow, Integer> colParkId;
    @FXML
    private TableColumn<PendingRequestRow, String> colType;
    @FXML
    private TableColumn<PendingRequestRow, String> colOldValue;
    @FXML
    private TableColumn<PendingRequestRow, String> colNewValue;
    @FXML
    private TableColumn<PendingRequestRow, String> colStatus;

    @FXML
    private Label statusLabel;

    private final ObservableList<PendingRequestRow> tableData = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        instance = this;

        colRequestId.setCellValueFactory(new PropertyValueFactory<>("requestId"));
        colParkId.setCellValueFactory(new PropertyValueFactory<>("parkId"));
        colType.setCellValueFactory(new PropertyValueFactory<>("requestType"));
        colOldValue.setCellValueFactory(new PropertyValueFactory<>("oldValue"));
        colNewValue.setCellValueFactory(new PropertyValueFactory<>("newValue"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));

        requestsTable.setItems(tableData);

        refreshRequests(null);
    }

    @FXML
    public void refreshRequests(ActionEvent event) {
        statusLabel.setText("");
        try {
            ClientUI.send(new Message("GET_PENDING_REQUESTS", null));
        } catch (Exception e) {
            statusLabel.setText("Failed to load requests.");
            e.printStackTrace();
        }
    }

    @FXML
    void approveRequest(ActionEvent event) {
        PendingRequestRow selected = requestsTable.getSelectionModel().getSelectedItem();

        if (selected == null) {
            statusLabel.setText("Please select a request first.");
            return;
        }

        try {
            ClientUI.send(new Message("APPROVE_REQUEST", selected.getRequestId()));
        } catch (Exception e) {
            statusLabel.setText("Failed to approve request.");
            e.printStackTrace();
        }
    }

    @FXML
    void rejectRequest(ActionEvent event) {
        PendingRequestRow selected = requestsTable.getSelectionModel().getSelectedItem();

        if (selected == null) {
            statusLabel.setText("Please select a request first.");
            return;
        }

        try {
            ClientUI.send(new Message("REJECT_REQUEST", selected.getRequestId()));
        } catch (Exception e) {
            statusLabel.setText("Failed to reject request.");
            e.printStackTrace();
        }
    }

    public void loadRequests(ArrayList<ArrayList<String>> rawRequests) {
        tableData.clear();

        for (ArrayList<String> row : rawRequests) {
            tableData.add(new PendingRequestRow(
                    Integer.parseInt(row.get(0)),
                    Integer.parseInt(row.get(1)),
                    row.get(2),
                    row.get(3),
                    row.get(4),
                    row.get(5)
            ));
        }
    }

    public void showStatus(String text) {
        statusLabel.setText(text);
    }

    public static class PendingRequestRow {
        private final int requestId;
        private final int parkId;
        private final String requestType;
        private final String oldValue;
        private final String newValue;
        private final String status;

        public PendingRequestRow(int requestId, int parkId, String requestType, String oldValue, String newValue, String status) {
            this.requestId = requestId;
            this.parkId = parkId;
            this.requestType = requestType;
            this.oldValue = oldValue;
            this.newValue = newValue;
            this.status = status;
        }

        public int getRequestId() { return requestId; }
        public int getParkId() { return parkId; }
        public String getRequestType() { return requestType; }
        public String getOldValue() { return oldValue; }
        public String getNewValue() { return newValue; }
        public String getStatus() { return status; }
    }
}