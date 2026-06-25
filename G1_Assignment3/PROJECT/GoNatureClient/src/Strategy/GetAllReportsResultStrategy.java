package Strategy;

import Common.Message;
import Common.ReportImage;
import GUI.DeptManagerSavedReportsPanelController;

import java.util.List;

/**
 * Handles the server response containing all saved reports.
 * <p>
 * This strategy receives a list of saved report images from the server
 * and loads them into the department manager saved reports panel.
 */
public class GetAllReportsResultStrategy implements MessageStrategy {

	/**
	 * Executes the strategy for displaying all saved reports.
	 * <p>
	 * The message data is expected to contain a {@code List<ReportImage>}
	 * with all reports received from the server.
	 *
	 * @param message the message received from the server containing the saved reports
	 */
	@Override
	public void execute(Message message) {

		@SuppressWarnings("unchecked")
		List<ReportImage> reports = (List<ReportImage>) message.getData();

		if (DeptManagerSavedReportsPanelController.instance != null) {
			DeptManagerSavedReportsPanelController.instance.setReports(reports);
		}
	}
}