package Strategy;

import Common.Message;
import GUI.NewVisitorOrderController;
import javafx.application.Platform;

public class RegisterOrderSuccessStrategy implements MessageStrategy {

	@Override
	public void execute(Message message) {
		Platform.runLater(() -> {
			if (NewVisitorOrderController.instance != null) {
				NewVisitorOrderController.instance.handleOrderResult(true, null);
			}
		});
	}
}