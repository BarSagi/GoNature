package GUI;

import Client.ClientUI;
import Common.Message;
import Common.Order;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.sql.Date;
import java.util.ArrayList;

// this method handles client actions
public class ClientController {

	@FXML
	private TextArea ordersArea;

	@FXML
	private TextField orderNumberField;

	@FXML
	private TextField orderDateField;

	@FXML
	private TextField visitorsField;

	// send a request to server to return all orders
	@FXML
	public void getOrders(ActionEvent event) {
		ClientUI.client.getOrders();
	}

	// collect user input to update an order and send the server the details
	@FXML
	public void updateOrder(ActionEvent event) {
		try {
			int orderNumber = Integer.parseInt(orderNumberField.getText());
			Date orderDate = Date.valueOf(orderDateField.getText());
			int visitors = Integer.parseInt(visitorsField.getText());
			Order order = new Order();
			order.setOrderNumber(orderNumber);
			order.setOrderDate(orderDate);
			order.setNumberOfVisitors(visitors);
			ClientUI.client.updateOrder(order);
		} catch (Exception e) {
			ordersArea.setText("Invalid input!");
		}
	}

	// displays a list of orders in the GUI
	public void showOrders(ArrayList<ArrayList<String>> orders) {
		ordersArea.clear();
		for (ArrayList<String> row : orders) {
			String orderNumber = row.get(0);
			String orderDate = row.get(1);
			String numberOfVisitors = row.get(2);
			String confirmationCode = row.get(3);
			String subscriberId = row.get(4);
			String dateOfPlacingOrder = row.get(5);
			ordersArea.appendText("Order Number: " + orderNumber + " " + "Order Date: " + orderDate + " " + "Vistiors: "
					+ numberOfVisitors + " " + "Confirmation code: " + confirmationCode + " " + "Subscriber ID: "
					+ subscriberId + " " + "Date of Placing Order: " + dateOfPlacingOrder + " \n");

		}
	}

	// this method will print to GUI if order is updated successfuly or not
	public void showSuccess(String msg) {
		ordersArea.setText(msg);
	}

	@FXML
	void exit(ActionEvent event) {

		try {

			if (ClientUI.client != null) {
				ClientUI.client.sendToServer(new Message("DISCONNECT", null));
				ClientUI.client.closeConnection();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		Platform.exit();
	}

	// this method will show feedback to the client
	public void log(String msg) {
		ordersArea.setText(msg);
	}
}