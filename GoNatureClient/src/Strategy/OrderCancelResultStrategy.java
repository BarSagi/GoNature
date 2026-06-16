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
			Alert alert;

			if (success) {
				alert = new Alert(Alert.AlertType.INFORMATION);
				alert.setTitle("Order Canceled");
				alert.setHeaderText(null);
				alert.setContentText("The order was canceled successfully.");

				alert.showAndWait();

				try {
					if (GoNatureClient.currentVisitor != null) {
						Message refreshMsg = new Message("FETCH_VISITOR_ORDERS",
								GoNatureClient.currentVisitor.getVisitorId());
						ClientUI.send(refreshMsg);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}

			} else {
				alert = new Alert(Alert.AlertType.ERROR);
				alert.setTitle("Cancel Failed");
				alert.setHeaderText(null);
				alert.setContentText("Failed to cancel the order.");
				alert.showAndWait();
			}
		});
	}
}