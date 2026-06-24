package Strategy;

import Common.Message;
import Common.Visit;
import OCSFUtils.ConnectionToClient;
import Server.EchoServer;

import java.util.ArrayList;

/**
 * Strategy class responsible for generating a visit duration report.
 * The strategy receives the park name, month, and year from the client,
 * retrieves the matching visit duration data from the database,
 * and sends the result back to the client.
 */
public class GetVisitDurationReportStrategy implements MessageStrategy {

    /**
     * Executes the visit duration report request.
     * The method extracts the report parameters from the message,
     * converts the park name to a park ID, retrieves the visit duration
     * report from the database, and sends the visit list back to the client.
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