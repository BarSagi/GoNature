package Strategy;

import java.util.ArrayList;

import Common.Message;
import OCSFUtils.ConnectionToClient;
import Server.EchoServer;

/**
 * Strategy class responsible for fetching subscriber details by subscriber ID.
 * The strategy receives a subscriber ID from the client, retrieves the subscriber
 * information from the database, and sends the result back to the client.
 */
public class FetchSubscriberByIdStrategy implements MessageStrategy {

    /**
     * Executes the fetch subscriber by ID command.
     * The method extracts the subscriber ID from the message, fetches the matching
     * subscriber details from the database, and sends the result back to the client.
     *
     * @param msg the message received from the client
     * @param client the client connection that sent the message
     * @param server the server that handles the request and provides database access
     * @throws Exception if an error occurs while fetching the subscriber details or sending the response
     */
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