package Strategy;

import Common.Message;
import GUI.CreateOrderController;
import GUI.NewVisitorOrderController;
import GUI.ParkWorkerCreateOrderController;

/**
 * Handles the server response containing the calculated price for a preorder.
 * <p>
 * This strategy receives the price from the server and passes it to the
 * relevant order creation screen that is currently active.
 */
public class PriceResultPreorderStrategy implements MessageStrategy {

    /**
     * Executes the strategy for handling the preorder price result.
     * <p>
     * The message data is expected to contain a {@code Double} value
     * representing the calculated price.
     *
     * @param msg the message received from the server containing the price result
     */
    @Override
    public void execute(Message msg) {

        try {
            Double price = (Double) msg.getData();

            if (ParkWorkerCreateOrderController.instance != null) {
                ParkWorkerCreateOrderController.instance.handlePriceResult(price);
            }
            if (CreateOrderController.instance != null) {
            	CreateOrderController.instance.handlePriceResult(price);
            }
            if (NewVisitorOrderController.instance != null) {
            	NewVisitorOrderController.instance.handlePriceResult(price);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}