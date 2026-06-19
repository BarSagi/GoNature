package Strategy;

import Common.Message;
import GUI.ParkWorkerCreateOrderController;
import javafx.application.Platform;
import javafx.scene.control.Alert;

public class OrderCreationStrategy implements MessageStrategy {

	@Override
	public void execute(Message message) {

		Boolean success = (Boolean) message.getData();

		Platform.runLater(() -> {

			if (ParkWorkerCreateOrderController.instance != null) {
				ParkWorkerCreateOrderController.instance.handleOrderResult(success, success ? null : "Unknown error");
			}

			Alert alert = new Alert(success ? Alert.AlertType.INFORMATION : Alert.AlertType.ERROR);

			alert.setTitle(success ? "Order Approved" : "Order Failed");
			alert.setHeaderText(null);

			alert.setContentText(
					success ? "Your order has been approved successfully." : "We could not create your order.");

			alert.showAndWait();
		});
	}
}