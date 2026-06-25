package Strategy;

import Common.Message;
import Common.ReportImage;
import OCSFUtils.ConnectionToClient;
import Server.EchoServer;

import java.util.List;

/**
 * Strategy class responsible for fetching all report images.
 * The strategy retrieves all reports from the database
 * and sends them back to the client.
 */
public class GetAllReportsStrategy implements MessageStrategy {

	/**
	 * Executes the get all reports command.
	 * The method retrieves all report images from the database,
	 * creates a response message, and sends it back to the client.
	 *
	 * @param message the message received from the client
	 * @param client the client connection that sent the message
	 * @param server the server that handles the request and provides database access
	 * @throws Exception if an error occurs while fetching or sending the reports
	 */
	@Override
	public void execute(Message message, ConnectionToClient client, EchoServer server) throws Exception {

		List<ReportImage> reports = server.getDatabase().getAllReports();

		Message response = new Message("GET_ALL_REPORTS_RESULT", reports);

		client.sendToClient(response);
	}
}