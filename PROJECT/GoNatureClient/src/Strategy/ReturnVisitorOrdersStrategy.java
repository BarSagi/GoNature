package Strategy;

import java.util.ArrayList;

import Client.ClientUI;
import Common.Message;
import Common.Order;
import GUI.VisitorOrdersScreenController;
import javafx.application.Platform;

/**
 * Handles the server response containing the visitor's orders.
 * <p>
 * This strategy opens the visitor orders screen and loads the received orders
 * into the relevant controller.
 */
public class ReturnVisitorOrdersStrategy implements MessageStrategy {

	/**
	 * Executes the strategy for handling visitor orders.
	 * <p>
	 * The message data is expected to contain an {@code ArrayList<Order>} with the
	 * visitor's orders.
	 *
	 * @param message the message received from the server containing visitor orders
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void execute(Message message) {
		ArrayList<Order> orders = (ArrayList<Order>) message.getData();
		if (orders == null || orders.isEmpty()) {
			ClientUI.changeScreen("/GUI/LoginVisitor.fxml", "GoNature - Enter ID");
		}

		else {
			ClientUI.changeScreen("/GUI/VisitorOrdersScreen.fxml", "Your Orders");

			Platform.runLater(() -> {
				if (VisitorOrdersScreenController.instance != null) {
					VisitorOrdersScreenController.instance.loadOrders(orders);
				} else {
					System.err.println("CRITICAL ERROR: Controller instance is still null!");
				}
			});
		}
	}
}