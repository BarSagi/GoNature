package Strategy;

import Common.Message;
import Client.ClientUI;
import Client.GoNatureClient;
import javafx.application.Platform;
import javafx.scene.control.Alert;

/**
 * Handles the server response after attempting to add a visitor to the waiting
 * list.
 * <p>
 * If the operation succeeds, a success message is displayed and the visitor's
 * orders are fetched again. If the current visitor is not available, the user
 * is redirected to the visitor login screen. If the operation fails, an error
 * message is displayed.
 */
public class AddToWaitingListResultStrategy implements MessageStrategy {

	/**
	 * Executes the strategy for handling the waiting list result message.
	 * <p>
	 * The message data is expected to contain a boolean value that indicates
	 * whether the visitor was added to the waiting list successfully.
	 *
	 * @param message the message received from the server containing the operation
	 *                result
	 */
	@Override
	public void execute(Message message) {
		boolean success = (boolean) message.getData();

		Platform.runLater(() -> {
			Alert alert;

			if (success) {
				alert = new Alert(Alert.AlertType.INFORMATION);
				alert.setTitle("Waiting List");
				alert.setHeaderText(null);
				alert.setContentText("You have been added to the waiting list successfully.");

				// Wait for the user to read the message and click OK
				alert.showAndWait();

				if (GoNatureClient.currentVisitor != null) {

					Message msg = new Message("FETCH_VISITOR_ORDERS", GoNatureClient.currentVisitor.getVisitorId());

					try {
						ClientUI.send(msg);
					} catch (Exception e) {
						System.out.println("Error sending message to server");
						e.printStackTrace();
					}

				} else if (GoNatureClient.currentEmployee != null) {

					ClientUI.changeScreen("/GUI/ParkWorker.fxml", "Park Worker");

				} else {

					ClientUI.changeScreen("/GUI/LoginVisitor.fxml", "GoNature - Enter ID");
				}
			} else {
				alert = new Alert(Alert.AlertType.ERROR);
				alert.setTitle("Waiting List Failed");
				alert.setHeaderText(null);
				alert.setContentText("Could not add you to the waiting list.");

				// Show error alert, but do not change screens
				alert.showAndWait();
			}
		});
	}
}