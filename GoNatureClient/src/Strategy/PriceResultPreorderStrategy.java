package Strategy;

import Common.Message;
import GUI.CreateOrderController;
import GUI.NewVisitorOrderController;
import GUI.ParkWorkerCreateOrderController;

public class PriceResultPreorderStrategy implements MessageStrategy {

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