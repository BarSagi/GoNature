package Strategy;

import Common.Message;
import OCSFUtils.ConnectionToClient;
import Server.EchoServer;

public class GetVisitorTypeStrategy implements MessageStrategy {

    @Override
    public void execute(Message msg, ConnectionToClient client, EchoServer server) {

        try {
            String visitorId = (String) msg.getData();

            String visitorType = server.getDatabase().getVisitorTypeById(visitorId);

            if (visitorType == null || visitorType.isEmpty()) {
                visitorType = "Individual";
            }

            client.sendToClient(new Message("VISITOR_TYPE_RESULT", visitorType));

        } catch (Exception e) {
            e.printStackTrace();
            try {
                client.sendToClient(new Message("VISITOR_TYPE_RESULT", "Individual"));
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}
