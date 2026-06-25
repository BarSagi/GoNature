package Strategy;

import java.util.ArrayList;

import Common.Message;
import OCSFUtils.ConnectionToClient;
import Server.EchoServer;

/**
 * Strategy class responsible for handling visitor exit from a park.
 * The strategy receives visitor or worker exit data, identifies the park,
 * updates the database, and sends the exit result back to the client.
 */
public class ExitVisitorStrategy implements MessageStrategy {

    /**
     * Executes the exit visitor command.
     * The method extracts the visitor identifier, park data, and exit amount
     * from the message, converts the park data to a park ID if needed,
     * updates the visitor exit information in the database,
     * and sends the result message back to the client.
     *
     * @param message the message received from the client
     * @param client the client connection that sent the message
     * @param server the server that handles the request and provides database access
     */
    @Override
    public void execute(Message message, ConnectionToClient client, EchoServer server) {
        try {
            @SuppressWarnings("unchecked")
            ArrayList<String> data = (ArrayList<String>) message.getData();

            // Extract the parameters
            String identifier = data.get(0);
            String parkData = data.get(1); // This can be "7" (from Visitor) or "Banias" (from Worker)
            
            int parkId;
            // Check if the data is purely numeric (Visitor sent ID) or a string (Worker sent Name)
            if (parkData.matches("\\d+")) {
                parkId = Integer.parseInt(parkData);
            } else {
                parkId = server.getDatabase().getParkIdByName(parkData);
            }
            
            int exitingAmount = Integer.parseInt(data.get(2));
            
            // Execute the DB update and get the string result message
            String resultMessage = server.getDatabase().exitVisitor(identifier, parkId, exitingAmount);

            // Send the result message back to the client controller
            client.sendToClient(new Message("EXIT_VISITOR_RESULT", resultMessage));

        } catch (Exception e) {
            e.printStackTrace();
            try {
                client.sendToClient(new Message("EXIT_VISITOR_RESULT", "Error: Server exception occurred."));
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}