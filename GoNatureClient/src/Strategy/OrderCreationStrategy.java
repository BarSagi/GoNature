package Strategy;

import Client.ClientUI;
import Client.GoNatureClient;
import Common.Message;
import GUI.CreateOrderController;
import GUI.ParkWorkerCreateOrderController;
import javafx.application.Platform;
import javafx.scene.control.Alert;

/**
 * Handles the server response after attempting to create an order.
 * <p>
 * This strategy updates the relevant order screen, displays a success or
 * failure alert, and refreshes the current visitor's orders if a visitor is logged in.
 */
public class OrderCreationStrategy implements MessageStrategy {

	/**
	 * Executes the strategy for handling the order creation result.
	 * <p>
	 * The message data is expected to contain a boolean value that indicates
	 * whether the order was created successfully.
	 *
	 * @param message the message received from the server containing the order creation result
	 */
	@Override
	public void execute(Message message) {

		Boolean success = (Boolean) message.getData();

		if (ParkWorkerCreateOrderController.instance != null) {
			ParkWorkerCreateOrderController.instance.handleOrderResult(success, success ? null : "Unknown error");
		}

		if (CreateOrderController.instance != null) {
			CreateOrderController.instance.handleOrderResult(success, success ? null : "Unknown error");
		}

		Platform.runLater(() -> {
			Alert alert = new Alert(success ? Alert.AlertType.INFORMATION : Alert.AlertType.ERROR);
			alert.setTitle(success ? "Order Approved" : "Order Failed");
			alert.setHeaderText(null);
			alert.setContentText(success ? "Your order has been approved successfully."
					: "We could not create your order.\nIt is possible that the park has reached its maximum capacity for the requested date and time.");
			alert.getDialogPane().setMinHeight(javafx.scene.layout.Region.USE_PREF_SIZE);
			alert.showAndWait();
		});

		if (GoNatureClient.currentVisitor != null) {
			Message msg = new Message("FETCH_VISITOR_ORDERS", GoNatureClient.currentVisitor.getVisitorId());
			try {
				ClientUI.send(msg);
			} catch (Exception e) {
				System.out.println("Error sending message to server");
				e.printStackTrace();
			}
		}
	}
}