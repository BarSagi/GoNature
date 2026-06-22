package Strategy;

import Client.ClientUI;
import Client.GoNatureClient;
import Common.Message;
import GUI.CreateOrderController;
import GUI.ParkWorkerCreateOrderController;
import javafx.application.Platform;
import javafx.scene.control.Alert;

public class OrderCreationStrategy implements MessageStrategy {

	@Override
	public void execute(Message message) {

		Boolean success = (Boolean) message.getData();

		if (ParkWorkerCreateOrderController.instance != null) {
			ParkWorkerCreateOrderController.instance.handleOrderResult(success, success ? null : "Unknown error");
		}

		if (CreateOrderController.instance != null) {
			CreateOrderController.instance.handleOrderResult(success, success ? null : "Unknown error");
		}

		Platform.runLater(() -> {
			Alert alert = new Alert(success ? Alert.AlertType.INFORMATION : Alert.AlertType.ERROR);
			alert.setTitle(success ? "Order Approved" : "Order Failed");
			alert.setHeaderText(null);
			alert.setContentText(
					success ? "Your order has been approved successfully." : "We could not create your order.");
			alert.showAndWait();
		});

		if (GoNatureClient.currentVisitor != null) {
			Message msg = new Message("FETCH_VISITOR_ORDERS", GoNatureClient.currentVisitor.getVisitorId());
			try {
				ClientUI.send(msg);
			} catch (Exception e) {
				System.out.println("Error sending message to server");
				e.printStackTrace();
			}
		}
	}
}