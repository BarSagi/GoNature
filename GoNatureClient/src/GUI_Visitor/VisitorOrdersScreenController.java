package GUI_Visitor;

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
import javafx.scene.layout.BorderPane;

public class VisitorOrdersScreenController {

	public static VisitorOrdersScreenController instance;

	private boolean pendingPopupShown = false;
	private boolean reminderPopupShown = false;
	
	// Tell the TableView to use your Entity.Order class
	@FXML
	private TableView<Order> ordersTable;

	@FXML
	private Label welcomeLabel;

	@FXML
	private Label roleLabel;

	@FXML
	private TableColumn<Order, Integer> colPark;
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

		// 1. Tell the column to grab the "parkId" integer from the Order object
		colPark.setCellValueFactory(new PropertyValueFactory<>("parkId"));

		// 2. The Switch Case: Tell the column how to display that integer!
		colPark.setCellFactory(column -> {
			return new javafx.scene.control.TableCell<Order, Integer>() {
				@Override
				protected void updateItem(Integer parkId, boolean empty) {
					super.updateItem(parkId, empty);

					if (empty || parkId == null) {
						setText(null);
					} else {
						switch (parkId) {
						case 1:
							setText("Karmel");
							break;
						case 2:
							setText("Banias");
							break;
						case 3:
							setText("Yarkon");
							break;
						default:
							setText("Unknown Park (" + parkId + ")");
							break;
						}
					}
				}
			};
		});

		ordersTable.setItems(tableData);

		if (GoNatureClient.currentVisitor != null) {
			welcomeLabel.setText("Welcome, " + GoNatureClient.currentVisitor.getFirstName() + "!");

			roleLabel.setText("Role: " + GoNatureClient.currentVisitor.getVisitorType());

		} else {
			welcomeLabel.setText("Welcome!");

			roleLabel.setText("Role: Unknown");
		}

