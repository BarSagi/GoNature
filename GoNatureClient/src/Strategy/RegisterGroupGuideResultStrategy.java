package Strategy;

import Common.Message;
import GUI.ServiceRepRegisterGuidePanelController;
import javafx.application.Platform;

/**
 * Handles the server response after attempting to register a group guide.
 * <p>
 * This strategy receives the registration result from the server and updates
 * the service representative register guide screen with a success or failure message.
 */
public class RegisterGroupGuideResultStrategy implements MessageStrategy {

    /**
     * Executes the strategy for handling the group guide registration result.
     * <p>
     * The message data is expected to contain a boolean value that indicates
     * whether the guide registration was successful.
     *
     * @param message the message received from the server containing the registration result
     */
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