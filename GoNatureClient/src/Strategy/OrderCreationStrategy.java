package Strategy;

import Common.Message;
import Client.ClientUI;
import Client.GoNatureClient;
import javafx.application.Platform;
import javafx.scene.control.Alert;

public class OrderCreationStrategy implements MessageStrategy {
	@Override
	public void execute(Message message) {
		boolean isOrderCreated = (boolean) message.getData();
		Platform.runLater(() -> {
			if (isOrderCreated) {
				Alert alert = new Alert(Alert.AlertType.INFORMATION);
				alert.setTitle("Order Successful");
				alert.setHeaderText(null);
				alert.setContentText(
						"Your order has been successfully created! We look forward to seeing you at GoNature.");
				alert.showAndWait();

				try {
					if (GoNatureClient.currentVisitor != null) {
						Message msg = new Message("FETCH_VISITOR_ORDERS", GoNatureClient.currentVisitor.getVisitorId());

						try {
							ClientUI.send(msg);
						} catch (Exception e) {
							System.out.println("Error sending message to server");
							e.printStackTrace();
						}
					} else
						ClientUI.changeScreen("/GUI/LoginVisitor.fxml", "GoNature - Visitor Login");
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else {
				Alert alert = new Alert(Alert.AlertType.ERROR);
				alert.setTitle("Order Failed");
				alert.setHeaderText(null);
				alert.setContentText(
						"We're sorry, we could not create your order. The park might be at full capacity.");
				alert.showAndWait();
			}
		});
	}
}