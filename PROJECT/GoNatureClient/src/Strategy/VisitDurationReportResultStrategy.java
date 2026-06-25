package Strategy;

import Common.Message;
import Common.Visit;
import GUI.DeptManagerVisitDurationReportController;
import javafx.application.Platform;
import java.util.List;

/**
 * Handles the server response containing visit duration report data.
 * <p>
 * This strategy receives a list of visits from the server and displays it
 * in the department manager visit duration report screen.
 */
public class VisitDurationReportResultStrategy implements MessageStrategy {

	/**
	 * Executes the strategy for displaying the visit duration report.
	 * <p>
	 * The message data is expected to contain a {@code List<Visit>}
	 * with the visit records used for the report.
	 *
	 * @param msg the message received from the server containing the visit duration report data
	 */
	@Override
	public void execute(Message msg) {
		@SuppressWarnings("unchecked")
		List<Visit> visits = (List<Visit>) msg.getData();

		Platform.runLater(() -> {
			if (DeptManagerVisitDurationReportController.instance != null) {
				DeptManagerVisitDurationReportController.instance.showReport(visits);
			}
		});
	}
}