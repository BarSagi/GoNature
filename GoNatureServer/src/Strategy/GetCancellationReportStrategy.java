package Strategy;

import Common.CancellationReportData;
import Common.Message;
import Server.EchoServer;
import OCSFUtils.ConnectionToClient;

import java.util.ArrayList;

public class GetCancellationReportStrategy implements MessageStrategy {

	@Override
	public void execute(Message message, ConnectionToClient client, EchoServer server) {

		@SuppressWarnings("unchecked")
		ArrayList<Object> data = (ArrayList<Object>) message.getData();

		String parkName = (String) data.get(0);
		int month = (int) data.get(1);
		int year = (int) data.get(2);

		int parkId = server.getDatabase().getParkIdByName(parkName);

		ArrayList<CancellationReportData> result = server.getDatabase().getCancellationReport(parkId, month, year);
		
		try {
			client.sendToClient(new Message("CANCELLATION_REPORT_RESULT", result));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}