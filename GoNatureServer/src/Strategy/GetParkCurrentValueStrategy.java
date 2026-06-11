package Strategy;

import java.util.ArrayList;

import Common.Message;
import OCSFUtils.ConnectionToClient;
import Server.EchoServer;

public class GetParkCurrentValueStrategy implements MessageStrategy {

    @Override
    public void execute(Message msg, ConnectionToClient client, EchoServer server) throws Exception {
        try {
            @SuppressWarnings("unchecked")
            ArrayList<String> data = (ArrayList<String>) msg.getData();

            String parkName = data.get(0);
            String requestType = data.get(1);

            String currentValue = server.getDatabase().getParkCurrentValue(parkName, requestType);

            client.sendToClient(new Message("GET_PARK_CURRENT_VALUE_RESULT", currentValue));

        } catch (Exception e) {
            e.printStackTrace();
            client.sendToClient(new Message("GET_PARK_CURRENT_VALUE_RESULT", null));
        }
    }
}
