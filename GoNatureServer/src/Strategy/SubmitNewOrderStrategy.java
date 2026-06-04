package Strategy;

import java.util.ArrayList;
import Common.Message;
import OCSFUtils.ConnectionToClient;
import Server.EchoServer;

public class SubmitNewOrderStrategy implements MessageStrategy {

	@Override
	public void execute(Message msg, ConnectionToClient client, EchoServer server) {
		try {
			// 1. Extract the raw data (ArrayList of Strings) from the client's message
			@SuppressWarnings("unchecked")
			ArrayList<String> orderData = (ArrayList<String>) msg.getData();

			// 2. Pass the data to the DBController to execute the INSERT query
			// Note: If your DBController is a Singleton, this might be
			// DBController.getInstance().createNewOrder(orderData);
			// Assuming your EchoServer holds the DBController instance:
			boolean isCreated = server.getDatabase().createNewOrder(orderData);

			// 3. Package the boolean result into a new Message and send it back to the
			// client
			Message responseMsg = new Message("ORDER_CREATION_RESULT", isCreated);
			client.sendToClient(responseMsg);

			// Print to server console for debugging
			System.out.println(
					"Server: Order creation attempt for visitor " + orderData.get(0) + " resulted in: " + isCreated);

		} catch (Exception e) {
			System.out.println("Server Error: Failed to execute SubmitNewOrderStrategy.");
			e.printStackTrace();

			// Optionally send a failure message back to the client if an exception occurs
			try {
				client.sendToClient(new Message("ORDER_CREATION_RESULT", false));
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}
}