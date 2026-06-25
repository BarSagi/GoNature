package GUI;

import java.util.ArrayList;


import Client.ClientUI;
import Common.Message;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

/**
 * Controller class for the Department Manager's request approval/rejection
 * panel. Handles the display of pending park parameter change requests and
 * allows the manager to approve or reject them.
 */
public class DeptManagerApproveRejectPanelController {

	/**
	 * Static instance of this controller for external access.
	 */
	public static DeptManagerApproveRejectPanelController instance;

	@FXML
	private TableView<PendingRequestRow> requestsTable;
	@FXML
	private TableColumn<PendingRequestRow, Integer> colRequestId;
	@FXML
	private TableColumn<PendingRequestRow, Integer> colParkName;
	@FXML
	private TableColumn<PendingRequestRow, String> colType;
	@FXML
	private TableColumn<PendingRequestRow, String> colOldValue;
	@FXML
	private TableColumn<PendingRequestRow, String> colNewValue;
	@FXML
	private TableColumn<PendingRequestRow, String> colStatus;
	@FXML
	private TableColumn<PendingRequestRow, String> colStartDate;
	@FXML
	private TableColumn<PendingRequestRow, String> colEndDate;

	@FXML
	private Label statusLabel;

	private final ObservableList<PendingRequestRow> tableData = FXCollections.observableArrayList();

	/**
	 * Initializes the controller, sets up table column factories, and loads the
	 * initial list of pending requests.
	 */
	@FXML
	public void initialize() {
		instance = this;

		colRequestId.setCellValueFactory(new PropertyValueFactory<>("requestId"));
		colParkName.setCellValueFactory(new PropertyValueFactory<>("parkName"));
		colType.setCellValueFactory(new PropertyValueFactory<>("requestType"));
		colOldValue.setCellValueFactory(new PropertyValueFactory<>("oldValue"));
		colNewValue.setCellValueFactory(new PropertyValueFactory<>("newValue"));
		colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
		colStartDate.setCellValueFactory(new PropertyValueFactory<>("startDate"));
		colEndDate.setCellValueFactory(new PropertyValueFactory<>("endDate"));
		requestsTable.setItems(tableData);

		Platform.runLater(() -> {
			refreshRequests(null);
		});
	}

	/**
	 * Refreshes the table view by fetching pending requests from the server.
	 *
	 * @param event The action event triggering the refresh.
	 */
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

	/**
	 * Processes the approval of the currently selected request.
	 *
	 * @param event The action event triggered by the approve button.
	 */
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

	/**
	 * Processes the rejection of the currently selected request.
	 *
	 * @param event The action event triggered by the reject button.
	 */
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

	/**
	 * Maps raw data from the server into the table data format.
	 *
	 * @param rawRequests A list of requests received from the server.
	 */
	public void loadRequests(ArrayList<ArrayList<String>> rawRequests) {
		tableData.clear();

		for (ArrayList<String> row : rawRequests) {
            String requestType;
            String value;
            switch (row.get(2)) {
            case "CasualGap":
                requestType = "Casual Gap Change Request";
                value = String.valueOf(Integer.parseInt(row.get(4)));
                break;

            case "AvgStayDuration":
                requestType = "Average Stay Duration Change Request";
                value = String.valueOf(Double.parseDouble(row.get(4)));
                break;

            case "Promotion":
                requestType = "New Promotion Request";
                value = String.valueOf(Double.parseDouble(row.get(4)));
                value += "%";
                break;

            case "MaxCapacity":
                requestType = "Max Capacity Change Request";
                value = String.valueOf(Integer.parseInt(row.get(4)));
                break;

            default:
                requestType = "Uknown";
                value = "Unkown";
            }
			tableData.add(new PendingRequestRow(Integer.parseInt(row.get(0)), row.get(1), requestType, row.get(3),
					value, row.get(5), row.get(6), row.get(7)));
		}
	}

	/**
	 * Updates the status label in the GUI.
	 *
	 * @param text The status message to display.
	 */
	public void showStatus(String text) {
		statusLabel.setText(text);
	}

	/**
	 * Inner class representing a row of data in the requests table.
	 */
	public static class PendingRequestRow {
		private final int requestId;
		private final String parkName;
		private final String requestType;
		private final String oldValue;
		private final String newValue;
		private final String status;
		private final String startDate;
		private final String endDate;

		/**
		 * Constructs a new row for the request table.
		 */
		public PendingRequestRow(int requestId, String parkName, String requestType, String oldValue, String newValue,
				String status, String startDate, String endDate) {
			this.requestId = requestId;
			this.parkName = parkName;
			this.requestType = requestType;
			this.oldValue = oldValue;
			this.newValue = newValue;
			this.status = status;
			this.startDate = startDate;
			this.endDate = endDate;
		}


		public String getStartDate() {
			return startDate;
		}

		public String getEndDate() {
			return endDate;
		}

		public int getRequestId() {
			return requestId;
		}

		public String getParkName() {
			return parkName;
		}

		public String getRequestType() {
			return requestType;
		}

		public String getOldValue() {
			return oldValue;
		}

		public String getNewValue() {
			return newValue;
		}

		public String getStatus() {
			return status;
		}
	}
}