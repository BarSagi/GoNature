package Strategy;

import java.util.ArrayList;

import Common.Message;
import OCSFUtils.ConnectionToClient;
import Server.EchoServer;

/**
 * Strategy class responsible for submitting a park update request.
 * The strategy receives request data from the client, saves the request
 * in the database, and sends the submission result back to the client.
 */
public class SubmitParkRequestStrategy implements MessageStrategy {

    /**
     * Executes the submit park request command.
     * The method extracts the request data from the message, submits it to the
     * database, and sends a success or failure result back to the client.
     *
     * @param msg the message received from the client
     * @param client the client connection that sent the message
     * @param server the server that handles the request and provides database access
     * @throws Exception if an error occurs while submitting or sending the response
     */
    @Override
    public void execute(Message msg, ConnectionToClient client, EchoServer server) throws Exception {
        try {
            @SuppressWarnings("unchecked")
            ArrayList<String> data = (ArrayList<String>) msg.getData();

            boolean success = server.getDatabase().submitParkRequest(data);

            client.sendToClient(new Message("SUBMIT_PARK_REQUEST_RESULT", success));

        } catch (Exception e) {
            e.printStackTrace();
            client.sendToClient(new Message("SUBMIT_PARK_REQUEST_RESULT", false));
        }
    }
}