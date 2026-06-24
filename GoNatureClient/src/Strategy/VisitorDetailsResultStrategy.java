package Strategy;

import java.util.ArrayList;

import Common.Message;
import GUI.VisitorMyDetailsPanelController;
import javafx.application.Platform;

public class VisitorDetailsResultStrategy implements MessageStrategy {

	@SuppressWarnings("unchecked")
	@Override
	public void execute(Message message) {
		ArrayList<String> visitorDetails = (ArrayList<String>) message.getData();

		Platform.runLater(() -> {
			if (VisitorMyDetailsPanelController.instance != null) {
				VisitorMyDetailsPanelController.instance.loadVisitorDetails(visitorDetails);
			}
		});
	}
}