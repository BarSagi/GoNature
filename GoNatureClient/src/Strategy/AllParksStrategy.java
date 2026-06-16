package Strategy;

import java.util.ArrayList;

import Common.Message;
import GUI.DeptManagerCancellationReportPanelController;
import GUI.DeptManagerVisitDurationReportController;
import javafx.application.Platform;

public class AllParksStrategy implements MessageStrategy {

    @SuppressWarnings("unchecked")
    @Override
    public void execute(Message msg) {

        ArrayList<String> parks = (ArrayList<String>) msg.getData();

        Platform.runLater(() -> {

            if (DeptManagerVisitDurationReportController.instance != null) {
                DeptManagerVisitDurationReportController.instance.loadParks(parks);
            }

            if (DeptManagerCancellationReportPanelController.instance != null) {
                DeptManagerCancellationReportPanelController.instance.loadParks(parks);
            }
        });
    }
}