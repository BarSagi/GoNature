package Strategy;

import Common.Message;
import Common.Visit;
import GUI.DeptManagerVisitDurationReportController;
import javafx.application.Platform;

import java.util.List;

public class VisitDurationReportStrategy implements MessageStrategy {

    @SuppressWarnings("unchecked")
    @Override
    public void execute(Message msg) {

        List<Visit> visits = (List<Visit>) msg.getData();

        System.out.println("Visit duration report received");

        Platform.runLater(() -> {
            if (DeptManagerVisitDurationReportController.instance != null) {
            	DeptManagerVisitDurationReportController.instance.showReport(visits);
            }
        });
    }
}