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
	public void getOrders(ActionEvent event) {
		ClientUI.client.getOrders();
	}

	@FXML
	public void updateOrder(ActionEvent event) {
		try {
			int orderId = Integer.parseInt(orderNumberField.getText().trim());
			String visitDate = orderDateField.getText().trim();
			int visitorCount = Integer.parseInt(visitorsField.getText().trim());

			ClientUI.client.updateOrder(orderId, visitDate, visitorCount);

		} catch (Exception e) {
			ordersArea.setText("Invalid input!");
		}
	}

	public void showOrders(ArrayList<Order> orders) {
		ordersArea.clear();

		for (Order order : orders) {
			ordersArea.appendText(
				"Order ID: " + order.getOrderId() +
				" | Park ID: " + order.getParkId() +
				" | Visitor ID: " + order.getVisitorId() +
				" | Visit Date: " + order.getVisitDate() +
				" | Visit Time: " + order.getVisitTime() +
				" | Visitors: " + order.getVisitorCount() +
				" | Email: " + order.getEmail() +
				" | Type: " + order.getOrderType() +
				" | Status: " + order.getOrderStatus() +
				"\n"
			);
		}
	}

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

	@FXML
	void goBack(ActionEvent event) {
		ClientUI.changeScreen("/GUI/LoginVisitor.fxml", "GoNature - Visitor Login");
	}

	public void log(String msg) {
		ordersArea.setText(msg);
	}
}