package Strategy;

import Common.Message;
import GUI.ParkWorkerCreateOrderController;

/**
 * Handles the server response containing a visitor email.
 * <p>
 * This strategy receives the email from the server and passes it to the
 * park worker create order screen.
 */
public class VisitorEmailResult implements MessageStrategy {

    /**
     * Executes the strategy for handling the visitor email result.
     * <p>
     * The message data is expected to contain a string value representing
     * the visitor's email address.
     *
     * @param message the message received from the server containing the visitor email
     */
    @Override
    public void execute(Message message) {

        String email = (String) message.getData();

        if (ParkWorkerCreateOrderController.instance != null) {
            ParkWorkerCreateOrderController.instance.handleVisitorEmailResult(email);
        }
    }
}