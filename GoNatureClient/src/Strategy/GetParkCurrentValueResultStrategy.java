package Strategy;

import Common.Message;
import GUI.ParkManagerSubmitRequestPanelController;
import javafx.application.Platform;

public class GetParkCurrentValueResultStrategy implements MessageStrategy {

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
