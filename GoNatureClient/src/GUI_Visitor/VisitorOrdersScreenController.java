package GUI_Visitor;

import java.sql.Date;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Optional;
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
import javafx.scene.control.TableCell;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.application.Platform;

public class VisitorOrdersScreenController {

	public static VisitorOrdersScreenController instance;

	private boolean pendingPopupShown = false;
	private boolean reminderPopupShown = false;

	private ArrayList<String> dbParksList = new ArrayList<>();

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

	private ObservableList<Order> tableData = FXCollections.observableArrayList();

	@FXML
	private BorderPane mainBorderPane;

	@FXML
	public void initialize() {
		instance = this;

		try {
			ClientUI.send(new Message("GET_ALL_PARKS", null));
		} catch (Exception e) {
			e.printStackTrace();
		}

		colId.setCellValueFactory(new PropertyValueFactory<>("orderId"));
		colDate.setCellValueFactory(new PropertyValueFactory<>("visitDate"));
		colTime.setCellValueFactory(new PropertyValueFactory<>("visitTime"));
		colVisitors.setCellValueFactory(new PropertyValueFactory<>("visitorCount"));
		colType.setCellValueFactory(new PropertyValueFactory<>("orderType"));
		colStatus.setCellValueFactory(new PropertyValueFactory<>("orderStatus"));

		colPark.setCellValueFactory(new PropertyValueFactory<>("parkId"));

		colPark.setCellFactory(column -> {
			return new TableCell<Order, Integer>() {
				@Override
				protected void updateItem(Integer parkId, boolean empty) {
					super.updateItem(parkId, empty);

					if (empty || parkId == null) {
						setText(null);
					} else {
						int index = parkId - 1;

						// Only display if the list from the server is ready and valid
						if (dbParksList != null && !dbParksList.isEmpty() && index >= 0 && index < dbParksList.size()) {
							setText(dbParksList.get(index));
						} else {
							// Leave blank momentarily; loadParks() will refresh the table once data arrives
							setText("");
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
			if (event.getClickCount() == 2 && ordersTable.getSelectionModel().getSelectedItem() != null) {
				showTicket(null);
			}
		});
	}

	public void loadParks(ArrayList<String> parks) {
		if (parks != null) {
			this.dbParksList = parks;
			Platform.runLater(() -> ordersTable.refresh());
		}
	}

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

					Alert alert = new Alert(AlertType.INFORMATION);
					alert.setTitle("New SMS Notification");
					alert.setHeaderText("A place has become available for your waiting list order.");
					alert.setContentText("You received this notification at: " + receivedTime
							+ "\n\nYou have one hour to confirm your order.");
					alert.showAndWait();
				}

				if (!reminderPopupShown && "PendingVisitReminder".equalsIgnoreCase(order.getOrderStatus())) {
					reminderPopupShown = true;

					java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter
							.ofPattern("dd/MM/yyyy HH:mm");
					String receivedTime = java.time.LocalDateTime.now().format(formatter);

					Alert alert = new Alert(AlertType.INFORMATION);
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
	void editOrder(ActionEvent event) {
		Order selectedOrder = ordersTable.getSelectionModel().getSelectedItem();

		if (selectedOrder == null) {
			showErrorAlert("Please select an order to edit!");
			return;
		}

		if ("Canceled".equalsIgnoreCase(selectedOrder.getOrderStatus())) {
			showErrorAlert("You cannot edit a cancelled order!");
			return;
		}

		try {
			javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(
					getClass().getResource("/GUI_Visitor/EditOrder.fxml"));
			javafx.scene.Parent editView = loader.load();

			EditOrderController editController = loader.getController();
			editController.setOrderData(selectedOrder);
			mainBorderPane.setCenter(editView);

		} catch (Exception e) {
			System.out.println("Error loading Edit Order pane.");
			e.printStackTrace();
		}
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
		Order selectedOrder = ordersTable.getSelectionModel().getSelectedItem();

		if (selectedOrder == null) {
			showErrorAlert("Please select an order to cancel!");
			return;
		}

		if ("Canceled".equalsIgnoreCase(selectedOrder.getOrderStatus())) {
			showErrorAlert("This order is already cancelled!");
			return;
		}

		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle("Cancel Order");
		alert.setHeaderText("Cancel Order #" + selectedOrder.getOrderId());
		alert.setContentText("Are you sure you want to cancel this order? This action cannot be undone.");

		Optional<ButtonType> result = alert.showAndWait();

		if (result.isPresent() && result.get() == ButtonType.OK) {
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
		Alert alert = new Alert(AlertType.ERROR);
		alert.setTitle("Error");
		alert.setHeaderText("Invalid Action");
		alert.setContentText(message);
		alert.showAndWait();
	}

	@FXML
	void showTicket(ActionEvent event) {
		Order selectedOrder = ordersTable.getSelectionModel().getSelectedItem();

		if (selectedOrder == null) {
			showErrorAlert("Please select an order to view its ticket!");
			return;
		}

		if (!"Approved".equalsIgnoreCase(selectedOrder.getOrderStatus())) {
			showErrorAlert("Only approved orders have an active entrance ticket.");
			return;
		}

		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle("Entrance Ticket");
		alert.setHeaderText("Entrance Ticket for Order #" + selectedOrder.getOrderId());

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
		if (GoNatureClient.currentVisitor != null) {
			try {
				String visitorId = GoNatureClient.currentVisitor.getVisitorId();
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

		if (selectedOrder == null) {
			showErrorAlert("Please select an order to report exit!");
			return;
		}

		if (!"Entered".equalsIgnoreCase(selectedOrder.getOrderStatus())) {
			showErrorAlert("You can only report exit for an order that is currently marked as 'Entered'.");
			return;
		}

		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle("Report Exit");
		alert.setHeaderText("Report Exit for Order #" + selectedOrder.getOrderId());
		alert.setContentText(
				"Are you sure you want to report exit? This will mark the order as fulfilled and free up park capacity.");

		Optional<ButtonType> result = alert.showAndWait();

		if (result.isPresent() && result.get() == ButtonType.OK) {
			try {
				ArrayList<String> dataToServer = new ArrayList<>();
				String visitorId = GoNatureClient.currentVisitor.getVisitorId();
				dataToServer.add(visitorId);
				dataToServer.add(String.valueOf(selectedOrder.getParkId()));
				dataToServer.add(String.valueOf(selectedOrder.getVisitorCount()));

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