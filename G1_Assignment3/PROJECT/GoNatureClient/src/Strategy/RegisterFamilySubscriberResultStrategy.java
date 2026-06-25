package Strategy;

import Common.Message;
import GUI.ServiceRepRegisterSubscriberPanelController;
import javafx.application.Platform;

/**
 * Handles the server response after attempting to register a family subscriber.
 * <p>
 * This strategy receives the registration result from the server and updates
 * the service representative register subscriber screen with a success or failure message.
 */
public class RegisterFamilySubscriberResultStrategy implements MessageStrategy {

    /**
     * Executes the strategy for handling the family subscriber registration result.
     * <p>
     * The message data is expected to contain a boolean value that indicates
     * whether the subscriber registration was successful.
     *
     * @param message the message received from the server containing the registration result
     */
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