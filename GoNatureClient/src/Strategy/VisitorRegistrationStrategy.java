package Strategy;

import Common.Message;
import Client.ClientUI;
import javafx.application.Platform;

/**
 * Handles the server response after attempting to register a visitor.
 * <p>
 * This strategy checks whether the visitor registration succeeded and,
 * if successful, redirects the user to the create order screen.
 */
public class VisitorRegistrationStrategy implements MessageStrategy {
	
	/**
	 * Executes the strategy for handling the visitor registration result.
	 * <p>
	 * The message data is expected to contain a boolean value that indicates
	 * whether the visitor registration was successful.
	 *
	 * @param message the message received from the server containing the registration result
	 */
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