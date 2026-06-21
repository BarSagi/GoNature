package Strategy;

import Client.ClientUI;
import Client.GoNatureClient;
import Common.Message;
import javafx.application.Platform;
import javafx.scene.control.Alert;

public class OrderCancelResultStrategy implements MessageStrategy {

	@Override
	public void execute(Message message) {
		boolean success = (boolean) message.getData();

		Platform.runLater(() -> {

			if (success) {
				// 1. Show the success pop-up
				Alert alert = new Alert(Alert.AlertType.INFORMATION);
				alert.setTitle("Cancellation Successful");
				alert.setHeaderText("Order Cancelled");
				alert.setContentText("Your order has been successfully cancelled.");
				alert.showAndWait();

				if (GoNatureClient.currentVisitor != null) {
					try {
						ClientUI.send(new Message("FETCH_VISITOR_ORDERS", GoNatureClient.currentVisitor.getVisitorId()));
					} catch (Exception e) {
						System.out.println("Error requesting updated orders list.");
						e.printStackTrace();
					}
				}

			} else {
				Alert alert = new Alert(Alert.AlertType.ERROR);
				alert.setTitle("Error");
				alert.setHeaderText("Cancellation Failed");

				alert.setContentText("We could not cancel your order at this time. Please try again.");

				alert.showAndWait();
			}
		});
	}
}