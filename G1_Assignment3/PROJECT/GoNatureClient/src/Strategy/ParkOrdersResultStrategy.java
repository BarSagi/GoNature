package Strategy;

import java.util.ArrayList;

import Common.Message;
import Common.Order;
import GUI.ParkWorkerViewOrdersController;
import javafx.application.Platform;

/**
 * Handles the server response containing park orders.
 * <p>
 * This strategy receives a list of orders from the server and displays it
 * in the park worker view orders screen.
 */
public class ParkOrdersResultStrategy implements MessageStrategy {

	/**
	 * Executes the strategy for handling park orders data.
	 * <p>
	 * The message data is expected to contain an {@code ArrayList<Order>}
	 * with the orders that belong to the selected park.
	 *
	 * @param message the message received from the server containing park orders
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void execute(Message message) {
		ArrayList<Order> orders = (ArrayList<Order>) message.getData();

		Platform.runLater(() -> {
			if (ParkWorkerViewOrdersController.instance != null) {
				ParkWorkerViewOrdersController.instance.showOrders(orders);
			} else {
				System.err.println("CRITICAL ERROR: No matching controller instance found!");
			}
		});
	}
}