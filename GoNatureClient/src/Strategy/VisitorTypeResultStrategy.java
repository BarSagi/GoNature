package Strategy;

import Common.Message;
import GUI.CreateOrderController;
import GUI.ParkWorkerCreateOrderController;

/**
 * Handles the server response containing the visitor type.
 * <p>
 * This strategy receives the visitor type from the server and passes it
 * to the relevant order creation screen that is currently active.
 */
public class VisitorTypeResultStrategy implements MessageStrategy {

	/**
	 * Executes the strategy for handling the visitor type result.
	 * <p>
	 * The message data is expected to contain a string value representing
	 * the visitor type.
	 *
	 * @param message the message received from the server containing the visitor type
	 */
	@Override
	public void execute(Message message) {
		try {
			String visitorType = (String) message.getData();

			if (CreateOrderController.instance != null) {
				CreateOrderController.instance.handleVisitorTypeResult(visitorType);
			}
			if (ParkWorkerCreateOrderController.instance != null) {
				ParkWorkerCreateOrderController.instance.handleVisitorTypeResult(visitorType);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}