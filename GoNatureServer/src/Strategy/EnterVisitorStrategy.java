package Strategy;

import java.util.ArrayList;

import Common.Message;
import OCSFUtils.ConnectionToClient;
import Server.EchoServer;

public class EnterVisitorStrategy implements MessageStrategy {

    @Override
    public void execute(Message message, ConnectionToClient client, EchoServer server) {
        try {
            @SuppressWarnings("unchecked")
            ArrayList<String> data = (ArrayList<String>) message.getData();

            String visitorId = data.get(0);

            boolean success = server.getDatabase().enterVisitor(visitorId);

            client.sendToClient(new Message("ENTER_VISITOR_RESULT", success));

        } catch (Exception e) {
            e.printStackTrace();
            try {
                client.sendToClient(new Message("ENTER_VISITOR_RESULT", false));
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}
