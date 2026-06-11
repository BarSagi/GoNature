package Strategy;

import Common.Message;
import GUI.ServiceRepRegisterGuidePanelController;
import javafx.application.Platform;

public class RegisterGroupGuideResultStrategy implements MessageStrategy {

    @Override
    public void execute(Message message) {
        boolean success = (boolean) message.getData();

        Platform.runLater(() -> {
            if (ServiceRepRegisterGuidePanelController.instance != null) {
                if (success) {
                    ServiceRepRegisterGuidePanelController.instance.showStatus("Guide registered successfully.");
                } else {
                    ServiceRepRegisterGuidePanelController.instance.showStatus("Failed to register guide.");
                }
            }
        });
    }
}
