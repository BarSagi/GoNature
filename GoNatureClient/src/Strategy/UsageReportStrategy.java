package Strategy;

import Common.Message;
import Common.UsageReportData;
import GUI.ParkManagerUsageReportsPanelController;

import java.util.ArrayList;

public class UsageReportStrategy implements MessageStrategy {

    @SuppressWarnings("unchecked")
    @Override
    public void execute(Message message) {

        ArrayList<UsageReportData> report =
                (ArrayList<UsageReportData>) message.getData();

        ParkManagerUsageReportsPanelController.instance.showReport(report);
    }
}