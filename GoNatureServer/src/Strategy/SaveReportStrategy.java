package Strategy;

import Common.Message;
import Common.ReportImage;
import OCSFUtils.ConnectionToClient;
import Server.EchoServer;

/**
 * Strategy class responsible for saving a report image.
 * The strategy receives a report from the client, saves it in the database,
 * and sends the save result back to the client.
 */
public class SaveReportStrategy implements MessageStrategy {

	/**
	 * Executes the save report command.
	 * The method extracts the report image from the message, saves it in the database,
	 * creates a response message, and sends the result back to the client.
	 *
	 * @param message the message received from the client
	 * @param client the client connection that sent the message
	 * @param server the server that handles the request and provides database access
	 * @throws Exception if an error occurs while saving or sending the report result
	 */
	@Override
	public void execute(Message message, ConnectionToClient client, EchoServer server) throws Exception {

		ReportImage report = (ReportImage) message.getData();

		boolean success = server.getDatabase().saveReport(report);

		Message response = new Message("SAVE_REPORT_RESULT", success);

		try {
			client.sendToClient(response);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}