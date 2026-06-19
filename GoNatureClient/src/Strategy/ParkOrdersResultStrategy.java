package Strategy;

import java.util.ArrayList;

import Common.Message;
import Common.Order;
import GUI.ParkWorkerViewOrdersController;
import javafx.application.Platform;

public class ParkOrdersResultStrategy implements MessageStrategy {

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