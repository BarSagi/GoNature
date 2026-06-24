package Strategy;

import Common.Message;
import GUI_Visitor.VisitorMyDetailsPanelController;
import javafx.application.Platform;

public class UpdateVisitorDetailsResultStrategy implements MessageStrategy {

	@Override
	public void execute(Message message) {
		boolean success = (boolean) message.getData();

		Platform.runLater(() -> {
			if (VisitorMyDetailsPanelController.instance != null) {
				VisitorMyDetailsPanelController.instance.handleUpdateResult(success);
			}
		});
	}
}