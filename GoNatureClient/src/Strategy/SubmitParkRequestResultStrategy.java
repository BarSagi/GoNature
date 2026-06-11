package Strategy;

import Common.Message;
import GUI.ParkManagerSubmitRequestPanelController;
import javafx.application.Platform;

public class SubmitParkRequestResultStrategy implements MessageStrategy {

    @Override
    public void execute(Message message) {
        boolean success = (boolean) message.getData();

        Platform.runLater(() -> {
            if (ParkManagerSubmitRequestPanelController.instance != null) {
                if (success) {
                    ParkManagerSubmitRequestPanelController.instance.showStatus("Request submitted successfully.");
                } else {
                    ParkManagerSubmitRequestPanelController.instance.showStatus("Failed to submit request.");
                }
            }
        });
    }
}
