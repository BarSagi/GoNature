package Strategy;

import GUI.ParkWorkerCreateCasualVisitController;
import Common.Message;

/**
 * Handles the server response containing the calculated price for a casual visit.
 * <p>
 * This strategy receives the price from the server and passes it to the
 * park worker casual visit screen.
 */
public class PriceResultCasualStrategy implements MessageStrategy {

    /**
     * Executes the strategy for handling the casual visit price result.
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

            if (ParkWorkerCreateCasualVisitController.instance != null) {
                ParkWorkerCreateCasualVisitController.instance.handlePriceResult(price);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}