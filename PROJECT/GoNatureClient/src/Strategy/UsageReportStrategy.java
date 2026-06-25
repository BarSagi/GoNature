package Strategy;

import Common.Message;
import Common.UsageReportData;
import GUI.ParkManagerUsageReportsPanelController;

import java.util.ArrayList;

/**
 * Handles the server response containing usage report data.
 * <p>
 * This strategy receives the usage report from the server and displays it
 * in the park manager usage reports panel.
 */
public class UsageReportStrategy implements MessageStrategy {

	/**
	 * Executes the strategy for displaying the usage report.
	 * <p>
	 * The message data is expected to contain an {@code ArrayList<UsageReportData>}
	 * with the usage report information.
	 *
	 * @param message the message received from the server containing the usage report data
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void execute(Message message) {
		ArrayList<UsageReportData> report = (ArrayList<UsageReportData>) message.getData();
		ParkManagerUsageReportsPanelController.instance.showReport(report);
	}
}