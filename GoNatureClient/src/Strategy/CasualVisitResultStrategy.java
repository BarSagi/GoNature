package Strategy;

import Common.Message;
import GUI.ParkWorkerCreateCasualVisitController;
import javafx.application.Platform;

/**
 * Handles the server response after creating a casual visit.
 * <p>
 * This strategy receives the registration result from the server and updates
 * the casual visit screen according to whether the registration succeeded or failed.
 */
public class CasualVisitResultStrategy implements MessageStrategy {

	/**
	 * Executes the strategy for handling the casual visit registration result.
	 * <p>
	 * The message data is expected to contain a boolean value that indicates
	 * whether the casual visit registration succeeded.
	 *
	 * @param message the message received from the server containing the registration result
	 */
	@Override
	public void execute(Message message) {
		// The server sends a boolean indicating if the registration succeeded
		boolean isSuccess = (boolean) message.getData();

		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				if (ParkWorkerCreateCasualVisitController.instance != null) {
					if (isSuccess) {
						ParkWorkerCreateCasualVisitController.instance.handleRegistrationResult(true, null);
					} else {
						ParkWorkerCreateCasualVisitController.instance.handleRegistrationResult(false,
								"Registration failed.\nThe park is currently at full capacity.");
					}
				}
			}
		});
	}
}