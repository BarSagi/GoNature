package Strategy;

import Common.Message;
import GUI.ParkWorkerCreateOrderController;

public class VisitorEmailResult implements MessageStrategy {

    @Override
    public void execute(Message message) {

        String email = (String) message.getData();

        if (ParkWorkerCreateOrderController.instance != null) {
            ParkWorkerCreateOrderController.instance.handleVisitorEmailResult(email);
        }
    }
}