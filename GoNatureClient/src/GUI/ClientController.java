package GUI;

import Client.ClientUI;
import Entity.Order;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.sql.Date;
import java.util.ArrayList;

public class ClientController {

	@FXML
	private TextArea ordersArea;

	@FXML
	private TextField orderNumberField;

	@FXML
	private TextField orderDateField;

	@FXML
	private TextField visitorsField;

	@FXML
	public void getOrders() {
		ClientUI.client.getOrders();
	}
	
	@FXML
	public void updateOrder() {

		int orderNumber = Integer.parseInt(orderNumberField.getText());
		Date orderDate = Date.valueOf(orderDateField.getText());
		int visitors = Integer.parseInt(visitorsField.getText());
		Order order = new Order();
		order.setOrderNumber(orderNumber);
		order.setOrderDate(orderDate);
		order.setNumberOfVisitors(visitors);
		ClientUI.client.updateOrder(order);
	}

	public void showOrders(ArrayList<ArrayList<String>> orders) {

	    for (ArrayList<String> row : orders) {
	        System.out.println(row);
	    }
	}
}