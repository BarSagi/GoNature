package Strategy;

import Common.Message;
import GUI.ParkManagerSubmitRequestPanelController;
import javafx.application.Platform;

/**
 * Handles the server response after a park manager submits a park change request.
 * <p>
 * This strategy receives the request submission result from the server and updates
 * the park manager submit request panel with a success or failure message.
 */
public class SubmitParkRequestResultStrategy implements MessageStrategy {

    /**
     * Executes the strategy for handling the park request submission result.
     * <p>
     * The message data is expected to contain a boolean value that indicates
     * whether the request was submitted successfully.
     *
     * @param message the message received from the server containing the submission result
     */
    @Override
    public void execute(Message message) {
        boolean success = (boolean) message.getData();

        Platform.runLater(() -> {
            if (ParkManagerSubmitRequestPanelController.instance != null) {
                if (success) {
                    ParkManagerSubmitRequestPanelController.instance.showStatus("Request submitted successfully.");
                    ParkManagerSubmitRequestPanelController.instance.clearFields();
                } else {
                    ParkManagerSubmitRequestPanelController.instance.showStatus("Failed to submit request.");
                    
                }
            }
        });
    }
}