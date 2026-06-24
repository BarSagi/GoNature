package Strategy;

import Common.Message;
import Common.VisitReportData;
import GUI.ParkManagerVisitReportsPanelController;
import javafx.application.Platform;

/**
 * Handles the server response containing visit report data.
 * <p>
 * This strategy receives the visit report from the server and displays it
 * in the park manager visit reports panel.
 */
public class VisitReportStrategy implements MessageStrategy {

    /**
     * Executes the strategy for displaying the visit report.
     * <p>
     * The message data is expected to contain a {@code VisitReportData}
     * object with the visit report information.
     *
     * @param msg the message received from the server containing the visit report data
     */
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