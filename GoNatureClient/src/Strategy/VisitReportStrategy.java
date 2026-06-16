package Strategy;

import Common.Message;
import Common.VisitReportData;
import GUI.ParkManagerVisitReportsPanelController;
import javafx.application.Platform;

public class VisitReportStrategy implements MessageStrategy {

    @Override
    public void execute(Message msg) {

        VisitReportData report = (VisitReportData) msg.getData();

        Platform.runLater(() -> {
            if (ParkManagerVisitReportsPanelController.instance != null) {
                ParkManagerVisitReportsPanelController.instance.showReport(report);
            }
        });
    }
}