package Strategy;

import Common.Message;
import Common.VisitReportData;
import OCSFUtils.ConnectionToClient;
import Server.EchoServer;

import java.util.ArrayList;

/**
 * Strategy class responsible for generating a visit report. The strategy
 * receives the park name, month, and year from the client, retrieves the
 * matching visit report from the database, and sends the result back to the
 * client.
 */
public class GetVisitReportStrategy implements MessageStrategy {

	/**
	 * Executes the visit report request. The method extracts the report parameters
	 * from the message, converts the park name to a park ID, retrieves the visit
	 * report from the database, and sends the result back to the client.
	 *
	 * @param message the message received from the client
	 * @param client  the client connection that sent the message
	 * @param server  the server that handles the request and provides database
	 *                access
	 */
	@Override
	public void execute(Message message, ConnectionToClient client, EchoServer server) {
		try {
			@SuppressWarnings("unchecked")
			ArrayList<Object> data = (ArrayList<Object>) message.getData();

			int parkId = server.getDatabase().getParkIdByName((String) data.get(0));

			if (parkId == -1 || data.get(0) == null || data.get(1) == null || data.get(2) == null) {
				client.sendToClient(new Message("VISIT_REPORT_RESULT", null));
				return;
			}

			int month = (Integer) data.get(1);
			int year = (Integer) data.get(2);

			VisitReportData report = server.getDatabase().getVisitReport(parkId, month, year);

			client.sendToClient(new Message("VISIT_REPORT_RESULT", report));

		} catch (Exception e) {
			System.out.println("EXCEPTION IN VISIT REPORT:");
			e.printStackTrace();
			try {
				client.sendToClient(new Message("VISIT_REPORT_RESULT", new VisitReportData(0, 0)));
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}
}