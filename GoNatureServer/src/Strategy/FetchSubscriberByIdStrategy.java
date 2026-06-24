package Strategy;

import java.util.ArrayList;

import Common.Message;
import OCSFUtils.ConnectionToClient;
import Server.EchoServer;

public class FetchSubscriberByIdStrategy implements MessageStrategy {

    @Override
    public void execute(Message msg, ConnectionToClient client, EchoServer server) throws Exception {
        try {
            String subscriberId = (String) msg.getData();

            ArrayList<String> subscriberInfo = server.getDatabase().fetchSubscriberById(subscriberId);

            client.sendToClient(new Message("SUBSCRIBER_DETAILS_RESULT", subscriberInfo));

        } catch (Exception e) {
            e.printStackTrace();
            client.sendToClient(new Message("SUBSCRIBER_DETAILS_RESULT", null));
        }
    }
}