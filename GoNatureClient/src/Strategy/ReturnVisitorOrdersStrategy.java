package Strategy;

import java.util.ArrayList;

import Client.ClientUI;
import Common.Message;
import Common.Order;
import GUI_Visitor.VisitorOrdersScreenController;
import javafx.application.Platform;

public class ReturnVisitorOrdersStrategy implements MessageStrategy {

	@SuppressWarnings("unchecked")
	@Override
	public void execute(Message message) {
		ArrayList<Order> orders = (ArrayList<Order>) message.getData();
		ClientUI.changeScreen("/GUI_Visitor/VisitorOrdersScreen.fxml", "Your Orders");

		Platform.runLater(() -> {
			if (VisitorOrdersScreenController.instance != null) {
				VisitorOrdersScreenController.instance.loadOrders(orders);
			} else {
				System.err.println("CRITICAL ERROR: Controller instance is still null!");
			}
		});
	}
}
