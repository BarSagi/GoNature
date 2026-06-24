package Strategy;

import Common.Message;
import GUI.ParkWorkerEnterVisitorController;
import javafx.application.Platform;

/**
 * Handles the server response after attempting to enter a visitor into the park.
 * <p>
 * This strategy receives the entry result from the server and updates the park
 * worker screen with either a success status or an entry denied message.
 */
public class EnterVisitorResultStrategy implements MessageStrategy {

	/**
	 * Executes the strategy for handling the visitor entry result.
	 * <p>
	 * The message data is expected to contain a string that represents the
	 * result status returned from the server.
	 *
	 * @param message the message received from the server containing the entry result
	 */
	@Override
	public void execute(Message message) {
		String resultStatus = (String) message.getData();

		Platform.runLater(() -> {
			if (ParkWorkerEnterVisitorController.instance != null) {
				if (resultStatus.equals("Success") || resultStatus.startsWith("Success_Pay_")
						|| resultStatus.equals("PaymentUpdated") || resultStatus.equals("Payment Successful")) {
					ParkWorkerEnterVisitorController.instance.showStatus(resultStatus);
				} else {
					ParkWorkerEnterVisitorController.instance.showStatus(
							"Entry Denied!\nReason: " + resultStatus + "\n\n" + "Please verify the following:\n"
									+ "• The ID/Order number was entered correctly.\n"
									+ "• The order status is set to 'Approved'.\n"
									+ "• The current time is within 30 minutes of the scheduled visit.");
				}
			}
		});
	}
}