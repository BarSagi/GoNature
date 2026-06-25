package Strategy;

import java.util.ArrayList;

import Common.Message;
import OCSFUtils.ConnectionToClient;
import Server.EchoServer;

/**
 * Strategy class responsible for submitting a new order. The strategy receives
 * order data from the client, tries to create the order in the database,
 * handles full time slots, and sends the result back to the client.
 */
public class SubmitNewOrderStrategy implements MessageStrategy {

	/**
	 * Executes the submit new order command. The method extracts the order data
	 * from the message, creates the order in the database, sends a success result
	 * if the order is approved, sends alternative times if the selected time slot
	 * is full, or sends a failure result if the order cannot be created.
	 *
	 * @param msg    the message received from the client
	 * @param client the client connection that sent the message
	 * @param server the server that handles the request and provides database
	 *               access
	 */
	@Override
	public void execute(Message msg, ConnectionToClient client, EchoServer server) {

		try {

			@SuppressWarnings("unchecked")
			ArrayList<String> orderData = (ArrayList<String>) msg.getData();

			String dbResult = server.getDatabase().createNewOrder(orderData);

			if (dbResult != null && dbResult.startsWith("Approved")) {
				// Case 1: Order was successfully created
				client.sendToClient(new Message("ORDER_CREATION_RESULT", true));
				System.out.println("Server: Order creation for visitor " + orderData.get(0) + " result: Approved");

			} else if (dbResult != null && dbResult.startsWith("Full")) {
				// Case 2: Time slot is full. Extract alternatives and trigger waiting list
				// logic
				String[] parts = dbResult.split("\\|");
				String alternatives = parts.length > 1 ? parts[1] : "";

				// Package both the alternatives and the original data
				// so the client can use it to join the waiting list if they want
				ArrayList<Object> fullResponse = new ArrayList<>();
				fullResponse.add(alternatives);
				fullResponse.add(orderData);

				client.sendToClient(new Message("TIME_SLOT_FULL", fullResponse));
				System.out.println(
						"Server: Order creation for visitor " + orderData.get(0) + " result: Full (Alternatives sent)");

			} else {
				// Case 3: Standard failure (e.g. database error or missing park)
				client.sendToClient(new Message("ORDER_CREATION_RESULT", false));
				System.out.println("Server: Order creation for visitor " + orderData.get(0) + " result: Failed");
			}

		} catch (Exception e) {

			System.out.println("Server Error: SubmitNewOrderStrategy failed");
			e.printStackTrace();

			try {
				client.sendToClient(new Message("ORDER_CREATION_RESULT", false));
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}
}