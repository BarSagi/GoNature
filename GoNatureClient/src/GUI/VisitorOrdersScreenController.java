package GUI;

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
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Text;
import javafx.application.Platform;

/**
 * Controller class for the visitor orders management screen. This class handles
 * the display, interaction, and lifecycle of visitor orders, including editing,
 * canceling, confirming, and reporting exits.
 */
public class VisitorOrdersScreenController {

	/**
	 * Singleton instance of this controller to allow access from other controllers.
	 */
	public static VisitorOrdersScreenController instance;

	/** Internal cache for mapping park IDs to park names. */
	private ArrayList<String> dbParksList = new ArrayList<>();

	/** The TableView displaying the list of visitor orders. */
	@FXML
	private TableView<Order> ordersTable;

	/** Displays the visitor's name. */
	@FXML
	private Label welcomeLabel;

	/** Displays the role of the logged-in visitor. */
	@FXML
	private Label roleLabel;

	/** Table columns bound to Order properties. */
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

	/** Observable list containing the orders currently displayed in the table. */
	private ObservableList<Order> tableData = FXCollections.observableArrayList();

	/** Main container for the screen. */
	@FXML
	private BorderPane mainBorderPane;

	/**
	 * Initializes the controller, configures table columns, sets up cell factories,
	 * and requests initial data from the server.
	 */
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
		colType.setCellFactory(column -> {
			return new TableCell<Order, String>() {
				private final Text textNode = new Text();

				@Override
				protected void updateItem(String item, boolean empty) {
					super.updateItem(item, empty);

					if (empty || item == null) {
						setGraphic(null);
					} else {
						textNode.wrappingWidthProperty().bind(column.widthProperty().subtract(10));

						switch (item) {
						case "OrganizedGroup":
							textNode.setText("Guide Tour");
							break;
						case "Individual":
							textNode.setText("Regular Visit");
							break;
						default:
							textNode.setText(item);
							break;
						}
						setGraphic(textNode);
					}
				}
			};
		});

