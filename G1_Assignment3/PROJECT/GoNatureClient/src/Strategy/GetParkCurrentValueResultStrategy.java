package Strategy;

import Common.Message;
import GUI.ParkManagerSubmitRequestPanelController;
import javafx.application.Platform;

/**
 * Handles the server response containing the current park value.
 * <p>
 * This strategy receives the current value from the server and updates
 * the park manager submit request panel.
 */
public class GetParkCurrentValueResultStrategy implements MessageStrategy {

    /**
     * Executes the strategy for handling the current park value result.
     * <p>
     * The message data is expected to contain a string value. If the value
     * is valid, it is displayed in the panel. Otherwise, an error status is shown.
     *
     * @param message the message received from the server containing the current value
     */
    @Override
    public void execute(Message message) {
        String value = (String) message.getData();

        Platform.runLater(() -> {
            if (ParkManagerSubmitRequestPanelController.instance != null) {
                if (value != null) {
                    ParkManagerSubmitRequestPanelController.instance.setCurrentValue(value);
                } else {
                    ParkManagerSubmitRequestPanelController.instance.showStatus("Could not load current value.");
                }
            }
        });
    }
}