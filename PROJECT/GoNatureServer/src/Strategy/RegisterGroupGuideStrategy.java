package Strategy;

import java.util.ArrayList;

import Common.Message;
import OCSFUtils.ConnectionToClient;
import Server.EchoServer;

/**
 * Strategy class responsible for registering a group guide.
 * The strategy receives guide data from the client, registers the group guide
 * in the database, and sends the result back to the client.
 */
public class RegisterGroupGuideStrategy implements MessageStrategy {

    /**
     * Executes the register group guide command.
     * The method extracts the guide data from the message, tries to register
     * the group guide in the database, and sends a success or failure result
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

            boolean success = server.getDatabase().registerGroupGuide(data);

            client.sendToClient(new Message("REGISTER_GROUP_GUIDE_RESULT", success));

        } catch (Exception e) {
            e.printStackTrace();
            client.sendToClient(new Message("REGISTER_GROUP_GUIDE_RESULT", false));
        }
    }
}