package Strategy;

import Common.Message;
import Common.CancellationReportData;
import GUI.DeptManagerCancellationReportPanelController;

import java.util.ArrayList;

/**
 * Handles the server response containing cancellation report data.
 * <p>
 * This strategy receives the cancellation report result from the server
 * and displays it in the department manager cancellation report panel.
 */
public class CancellationReportStrategy implements MessageStrategy {

	/**
	 * Executes the strategy for displaying the cancellation report.
	 * <p>
	 * The message data is expected to contain an {@code ArrayList}
	 * of {@code CancellationReportData} objects.
	 *
	 * @param message the message received from the server containing the cancellation report data
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void execute(Message message) {

		ArrayList<CancellationReportData> result = (ArrayList<CancellationReportData>) message.getData();

		DeptManagerCancellationReportPanelController.instance.showReport(result);
	}
}