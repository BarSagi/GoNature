package Strategy;

import Common.Message;
import Common.Visit;
import GUI.DeptManagerVisitDurationReportController;
import javafx.application.Platform;
import java.util.List;

public class VisitDurationReportResultStrategy implements MessageStrategy {

    @Override
    public void execute(Message msg) {
        @SuppressWarnings("unchecked")
        List<Visit> visits = (List<Visit>) msg.getData();

        System.out.println("Visit duration report received successfully. Records: " + visits.size());

        Platform.runLater(() -> {
            if (DeptManagerVisitDurationReportController.instance != null) {
                DeptManagerVisitDurationReportController.instance.showReport(visits);
            } else {
                System.out.println("Error: DeptManagerVisitDurationReportController.instance is null!");
            }
        });
    }
}