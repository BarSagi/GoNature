package Strategy;

import java.util.ArrayList;

import Common.Message;
import OCSFUtils.ConnectionToClient;
import Server.EchoServer;

/**
 * Strategy class responsible for registering a family subscriber.
 * The strategy receives subscriber data from the client, registers the family
 * subscriber in the database, and sends the result back to the client.
 */
public class RegisterFamilySubscriberStrategy implements MessageStrategy {

    /**
     * Executes the register family subscriber command.
     * The method extracts the subscriber data from the message, tries to register
     * the family subscriber in the database, and sends a success or failure result
     * back to the client.
     *
     * @param msg the message received from the client
     * @param client the client connection that sent the message
     * @param server the server that handles the request and provides database access
     * @throws Exception if an error occurs while registering or sending the response
     */
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