package Strategy;

import Common.Message;
import Common.UsageReportData;
import OCSFUtils.ConnectionToClient;
import Server.EchoServer;

import java.util.ArrayList;

public class GetUsageReportStrategy implements MessageStrategy {

	@Override
	public void execute(Message message, ConnectionToClient client, EchoServer server) {

		@SuppressWarnings("unchecked")
		ArrayList<Object> data = (ArrayList<Object>) message.getData();

		String parkName = (String) data.get(0);
		int month = (int) data.get(1);
		int year = (int) data.get(2);

		ArrayList<UsageReportData> result = server.getDatabase().getUsageReport(parkName, month, year);

		try {
			Message response = new Message("USAGE_REPORT_RESULT", result);
			client.sendToClient(response);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}