package Strategy;

import Common.Message;
import GUI.NewVisitorOrderController;
import javafx.application.Platform;

/**
 * Handles the server response when registration and order creation succeed.
 * <p>
 * This strategy updates the new visitor order screen and marks the order
 * creation process as successful.
 */
public class RegisterOrderSuccessStrategy implements MessageStrategy {

	/**
	 * Executes the strategy for handling a successful registration and order creation result.
	 * <p>
	 * The relevant controller is updated on the JavaFX application thread.
	 *
	 * @param message the message received from the server
	 */
	@Override
	public void execute(Message message) {
		Platform.runLater(() -> {
			if (NewVisitorOrderController.instance != null) {
				NewVisitorOrderController.instance.handleOrderResult(true, null);
			}
		});
	}
}