		ordersTable.setOnMouseClicked(event -> {
			// Check if it was a double click AND a row is actually selected
			if (event.getClickCount() == 2 && ordersTable.getSelectionModel().getSelectedItem() != null) {
				showTicket(null); // CHANGED: Trigger the ticket display
			}
		});
	}

	@FXML
	void editOrder(ActionEvent event) {
		Order selectedOrder = ordersTable.getSelectionModel().getSelectedItem();

		// 1. Validate selection
		if (selectedOrder == null) {
			showErrorAlert("Please select an order to edit!");
			return;
		}

		// 2. Prevent editing of cancelled orders
		if ("Canceled".equalsIgnoreCase(selectedOrder.getOrderStatus())) {
			showErrorAlert("You cannot edit a cancelled order!");
			return;
		}

		try {
			javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(
					getClass().getResource("/GUI_Visitor/EditOrder.fxml"));
			javafx.scene.Parent editView = loader.load();

			EditOrderController editController = loader.getController();

			// 3. Pass the 3 arguments: the order, the border pane, and the current center
			// (for canceling)
			editController.setOrderData(selectedOrder);

			mainBorderPane.setCenter(editView);

		} catch (Exception e) {
			System.out.println("Error loading Edit Order pane.");
			e.printStackTrace();
		}
	}

	// Method to parse the raw Strings from the Server into your Order entities
	public void loadOrders(ArrayList<Order> rawOrders) {
		tableData.clear();
		pendingPopupShown = false;
		reminderPopupShown = false;

		for (Order order : rawOrders) {
			try {
				tableData.add(order);

				if (!pendingPopupShown && "PendingConfirmation".equalsIgnoreCase(order.getOrderStatus())) {
					pendingPopupShown = true;

					java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter
							.ofPattern("dd/MM/yyyy HH:mm");

					String receivedTime = java.time.LocalDateTime.now().format(formatter);

					javafx.scene.control.Alert alert = new javafx.scene.control.Alert(
							javafx.scene.control.Alert.AlertType.INFORMATION);
					alert.setTitle("New SMS Notification");
					alert.setHeaderText("A place has become available for your waiting list order.");
					alert.setContentText("You received this notification at: " + receivedTime
							+ "\n\nYou have one hour to confirm your order.");
					alert.showAndWait();
				}
				
				if (!reminderPopupShown && "PendingVisitReminder".equalsIgnoreCase(order.getOrderStatus())) {
					reminderPopupShown = true;

					java.time.format.DateTimeFormatter formatter =
							java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

					String receivedTime = java.time.LocalDateTime.now().format(formatter);

					javafx.scene.control.Alert alert = new javafx.scene.control.Alert(
							javafx.scene.control.Alert.AlertType.INFORMATION);
					alert.setTitle("Visit Reminder SMS");
					alert.setHeaderText("Reminder: your visit is tomorrow.");
					alert.setContentText("You received this reminder at: " + receivedTime
							+ "\n\nPlease confirm or cancel your visit within 2 hours.");
					alert.showAndWait();
				}

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
			showErrorAlert("Please select an order to cancel!");
			return;
		}

		if ("Canceled".equalsIgnoreCase(selectedOrder.getOrderStatus())) {
			showErrorAlert("This order is already cancelled!");
			return;
		}

		// 3. Ask for confirmation!
		javafx.scene.control.Alert alert = new javafx.scene.control.Alert(
				javafx.scene.control.Alert.AlertType.CONFIRMATION);
		alert.setTitle("Cancel Order");
		alert.setHeaderText("Cancel Order #" + selectedOrder.getOrderId());
		alert.setContentText("Are you sure you want to cancel this order? This action cannot be undone.");

		// Wait for the user's response
		java.util.Optional<javafx.scene.control.ButtonType> result = alert.showAndWait();

		if (result.isPresent() && result.get() == javafx.scene.control.ButtonType.OK) {
			try {
				Message msg = new Message("CANCEL_ORDER", selectedOrder.getOrderId());
				ClientUI.send(msg);
				System.out.println("Cancellation request sent for Order ID: " + selectedOrder.getOrderId());
			} catch (Exception e) {
				System.out.println("Error sending cancellation request to server.");
				e.printStackTrace();
			}
		}
	}

	private void showErrorAlert(String message) {
		javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR);
		alert.setTitle("Error");
		alert.setHeaderText("Invalid Action");
		alert.setContentText(message);
		alert.showAndWait();
	}

	@FXML
	void showTicket(ActionEvent event) {
		Order selectedOrder = ordersTable.getSelectionModel().getSelectedItem();

		// 1. Validate selection
		if (selectedOrder == null) {
			showErrorAlert("Please select an order to view its ticket!");
			return;
		}

		// 2. Only approved orders should get to see their entrance code!
		if (!"Approved".equalsIgnoreCase(selectedOrder.getOrderStatus())) {
			showErrorAlert("Only approved orders have an active entrance ticket.");
			return;
		}

		// 3. Show a nice pop-up with the QR Code
		javafx.scene.control.Alert alert = new javafx.scene.control.Alert(
				javafx.scene.control.Alert.AlertType.INFORMATION);
		alert.setTitle("Entrance Ticket");
		alert.setHeaderText("Entrance Ticket for Order #" + selectedOrder.getOrderId());

		// If you added qrCode to the Order object, use it here:
		String code = selectedOrder.getQrCode() != null ? selectedOrder.getQrCode() : "N/A";

		alert.setContentText("Please present this code to the Park Employee at the entrance:\n\n" + "QR CODE: " + code);

		alert.showAndWait();
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

	@FXML
	void confirmOrder(ActionEvent event) {
		Order selectedOrder = ordersTable.getSelectionModel().getSelectedItem();

		if (selectedOrder == null) {
			showErrorAlert("Please select an order to confirm!");
			return;
		}

		if (!"PendingConfirmation".equalsIgnoreCase(selectedOrder.getOrderStatus())) {
			showErrorAlert("Only orders waiting for confirmation can be confirmed.");
			return;
		}

		try {
			Message msg = new Message("CONFIRM_ORDER", selectedOrder.getOrderId());
			ClientUI.send(msg);
			System.out.println("Confirmation request sent for Order ID: " + selectedOrder.getOrderId());
		} catch (Exception e) {
			System.out.println("Error sending confirmation request to server.");
			e.printStackTrace();
		}
	}

	@FXML
	public void refreshOrders(ActionEvent event) {
		// Ensure we have a logged-in visitor before trying to fetch orders
		if (GoNatureClient.currentVisitor != null) {
			try {
				String visitorId = GoNatureClient.currentVisitor.getVisitorId();

				// Create the message.
				// IMPORTANT: Ensure "FETCH_VISITOR_ORDERS" exactly matches the command
				// your Server Strategy expects to grab a visitor's orders!
				Message msg = new Message("FETCH_VISITOR_ORDERS", visitorId);

				ClientUI.send(msg);
				System.out.println("Refresh request sent for Visitor ID: " + visitorId);

			} catch (Exception e) {
				System.out.println("Error sending refresh request to server.");
				e.printStackTrace();
				showErrorAlert("Failed to refresh orders. Please check your connection.");
			}
		} else {
			showErrorAlert("Cannot refresh: No visitor is currently logged in.");
		}
	}

	@FXML
	void reportExit(ActionEvent event) {
		Order selectedOrder = ordersTable.getSelectionModel().getSelectedItem();

		// 1. Validate selection
		if (selectedOrder == null) {
			showErrorAlert("Please select an order to report exit!");
			return;
		}

		// 2. Prevent exiting an order that isn't currently inside the park
		if (!"Entered".equalsIgnoreCase(selectedOrder.getOrderStatus())) {
			showErrorAlert("You can only report exit for an order that is currently marked as 'Entered'.");
			return;
		}

		// 3. Ask for confirmation
		javafx.scene.control.Alert alert = new javafx.scene.control.Alert(
				javafx.scene.control.Alert.AlertType.CONFIRMATION);
		alert.setTitle("Report Exit");
		alert.setHeaderText("Report Exit for Order #" + selectedOrder.getOrderId());
		alert.setContentText(
				"Are you sure you want to report exit? This will mark the order as fulfilled and free up park capacity.");

		java.util.Optional<javafx.scene.control.ButtonType> result = alert.showAndWait();

		if (result.isPresent() && result.get() == javafx.scene.control.ButtonType.OK) {
			try {
				// 4. Prepare the ArrayList<String> exactly as ExitVisitorStrategy expects it
				ArrayList<String> dataToServer = new ArrayList<>();

				// Grab the visitor ID from the currently logged-in user
				String visitorId = GoNatureClient.currentVisitor.getVisitorId();
				dataToServer.add(visitorId);
				dataToServer.add(String.valueOf(selectedOrder.getParkId()));
				dataToServer.add(String.valueOf(selectedOrder.getVisitorCount()));

				// 5. Send the specific "EXIT_VISITOR" command with the ArrayList
				Message msg = new Message("EXIT_VISITOR", dataToServer);
				ClientUI.send(msg);

				System.out.println("Exit request sent for Visitor ID: " + visitorId);

			} catch (Exception e) {
				System.out.println("Error sending exit request to server.");
				e.printStackTrace();
				showErrorAlert("Failed to communicate with the server.");
			}
		}
	}
}