package Strategy;

import Common.Message;
import Common.ReportImage;
import GUI.DeptManagerSavedReportsPanelController;

import java.util.List;

public class GetAllReportsResultStrategy implements MessageStrategy {

    @Override
    public void execute(Message message) {

        @SuppressWarnings("unchecked")
		List<ReportImage> reports = (List<ReportImage>) message.getData();

        if (DeptManagerSavedReportsPanelController.instance != null) {
            DeptManagerSavedReportsPanelController.instance.setReports(reports);
        }
    }
}