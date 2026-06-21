package Strategy;

import Common.Message;
import GUI.CreateOrderController;
import GUI.ParkWorkerCreateOrderController;

public class VisitorTypeResultStrategy implements MessageStrategy {

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
