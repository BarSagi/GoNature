package Strategy;

import Common.Message;
import Common.Order;
import GUI.VisitorOrdersScreenController;
import Client.ClientUI;
import javafx.application.Platform;
import java.util.ArrayList;

public class ReturnVisitorOrdersStrategy implements MessageStrategy {
	@Override
	public void execute(Message message) {
		@SuppressWarnings("unchecked")
		ArrayList<Order> orders = (ArrayList<Order>) message.getData();

		Platform.runLater(() -> {
			if (orders == null || orders.isEmpty()) {
				System.out.println("No existing orders found. Routing to Creation Screen.");
				ClientUI.changeScreen("/GUI/RegisterVisitor.fxml", "Visitor Registration");
			} else {
				System.out.println("Found " + orders.size() + " orders. Routing to Orders Screen.");
				ClientUI.changeScreen("/GUI/VisitorOrdersScreen.fxml", "Your Orders");

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