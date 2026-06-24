package Strategy;

import Common.Message;
import Client.ClientUI;
import Client.GoNatureClient;
import javafx.application.Platform;
import javafx.scene.control.Alert;

/**
 * Handles the server response after attempting to confirm an order.
 * <p>
 * This strategy displays a success or failure alert according to the result
 * received from the server. After the alert is closed, it refreshes the
 * current visitor's orders if a visitor is logged in.
 */
public class ConfirmOrderResultStrategy implements MessageStrategy {

	/**
	 * Executes the strategy for handling the order confirmation result.
	 * <p>
	 * The message data is expected to contain a boolean value that indicates
	 * whether the order confirmation was successful.
	 *
	 * @param message the message received from the server containing the confirmation result
	 */
	@Override
	public void execute(Message message) {
		boolean success = (boolean) message.getData();

		Platform.runLater(() -> {
			Alert alert;

			if (success) {
				alert = new Alert(Alert.AlertType.INFORMATION);
				alert.setTitle("Order Confirmed");
				alert.setHeaderText(null);
				alert.setContentText("Your order has been confirmed successfully.");
			} else {
				alert = new Alert(Alert.AlertType.ERROR);
				alert.setTitle("Confirmation Failed");
				alert.setHeaderText(null);
				alert.setContentText("Could not confirm the order. The confirmation time may have expired.");
			}

			alert.showAndWait();

			try {
				if (GoNatureClient.currentVisitor != null) {
					ClientUI.send(new Message("FETCH_VISITOR_ORDERS", GoNatureClient.currentVisitor.getVisitorId()));
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
	}
}