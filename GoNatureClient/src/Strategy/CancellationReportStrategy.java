package Strategy;

import Common.Message;
import Common.CancellationReportData;
import GUI.DeptManagerCancellationReportPanelController;

import java.util.ArrayList;

public class CancellationReportStrategy implements MessageStrategy {

	@SuppressWarnings("unchecked")
	@Override
	public void execute(Message message) {

		ArrayList<CancellationReportData> result = (ArrayList<CancellationReportData>) message.getData();

		DeptManagerCancellationReportPanelController.instance.showReport(result);
	}
}