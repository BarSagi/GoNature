package Strategy;

import Common.Message;
import Common.Visit;
import OCSFUtils.ConnectionToClient;
import Server.EchoServer;

import java.util.ArrayList;

public class GetVisitDurationReportStrategy implements MessageStrategy {

    @Override
    public void execute(Message message, ConnectionToClient client, EchoServer server) {

    	
        @SuppressWarnings("unchecked")
        ArrayList<Object> data = (ArrayList<Object>) message.getData();

        String parkName = (String) data.get(0);
        int month = (int) data.get(1);
        int year = (int) data.get(2);

        int parkId = server.getDatabase().getParkIdByName(parkName);
        
        ArrayList<Visit> visits =
                server.getDatabase().getVisitDurationReport(parkId, month, year);
        
        try {
            Message response = new Message("VISIT_DURATION_REPORT_RESULT", visits);
            client.sendToClient(response);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}