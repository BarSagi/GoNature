package GUI_Visitor;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

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

	@FXML
	public void initialize() {
		instance = this;

		// CRITICAL: The strings here MUST match the variable names in Entity.Order
		// exactly!
		colId.setCellValueFactory(new PropertyValueFactory<>("orderId"));
		colDate.setCellValueFactory(new PropertyValueFactory<>("visitDate"));
		colTime.setCellValueFactory(new PropertyValueFactory<>("visitTime"));
		colVisitors.setCellValueFactory(new PropertyValueFactory<>("visitorCount"));
		colType.setCellValueFactory(new PropertyValueFactory<>("orderType"));
		colStatus.setCellValueFactory(new PropertyValueFactory<>("orderStatus"));

		// Bind the data list to the table
		ordersTable.setItems(tableData);

		if (GoNatureClient.currentVisitor != null) {
			welcomeLabel.setText("Welcome, " + GoNatureClient.currentVisitor.getFirstName() + "!");
		} else {
			welcomeLabel.setText("Welcome!");
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