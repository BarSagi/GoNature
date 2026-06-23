package Strategy;

import Common.Message;
import Common.ReportImage;
import OCSFUtils.ConnectionToClient;
import Server.EchoServer;

public class SaveReportStrategy implements MessageStrategy {

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