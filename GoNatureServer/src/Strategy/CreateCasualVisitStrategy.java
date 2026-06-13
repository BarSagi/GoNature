package Strategy;

import java.util.ArrayList;
import Common.Message;
import OCSFUtils.ConnectionToClient;
import Server.EchoServer;

public class CreateCasualVisitStrategy implements MessageStrategy {

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
            boolean success = server.getDatabase().registerCasualVisit(parkName, visitorId, visitorCount);

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