package Strategy;

import java.util.ArrayList;

import Common.Message;
import OCSFUtils.ConnectionToClient;
import Server.EchoServer;

/**
 * Strategy class responsible for fetching the current value of a park setting.
 * The strategy receives a park name and request type from the client,
 * retrieves the matching current value from the database,
 * and sends the result back to the client.
 */
public class GetParkCurrentValueStrategy implements MessageStrategy {

    /**
     * Executes the get park current value command.
     * The method extracts the park name and request type from the message,
     * retrieves the current value from the database,
     * and sends the result back to the client.
     *
     * @param msg the message received from the client
     * @param client the client connection that sent the message
     * @param server the server that handles the request and provides database access
     * @throws Exception if an error occurs while fetching or sending the current value
     */
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