		colStatus.setCellValueFactory(new PropertyValueFactory<>("orderStatus"));
		colStatus.setCellFactory(column -> {
			return new TableCell<Order, String>() {
				private final Text textNode = new Text();

				@Override
				protected void updateItem(String item, boolean empty) {
					super.updateItem(item, empty);

					if (empty || item == null) {
						setGraphic(null);
					} else {
						textNode.wrappingWidthProperty().bind(column.widthProperty().subtract(10));

						switch (item) {
						case "WaitingList":
							textNode.setText("Waiting List");
							break;
						case "PendingConfirmation":
							textNode.setText("Pending SMS/Email Order Confirmation");
							break;
						case "PendingVisitReminder":
							textNode.setText("Pending SMS/Email Reminder Confirmation");
							break;
						default:
							textNode.setText(item);
							break;
						}

						setGraphic(textNode);
					}
				}
			};
		});

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
						if (dbParksList != null && !dbParksList.isEmpty() && index >= 0 && index < dbParksList.size()) {
							setText(dbParksList.get(index));
						} else {
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

		try {
			if (GoNatureClient.currentVisitor != null) {
				String visitorEmail = GoNatureClient.currentVisitor.getEmail();
				ClientUI.send(new Message("CHECK_NOTIFICATIONS", visitorEmail));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Updates the internal list of park names based on data received from the
	 * server.
	 *
	 * @param parks The list of available park names to cache.
	 */
	public void loadParks(ArrayList<String> parks) {
		if (parks != null) {
			this.dbParksList = parks;
			Platform.runLater(() -> ordersTable.refresh());
		}
	}

	/**
	 * Clears and populates the table with new order data.
	 *
	 * @param rawOrders The list of orders to display.
	 */
	public void loadOrders(ArrayList<Order> rawOrders) {
		tableData.clear();
		tableData.addAll(rawOrders);
	}

	/**
	 * Navigates the UI to the edit order screen if an order is selected and
	 * eligible for editing.
	 *
	 * @param event The action event triggering the navigation.
	 */
	@FXML
	void editOrder(ActionEvent event) {
		Order selectedOrder = ordersTable.getSelectionModel().getSelectedItem();

		if (selectedOrder == null) {
			showErrorAlert("Please select an order to edit!");
			return;
		}

		if (!"Approved".equalsIgnoreCase(selectedOrder.getOrderStatus())) {
			showErrorAlert("You can only edit approved orders!");
			return;
		}

		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/GUI/EditOrder.fxml"));
			Parent editView = loader.load();

			EditOrderController editController = loader.getController();
			editController.setOrderData(selectedOrder);
			mainBorderPane.setCenter(editView);

		} catch (Exception e) {
			System.out.println("Error loading Edit Order pane.");
			e.printStackTrace();
		}
	}

	/**
	 * Navigates the user to the "Create Order" screen.
	 *
	 * @param event The action event triggering the navigation.
	 */
	@FXML
	void createNewOrder(ActionEvent event) {
		try {
			ClientUI.changeScreen("/GUI/CreateOrder.fxml", "Create Order");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Loads and displays the visitor's personal details panel in the center pane.
	 *
	 * @param event The action event triggering the display.
	 */
	@FXML
	void showMyDetails(ActionEvent event) {

		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/GUI/VisitorMyDetailsPanel.fxml"));
			Parent detailsView = loader.load();
			mainBorderPane.setCenter(detailsView);

		} catch (Exception e) {
			System.out.println("Error loading My Details pane.");
			e.printStackTrace();
		}
	}

	/**
	 * Initiates the cancellation process for a selected order. Validates the order
	 * status to ensure it is eligible for cancellation.
	 *
	 * @param event The action event triggering the cancellation.
	 */
	@FXML
	void cancelOrder(ActionEvent event) {
		Order selectedOrder = ordersTable.getSelectionModel().getSelectedItem();

		if (selectedOrder == null) {
			showErrorAlert("Please select an order to cancel!");
			return;
		}

		String status = selectedOrder.getOrderStatus();
		if ("Canceled".equalsIgnoreCase(status) || "Fulfilled".equalsIgnoreCase(status)
				|| "Entered".equalsIgnoreCase(status)) {
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
			} catch (Exception e) {
				System.out.println("Error sending cancellation request to server.");
				e.printStackTrace();
			}
		}
	}

	/**
	 * Displays an error dialog to the user with a specific message.
	 *
	 * @param message The error message text to display.
	 */
	private void showErrorAlert(String message) {
		Alert alert = new Alert(AlertType.ERROR);
		alert.setTitle("Error");
		alert.setHeaderText("Invalid Action");
		alert.setContentText(message);
		alert.showAndWait();
	}

	/**
	 * Displays the QR code ticket for the selected order. Only allowed if the order
	 * is in 'Approved' status.
	 *
	 * @param event The action event triggering the ticket display.
	 */
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

	/**
	 * Handles the visitor logout process, notifying the server and switching
	 * screens.
	 *
	 * @param event The action event triggering logout.
	 */
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

	/**
	 * Handles the manual confirmation of a pending order.
	 *
	 * @param event The action event triggering confirmation.
	 */
	@FXML
	void confirmOrder(ActionEvent event) {
		Order selectedOrder = ordersTable.getSelectionModel().getSelectedItem();

		if (selectedOrder == null) {
			showErrorAlert("Please select an order to confirm!");
			return;
		}

		String status = selectedOrder.getOrderStatus();

		if (!"PendingConfirmation".equalsIgnoreCase(status) && !"PendingVisitReminder".equalsIgnoreCase(status)) {
			showErrorAlert("Only orders waiting for confirmation or visit reminders can be confirmed.");
			return;
		}

		try {
			Message msg = new Message("CONFIRM_ORDER", selectedOrder.getOrderId());
			ClientUI.send(msg);
		} catch (Exception e) {
			System.out.println("Error sending confirmation request to server.");
			e.printStackTrace();
		}
	}

	/**
	 * Requests the latest order list from the server to refresh the table.
	 *
	 * @param event The action event triggering the refresh.
	 */
	@FXML
	public void refreshOrders(ActionEvent event) {
		if (GoNatureClient.currentVisitor != null) {
			try {
				String visitorId = GoNatureClient.currentVisitor.getVisitorId();
				Message msg = new Message("FETCH_VISITOR_ORDERS", visitorId);
				ClientUI.send(msg);
			} catch (Exception e) {
				System.out.println("Error sending refresh request to server.");
				e.printStackTrace();
				showErrorAlert("Failed to refresh orders. Please check your connection.");
			}
		} else {
			showErrorAlert("Cannot refresh: No visitor is currently logged in.");
		}
	}

	/**
	 * Initiates the exit reporting process for a selected order.
	 *
	 * @param event The action event triggering the exit report.
	 */
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
			} catch (Exception e) {
				System.out.println("Error sending exit request to server.");
				e.printStackTrace();
				showErrorAlert("Failed to communicate with the server.");
			}
		}
	}
}