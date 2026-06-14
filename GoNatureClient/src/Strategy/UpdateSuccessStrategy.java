package Strategy;

import Client.ClientUI;
import Client.GoNatureClient;
import Common.Message;

public class UpdateSuccessStrategy implements MessageStrategy {
	@Override
	public void execute(Message message) {
		javafx.application.Platform.runLater(() -> {
			javafx.scene.control.Alert alert = new javafx.scene.control.Alert(
					javafx.scene.control.Alert.AlertType.INFORMATION);
			alert.setTitle("Success");
			alert.setHeaderText("Order Updated");
			alert.setContentText("Your order was successfully updated!");
			alert.showAndWait();

			Message msg = new Message("FETCH_VISITOR_ORDERS", GoNatureClient.currentVisitor.getVisitorId());

			try {
				ClientUI.send(msg);
			} catch (Exception e) {
				System.out.println("Error sending message to server");
				e.printStackTrace();
			}
		});
	}
}
