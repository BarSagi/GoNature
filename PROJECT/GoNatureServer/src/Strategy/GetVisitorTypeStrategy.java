package Strategy;

import Common.Message;
import OCSFUtils.ConnectionToClient;
import Server.EchoServer;

/**
 * Strategy class responsible for fetching the visitor type by visitor ID.
 * The strategy receives a visitor ID from the client, retrieves the visitor type
 * from the database, and sends the result back to the client.
 */
public class GetVisitorTypeStrategy implements MessageStrategy {

    /**
     * Executes the get visitor type command.
     * The method extracts the visitor ID from the message, retrieves the visitor type
     * from the database, and sends "Individual" as a default value if no type is found.
     *
     * @param msg the message received from the client
     * @param client the client connection that sent the message
     * @param server the server that handles the request and provides database access
     */
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