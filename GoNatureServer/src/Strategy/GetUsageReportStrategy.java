package Strategy;

import Common.Message;
import Common.UsageReportData;
import OCSFUtils.ConnectionToClient;
import Server.EchoServer;

import java.util.ArrayList;

/**
 * Strategy class responsible for generating a usage report.
 * The strategy receives the park name, month, and year from the client,
 * retrieves the matching usage report from the database,
 * and sends the result back to the client.
 */
public class GetUsageReportStrategy implements MessageStrategy {

	/**
	 * Executes the usage report request.
	 * The method extracts the report parameters from the message,
	 * retrieves the usage report from the database,
	 * and sends the report data back to the client.
	 *
	 * @param message the message received from the client
	 * @param client the client connection that sent the message
	 * @param server the server that handles the request and provides database access
	 */
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