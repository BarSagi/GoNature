package Strategy;

import Common.Message;
import Common.Order;
import Entity.Visitor;
import GUI_Visitor.VisitorOrdersScreenController;
import Client.ClientUI;
import Client.GoNatureClient;
import javafx.application.Platform;
import java.util.ArrayList;

public class ReturnVisitorOrdersAndDataStrategy implements MessageStrategy {
	@SuppressWarnings("unchecked")
	@Override
	public void execute(Message message) {
		ArrayList<Object> combinedData = (ArrayList<Object>) message.getData();

		// Unpack Index 0 (The Visitor)
		ArrayList<String> visitor = (ArrayList<String>) combinedData.get(0);

		// Unpack Index 1 (The Orders)
		ArrayList<Order> orders = (ArrayList<Order>) combinedData.get(1);

		Platform.runLater(() -> {
			if (orders == null || orders.isEmpty()) {
				System.out.println("No existing orders found. Routing to Creation Screen.");
				ClientUI.changeScreen("/GUI_Visitor/NewVisitorOrder.fxml", "Visitor Registration");
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

				ClientUI.changeScreen("/GUI_Visitor/VisitorOrdersScreen.fxml", "Your Orders");

				Platform.runLater(() -> {
					if (VisitorOrdersScreenController.instance != null) {
						VisitorOrdersScreenController.instance.loadOrders(orders);
					} else {
						System.err.println("CRITICAL ERROR: Controller instance is still null!");
					}
				});
			}
		});
	}
}