package Strategy;

import Common.Message;
import Client.ClientUI;
import javafx.application.Platform;

public class VisitorRegistrationStrategy implements MessageStrategy {
	@Override
	public void execute(Message message) {
		boolean isRegistered = (boolean) message.getData();
		if (isRegistered) {
			System.out.println("Client: Registration Successful!");
			Platform.runLater(() -> {
				try {
					ClientUI.changeScreen("/GUI/CreateOrder.fxml", "Create Order");
				} catch (Exception e) {
					e.printStackTrace();
				}
			});
		} else {
			System.out.println("Client: Registration Failed.");
		}
	}
}