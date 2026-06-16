package GUI_Visitor;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;

import java.sql.Date;
import java.sql.Time;
import java.util.ArrayList;

import Client.ClientUI;
import Client.GoNatureClient;
import Common.Message;
import Common.Order;

public class VisitorOrdersScreenController {

	public static VisitorOrdersScreenController instance;

	// Tell the TableView to use your Entity.Order class
	@FXML
	private TableView<Order> ordersTable;

	@FXML
	private Label welcomeLabel;

	@FXML
	private TableColumn<Order, Integer> colId;
	@FXML
	private TableColumn<Order, Date> colDate;
	@FXML
	private TableColumn<Order, Time> colTime;
	@FXML
	private TableColumn<Order, String> colType;
	@FXML
	private TableColumn<Order, String> colStatus;
	@FXML
	private TableColumn<Order, Integer> colVisitors;

	// This list holds the actual Order objects
	private ObservableList<Order> tableData = FXCollections.observableArrayList();

	// Add this new FXML variable for the error label
	@FXML
	private Label errorLabel;

	@FXML
	private BorderPane mainBorderPane;

	@FXML
	public void initialize() {
		instance = this;

		colId.setCellValueFactory(new PropertyValueFactory<>("orderId"));
		colDate.setCellValueFactory(new PropertyValueFactory<>("visitDate"));
		colTime.setCellValueFactory(new PropertyValueFactory<>("visitTime"));
		colVisitors.setCellValueFactory(new PropertyValueFactory<>("visitorCount"));
		colType.setCellValueFactory(new PropertyValueFactory<>("orderType"));
		colStatus.setCellValueFactory(new PropertyValueFactory<>("orderStatus"));

		ordersTable.setItems(tableData);

		if (GoNatureClient.currentVisitor != null) {
			welcomeLabel.setText("Welcome, " + GoNatureClient.currentVisitor.getFirstName() + "!");
		} else {
			welcomeLabel.setText("Welcome!");
		}

		// --- NEW: DOUBLE CLICK LISTENER ---
		ordersTable.setOnMouseClicked(event -> {
			// Check if it was a double click AND a row is actually selected
			if (event.getClickCount() == 2 && ordersTable.getSelectionModel().getSelectedItem() != null) {
				editOrder(null); // Trigger the edit logic
			}
		});
	}

	@FXML
	void editOrder(ActionEvent event) {
		Order selectedOrder = ordersTable.getSelectionModel().getSelectedItem();

		if (selectedOrder == null) {
			errorLabel.setText("Please select an order to edit!"); // Clarify the error
			errorLabel.setVisible(true);
			return;
		}

		errorLabel.setVisible(false);

		try {
			javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(
					getClass().getResource("/GUI_Visitor/EditOrder.fxml"));
			javafx.scene.Parent editView = loader.load();

			EditOrderController editController = loader.getController();

			// --- UPDATED LINE: Pass mainBorderPane.getCenter() as the third argument! ---
			editController.setOrderData(selectedOrder, mainBorderPane);

			mainBorderPane.setCenter(editView);

		} catch (Exception e) {
			System.out.println("Error loading Edit Order pane.");
			e.printStackTrace();
		}
	}

	// Method to parse the raw Strings from the Server into your Order entities
	public void loadOrders(ArrayList<Order> rawOrders) {
		tableData.clear();

		for (Order order : rawOrders) {
			try {

				tableData.add(order);

			} catch (Exception e) {
				System.out.println("Error displaying order in table: " + e.getMessage());
				e.printStackTrace();
			}
		}
		System.out.println("Controller: Finished loading. tableData size is now: " + tableData.size());
	}

	@FXML
	void createNewOrder(ActionEvent event) {
		try {
			ClientUI.changeScreen("/GUI/CreateOrder.fxml", "Create Order");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@FXML
	void cancelOrder(ActionEvent event) {
		// 1. Grab the selected order
		Order selectedOrder = ordersTable.getSelectionModel().getSelectedItem();

		// 2. Validate selection
		if (selectedOrder == null) {
			errorLabel.setText("Please select an order to cancel!");
			errorLabel.setVisible(true);
			return;
		}
		errorLabel.setVisible(false);

		// 3. Optional but Excellent UX: Ask for confirmation!
		javafx.scene.control.Alert alert = new javafx.scene.control.Alert(
				javafx.scene.control.Alert.AlertType.CONFIRMATION);
		alert.setTitle("Cancel Order");
		alert.setHeaderText("Cancel Order #" + selectedOrder.getOrderId());
		alert.setContentText("Are you sure you want to cancel this order? This action cannot be undone.");

		// Wait for the user's response
		java.util.Optional<javafx.scene.control.ButtonType> result = alert.showAndWait();

		if (result.isPresent() && result.get() == javafx.scene.control.ButtonType.OK) {
			try {
				// 4. Send the cancellation message to the server
				// Assuming your Server will have a "CANCEL_ORDER" Strategy that updates the
				// status in the DB
				Message msg = new Message("CANCEL_ORDER", selectedOrder.getOrderId());
				ClientUI.send(msg);

				System.out.println("Cancellation request sent for Order ID: " + selectedOrder.getOrderId());

			} catch (Exception e) {
				System.out.println("Error sending cancellation request to server.");
				e.printStackTrace();
			}
		}
	}

	@FXML
	void logout(ActionEvent event) {
		try {
			String userID = GoNatureClient.currentVisitor.getVisitorId();
			GoNatureClient.currentVisitor = null;
			Message msg = new Message("CLIENT_LOGOUT", userID);

			try {
				ClientUI.send(msg);
			} catch (Exception e) {
				System.out.println("Error sending message to server");
				e.printStackTrace();
			}
			ClientUI.changeScreen("/GUI/LoginRoute.fxml", "GoNature Login");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}