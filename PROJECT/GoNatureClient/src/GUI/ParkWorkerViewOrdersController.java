package GUI;

import Client.ClientUI;
import Client.GoNatureClient;
import Common.Message;
import Common.Order;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import java.util.ArrayList;

/**
 * Controller for the park worker's view orders panel. Manages the display of
 * orders for the park in a list view, with custom formatting for order details
 * and status.
 */
public class ParkWorkerViewOrdersController {

	/**
	 * Static instance of this controller for external access.
	 */
	public static ParkWorkerViewOrdersController instance;

	@FXML
	private ListView<Order> ordersListView;

	@FXML
	private Label statusLabel;

	/**
	 * Initializes the controller and sets up the custom cell factory for the
	 * ListView.
	 */
	@FXML
	public void initialize() {
		instance = this;
		setupCustomListView();
	}

	/**
	 * Requests the list of orders for the current park from the server.
	 *
	 * @param event The action event.
	 */
	@FXML
	void loadOrders(ActionEvent event) {
		try {
			Message msg = new Message("GET_ORDERS", GoNatureClient.currentEmployee.getAffiliation());
			ClientUI.send(msg);
			statusLabel.setText("Orders request sent.");
		} catch (Exception e) {
			statusLabel.setText("Failed to load orders.");
			e.printStackTrace();
		}
	}

	/**
	 * Populates the ListView with the retrieved orders.
	 *
	 * @param orders The list of orders to display.
	 */
	public void showOrders(ArrayList<Order> orders) {
		ordersListView.getItems().clear();

		if (orders == null || orders.isEmpty()) {
			statusLabel.setText("No orders found for this park.");
			return;
		}

		ordersListView.getItems().addAll(orders);
		statusLabel.setText("Orders loaded successfully.");
	}

	/**
	 * Configures the custom cell factory to define how each order is rendered in
	 * the ListView.
	 */
	private void setupCustomListView() {
		ordersListView.setCellFactory(param -> new ListCell<Order>() {
			@Override
			protected void updateItem(Order order, boolean empty) {
				super.updateItem(order, empty);

				if (empty || order == null) {
					setText(null);
					setGraphic(null);
				} else {

					Label idLabel = new Label("Order #" + order.getOrderId());
					idLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 15px; -fx-text-fill: #2c3e50;");

					Label detailsLabel = new Label(
							"Visitor ID: " + order.getVisitorId() + " | Email: " + order.getEmail());
					detailsLabel.setStyle("-fx-text-fill: #7f8c8d; -fx-font-size: 13px;");

					VBox leftSection = new VBox(4, idLabel, detailsLabel);
					leftSection.setAlignment(Pos.CENTER_LEFT);

					Label dateLabel = new Label("Date: " + order.getVisitDate());
					dateLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #34495e; -fx-font-size: 14px;");

					Label timeLabel = new Label("Time: " + order.getVisitTime());
					timeLabel.setStyle("-fx-text-fill: #7f8c8d; -fx-font-size: 13px;");

					VBox centerSection = new VBox(4, dateLabel, timeLabel);
					centerSection.setAlignment(Pos.CENTER_LEFT);
					HBox.setHgrow(centerSection, Priority.ALWAYS);
					HBox.setMargin(centerSection, new Insets(0, 0, 0, 40));

					Label countLabel = new Label("Visitors: " + order.getVisitorCount());
					countLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: #2c3e50;");

					Label statusTag = new Label(order.getOrderStatus().toUpperCase());

					String statusColor = "#7f8c8d";
					if ("Approved".equalsIgnoreCase(order.getOrderStatus()))
						statusColor = "#27ae60";
					else if ("Canceled".equalsIgnoreCase(order.getOrderStatus()))
						statusColor = "#e74c3c";
					else if ("WaitingList".equalsIgnoreCase(order.getOrderStatus()))
						statusColor = "#f39c12";
					else if ("Fulfilled".equalsIgnoreCase(order.getOrderStatus()))
						statusColor = "#2980b9";

					statusTag.setStyle("-fx-background-color: " + statusColor + "; -fx-text-fill: white; "
							+ "-fx-font-weight: bold; -fx-font-size: 11px; -fx-padding: 4 10 4 10; -fx-background-radius: 4;");

					VBox rightSection = new VBox(6, countLabel, statusTag);
					rightSection.setAlignment(Pos.CENTER_RIGHT);

					HBox mainLayout = new HBox(leftSection, centerSection, rightSection);
					mainLayout.setAlignment(Pos.CENTER_LEFT);
					mainLayout.setPadding(new Insets(10, 15, 10, 15));

					mainLayout.setStyle(
							"-fx-background-color: #ffffff; -fx-border-color: #ecf0f1; -fx-border-width: 0 0 1px 0;");

					setGraphic(mainLayout);
				}
			}
		});
	}

	/**
	 * Refreshes the orders list from the server.
	 *
	 * @param event The action event.
	 */
	@FXML
	void refreshOrders(ActionEvent event) {
		try {
			Message msg = new Message("GET_ORDERS", GoNatureClient.currentEmployee.getAffiliation());
			ClientUI.send(msg);
			statusLabel.setStyle("-fx-text-fill: #2980b9;");
			statusLabel.setText("Refreshing orders...");
		} catch (Exception e) {
			statusLabel.setStyle("-fx-text-fill: red;");
			statusLabel.setText("Failed to refresh orders.");
			e.printStackTrace();
		}
	}
}