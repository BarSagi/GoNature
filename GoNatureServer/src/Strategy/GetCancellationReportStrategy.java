package Strategy;

import Common.CancellationReportData;
import Common.Message;
import Server.EchoServer;
import OCSFUtils.ConnectionToClient;

import java.util.ArrayList;

/**
 * Strategy class responsible for generating a cancellation report.
 * The strategy receives the park name, month, and year from the client,
 * updates no-show orders to canceled, retrieves the matching cancellation
 * report from the database, and sends the result back to the client.
 */
public class GetCancellationReportStrategy implements MessageStrategy {

	/**
	 * Executes the cancellation report request.
	 * The method extracts the report parameters from the message, updates
	 * approved orders that did not take place to canceled, converts the park
	 * name to a park ID, retrieves the cancellation report from the database,
	 * and sends the result back to the client.
	 *
	 * @param message the message received from the client
	 * @param client  the client connection that sent the message
	 * @param server  the server that handles the request and provides database access
	 */
	@Override
	public void execute(Message message, ConnectionToClient client, EchoServer server) {

		@SuppressWarnings("unchecked")
		ArrayList<Object> data = (ArrayList<Object>) message.getData();

		String parkName = (String) data.get(0);
		int month = (int) data.get(1);
		int year = (int) data.get(2);

		// Update approved no-show orders to canceled before generating the report
		server.getDatabase().autoCancelNoShowOrders();

		int parkId = server.getDatabase().getParkIdByName(parkName);

		ArrayList<CancellationReportData> result = server.getDatabase().getCancellationReport(parkId, month, year);

		try {
			client.sendToClient(new Message("CANCELLATION_REPORT_RESULT", result));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}