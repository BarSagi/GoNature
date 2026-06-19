package Strategy;

import GUI.ParkWorkerCreateCasualVisitController;
import Common.Message;

public class PriceResultCasualStrategy implements MessageStrategy {

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