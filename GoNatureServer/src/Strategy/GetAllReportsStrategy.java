package Strategy;

import Common.Message;
import Common.ReportImage;
import OCSFUtils.ConnectionToClient;
import Server.EchoServer;

import java.util.List;

public class GetAllReportsStrategy implements MessageStrategy {

	@Override
	public void execute(Message message, ConnectionToClient client, EchoServer server) throws Exception {

		List<ReportImage> reports = server.getDatabase().getAllReports();

		Message response = new Message("GET_ALL_REPORTS_RESULT", reports);

		client.sendToClient(response);
	}
}