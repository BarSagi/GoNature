package Strategy;

import Client.ClientUI;
import Client.GoNatureClient;
import Common.Message;
import javafx.application.Platform;

/**
 * Handles the server response when an order update succeeds.
 * <p>
 * This strategy displays a success alert to the user and refreshes the
 * current visitor's orders.
 */
public class UpdateSuccessStrategy implements MessageStrategy {
	
	/**
	 * Executes the strategy for handling a successful order update.
	 * <p>
	 * After showing a success message, a request is sent to the server to fetch
	 * the updated list of orders for the current visitor.
	 *
	 * @param message the message received from the server
	 */
	@Override
	public void execute(Message message) {
		Platform.runLater(() -> {
			javafx.scene.control.Alert alert = new javafx.scene.control.Alert(
					javafx.scene.control.Alert.AlertType.INFORMATION);
			alert.setTitle("Success");
			alert.setHeaderText("Order Updated");
			alert.setContentText("Your order was successfully updated!");
			alert.showAndWait();

			Message msg = new Message("FETCH_VISITOR_ORDERS", GoNatureClient.currentVisitor.getVisitorId());

			try {
				ClientUI.send(msg);
			} catch (Exception e) {
				System.out.println("Error sending message to server");
				e.printStackTrace();
			}
		});
	}
}