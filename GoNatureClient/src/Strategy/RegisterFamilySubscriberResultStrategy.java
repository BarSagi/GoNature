package Strategy;

import Common.Message;
import GUI.ServiceRepRegisterSubscriberPanelController;
import javafx.application.Platform;

public class RegisterFamilySubscriberResultStrategy implements MessageStrategy {

    @Override
    public void execute(Message message) {
        boolean success = (boolean) message.getData();

        Platform.runLater(() -> {
            if (ServiceRepRegisterSubscriberPanelController.instance != null) {
                if (success) {
                    ServiceRepRegisterSubscriberPanelController.instance.showStatus("Subscriber registered successfully.");
                } else {
                    ServiceRepRegisterSubscriberPanelController.instance.showStatus("Failed to register subscriber.");
                }
            }
        });
    }
}
