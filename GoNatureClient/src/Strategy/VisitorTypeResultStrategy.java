package Strategy;

import Common.Message;
import GUI.CreateOrderController;

public class VisitorTypeResultStrategy implements MessageStrategy {

    @Override
    public void execute(Message message) {

        String visitorType = (String) message.getData();

        CreateOrderController.handleVisitorTypeResult(visitorType);
    }
}
