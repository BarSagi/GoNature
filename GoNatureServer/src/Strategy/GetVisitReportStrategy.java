package Strategy;

import Common.Message;
import Common.Order;
import OCSFUtils.ConnectionToClient;
import Server.EchoServer;

import java.util.ArrayList;

public class GetVisitReportStrategy implements MessageStrategy {

	@Override
	public void execute(Message message, ConnectionToClient client, EchoServer server) {

		try {
			@SuppressWarnings("unchecked")
			ArrayList<String> data = (ArrayList<String>) message.getData();

			String parkId = data.get(0);
			String month = data.get(1);
			String year = data.get(2);

			ArrayList<Order> report = server.getReportService().getVisitReport(parkId, month, year);

			client.sendToClient(new Message("VISIT_REPORT_RESULT", report));

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}