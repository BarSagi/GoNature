package Client;

import java.util.ArrayList;
import Common.Message;
import javafx.application.Platform;
import OCSFUtils.AbstractClient;
import GUI.*;
import Entity.*;

public class GoNatureClient extends AbstractClient {

	public static boolean awaitResponse = false;

	public GoNatureClient(String host, int port) {
		super(host, port);
	}

	@Override
	protected void handleMessageFromServer(Object msg) {
		// 1. Check if the object is our custom Message class
		if (msg instanceof Message) {
			Message message = (Message) msg;
			String command = message.getCommand();

			// 2. Route the message based on its command
			switch (command) {

			case "VISITOR_REGISTRATION_RESULT":
				boolean isRegistered = (boolean) message.getData();
				if (isRegistered) {
					System.out.println("Client: Registration Successful!");
					Platform.runLater(() -> {
						try {
							ClientUI.changeScreen("/GUI/CreateOrder.fxml", "Create Order");
						} catch (Exception e) {
							e.printStackTrace();
						}
					});
				} else {
					// Note: If you have a specific label on the registration screen for errors,
					// you'd update it here inside a Platform.runLater!
					System.out.println("Client: Registration Failed.");
				}
				break;

			case "ORDER_CREATION_RESULT":
				boolean isOrderCreated = (boolean) message.getData();
				Platform.runLater(() -> {
					if (isOrderCreated) {
						// Example of showing a success pop-up!
						javafx.scene.control.Alert alert = new javafx.scene.control.Alert(
								javafx.scene.control.Alert.AlertType.INFORMATION);
						alert.setTitle("Order Successful");
						alert.setHeaderText(null);
						alert.setContentText(
								"Your order has been successfully created! We look forward to seeing you at GoNature.");
						alert.showAndWait();

						// Optional: Send them back to the main menu after success
						try {
							ClientUI.changeScreen("/GUI/LoginRoute.fxml", "Login");
						} catch (Exception e) {
							e.printStackTrace();
						}

					} else {
						// Example of showing an error pop-up!
						javafx.scene.control.Alert alert = new javafx.scene.control.Alert(
								javafx.scene.control.Alert.AlertType.ERROR);
						alert.setTitle("Order Failed");
						alert.setHeaderText(null);
						alert.setContentText(
								"We're sorry, we could not create your order. The park might be at full capacity.");
						alert.showAndWait();
					}
				});
				break;

			case "RETURN_VISITOR_ORDERS":

				@SuppressWarnings("unchecked")
				ArrayList<ArrayList<String>> rawOrders = (ArrayList<ArrayList<String>>) message.getData();

				Platform.runLater(() -> {
					if (rawOrders == null || rawOrders.isEmpty()) {
						System.out.println("No existing orders found. Routing to Creation Screen.");
						ClientUI.changeScreen("/GUI/RegisterVisitor.fxml", "Visitor Registration");
					} else {
						System.out.println("Found " + rawOrders.size() + " orders. Routing to History Screen.");

						// NOTE: Here on the client side, you can loop through 'rawOrders'
						// and build your 'Order' entities to display them in a JavaFX TableView!

						ClientUI.changeScreen("/GUI/OrderHistoryScreen.fxml", "Your Orders");
					}
				});

		      case "EMPLOYEE_ROLE_RESULT":

		            String role = (String) message.getData();

		            if (role == null) {
		                Platform.runLater(() ->
		                    LoginEmployeeController.instance.showError(
		                        "Invalid username or password"
		                    )
		                );
		                return;
		            }

		            System.out.println("Role = " + role);

		            Platform.runLater(() -> {
		                switch (role) {
		                    case "ParkWorker":
		                        ClientUI.changeScreen("/GUI/ParkWorker.fxml", "Park Worker");
		                        break;

		                    case "ServiceRep":
		                        ClientUI.changeScreen("/GUI/ServiceRep.fxml", "Service Rep");
		                        break;

		                    case "ParkManager":
		                        ClientUI.changeScreen("/GUI/ParkManager.fxml", "Park Manager");
		                        break;

		                    case "DeptManager":
		                        ClientUI.changeScreen("/GUI/DeptManager.fxml", "Dept Manager");
		                        break;
		                }
		            });

		            break;

				// You can add more cases here later (e.g., "FETCH_PARKS_RESULT",
				// "LOGIN_EMPLOYEE_RESULT")

			default:
				System.out.println("Client: Received an unknown command from server: " + command);
				break;
			}
		}
	}

	// this method will show us if the connection is interupted
	@Override
	protected void connectionException(Exception exception) {

		Platform.runLater(new Runnable() {

			@Override
			public void run() {

				if (ClientUI.connectionController != null) {
					ClientUI.connectionController.showErrorInGUI("Connection failed");
				}
			}
		});
	}

	@Override
	protected void connectionEstablished() {
		try {
			String pcName = java.net.InetAddress.getLocalHost().getHostName(); // get the host name

			sendToServer(new Message("CONNECT", pcName));

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void disconnectClient() {
		try {
			if (isConnected()) {
				closeConnection();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}