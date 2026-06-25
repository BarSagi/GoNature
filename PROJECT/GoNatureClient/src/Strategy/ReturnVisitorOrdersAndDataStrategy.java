package Strategy;

import Common.Message;
import Common.Order;
import Entity.Visitor;
import GUI.VisitorOrdersScreenController;
import Client.ClientUI;
import Client.GoNatureClient;
import javafx.application.Platform;
import java.util.ArrayList;

/**
 * Handles the server response containing visitor data and the visitor's orders.
 * <p>
 * This strategy receives combined data from the server, extracts the visitor
 * information and order list, saves the current visitor, and opens the relevant
 * visitor screen.
 */
public class ReturnVisitorOrdersAndDataStrategy implements MessageStrategy {

	/**
	 * Executes the strategy for handling visitor data and order results.
	 * <p>
	 * The message data is expected to contain an {@code ArrayList<Object>} where
	 * the first element is the visitor data and the second element is the list of
	 * orders.
	 *
	 * @param message the message received from the server containing visitor data
	 *                and orders
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void execute(Message message) {
		ArrayList<Object> combinedData = (ArrayList<Object>) message.getData();

		// Unpack Index 0 (The Visitor)
		ArrayList<String> visitor = (ArrayList<String>) combinedData.get(0);

		// Unpack Index 1 (The Orders)
		ArrayList<Order> orders = (ArrayList<Order>) combinedData.get(1);

		Platform.runLater(() -> {
			if ((orders == null || orders.isEmpty()) && visitor == null) {
				ClientUI.changeScreen("/GUI/NewVisitorOrder.fxml", "Visitor Registration");
			} else {
				System.out.println("Found " + orders.size() + " orders. Routing to Orders Screen.");

				// Extract the visitor data based on the indices from fetchVisitor
				String id = visitor.get(0);
				String firstName = visitor.get(1);
				String lastName = visitor.get(2);
				String phone = visitor.get(3);
				String email = visitor.get(4);
				String visitorType = visitor.get(5);
				int subscriptionNumber = Integer.parseInt(visitor.get(6));
				int familyMembers = Integer.parseInt(visitor.get(7));

				// Instantiate the Visitor entity and save it globally
				GoNatureClient.currentVisitor = new Visitor(id, firstName, lastName, phone, email, visitorType,
						subscriptionNumber, familyMembers);

				if ((orders == null || orders.isEmpty()) && visitor != null) {// Guide or Subscriber first time
					ClientUI.changeScreen("/GUI/CreateOrder.fxml", "Create first order to continue!");
				} else {
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
		});
	}
}