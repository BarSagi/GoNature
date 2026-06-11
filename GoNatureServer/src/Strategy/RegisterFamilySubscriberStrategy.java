package Strategy;

import java.util.ArrayList;

import Common.Message;
import OCSFUtils.ConnectionToClient;
import Server.EchoServer;

public class RegisterFamilySubscriberStrategy implements MessageStrategy {

    @Override
    public void execute(Message msg, ConnectionToClient client, EchoServer server) throws Exception {
        try {
            @SuppressWarnings("unchecked")
            ArrayList<String> data = (ArrayList<String>) msg.getData();

            boolean success = server.getDatabase().registerFamilySubscriber(data);

            client.sendToClient(new Message("REGISTER_FAMILY_SUBSCRIBER_RESULT", success));

        } catch (Exception e) {
            e.printStackTrace();
            client.sendToClient(new Message("REGISTER_FAMILY_SUBSCRIBER_RESULT", false));
        }
    }
}