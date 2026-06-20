package Strategy;

import Common.Message;
import Client.ClientUI;
import Client.GoNatureClient;
import javafx.application.Platform;
import javafx.scene.control.Alert;

public class ConfirmOrderResultStrategy implements MessageStrategy {

	@Override
	public void execute(Message message) {
		boolean success = (boolean) message.getData();

		Platform.runLater(() -> {
			Alert alert;

			if (success) {
				alert = new Alert(Alert.AlertType.INFORMATION);
				alert.setTitle("Order Confirmed");
				alert.setHeaderText(null);
				alert.setContentText("Your order has been confirmed successfully.");
			} else {
				alert = new Alert(Alert.AlertType.ERROR);
				alert.setTitle("Confirmation Failed");
				alert.setHeaderText(null);
				alert.setContentText("Could not confirm the order. The confirmation time may have expired.");
			}

			alert.showAndWait();

			try {
				if (GoNatureClient.currentVisitor != null) {
					ClientUI.send(new Message("FETCH_VISITOR_ORDERS", GoNatureClient.currentVisitor.getVisitorId()));
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
	}
}