package Strategy;

import java.util.ArrayList;
import Common.Message;
import OCSFUtils.ConnectionToClient;
import Server.EchoServer;

/**
 * Strategy class responsible for creating a casual visit.
 * The strategy receives visit details from the client, creates the visit
 * in the database, and sends the result back to the client.
 */
public class CreateCasualVisitStrategy implements MessageStrategy {

    /**
     * Executes the create casual visit command.
     * The method extracts the park name, visitor ID, and visitor count
     * from the message, creates the casual visit in the database,
     * and sends a success or failure response back to the client.
     *
     * @param message the message received from the client
     * @param client the client connection that sent the message
     * @param server the server that handles the request and provides database access
     */
    @Override
    public void execute(Message message, ConnectionToClient client, EchoServer server) {
        try {
            // Unpack the incoming parameters from the network data payload
            @SuppressWarnings("unchecked")
            ArrayList<String> data = (ArrayList<String>) message.getData();

            String parkName = data.get(0);
            String visitorId = data.get(1);
            int visitorCount = Integer.parseInt(data.get(2));

            // Execute DB logic via DBController
            boolean success = server.getDatabase().createCasualVisit(parkName, visitorId, visitorCount);

            // Respond back to the client console handler
            if (success) {
                client.sendToClient(new Message("CREATE_CASUAL_VISIT_RESULT", true));
            } else {
                client.sendToClient(new Message("CREATE_CASUAL_VISIT_RESULT", false));
            }

        } catch (Exception e) {
            e.printStackTrace();
            try {
                client.sendToClient(new Message("CREATE_CASUAL_VISIT_RESULT", false));
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}