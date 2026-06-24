package Strategy;

import java.util.ArrayList;

import Common.Message;
import GUI.ServiceRepSearchSubscriberController;
import javafx.application.Platform;

public class SubscriberDetailsResultStrategy implements MessageStrategy {

	@Override
	public void execute(Message message) {
		@SuppressWarnings("unchecked")
		ArrayList<String> subscriberInfo = (ArrayList<String>) message.getData();

		Platform.runLater(() -> {
			ServiceRepSearchSubscriberController controller = ServiceRepSearchSubscriberController.getInstance();

			if (subscriberInfo == null || subscriberInfo.isEmpty()) {
				controller.showSubscriberNotFound();
			} else {
				controller.displaySubscriberInfo(subscriberInfo);
			}
		});
	}
}
