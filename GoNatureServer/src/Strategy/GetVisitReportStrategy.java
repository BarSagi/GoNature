package Strategy;

import Common.Message;
import Common.VisitReportData;
import OCSFUtils.ConnectionToClient;
import Server.EchoServer;

import java.util.ArrayList;

public class GetVisitReportStrategy implements MessageStrategy {

	@Override
	public void execute(Message message, ConnectionToClient client, EchoServer server) {

	    try {

	        @SuppressWarnings("unchecked")
	        ArrayList<Object> data = (ArrayList<Object>) message.getData();

	        String parkName = (String) data.get(0);
	        int month = (Integer) data.get(1);
	        int year = (Integer) data.get(2);

	        int parkId = server.getDatabase().getParkIdByName(parkName);

	        if (parkId == -1) {
	            System.out.println("ERROR: parkId not found!");
	            client.sendToClient(new Message("VISIT_REPORT_RESULT", null));
	            return;
	        }

	        VisitReportData report =
	                server.getReportService().generateVisitReport(parkId, month, year);

	        client.sendToClient(new Message("VISIT_REPORT_RESULT", report));

	    } catch (Exception e) {
	        System.out.println("EXCEPTION IN VISIT REPORT:");
	        e.printStackTrace();
	    }
	}
}