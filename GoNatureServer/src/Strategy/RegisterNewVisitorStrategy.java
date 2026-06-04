package Strategy;

import java.util.ArrayList;
import Common.Message;
import OCSFUtils.ConnectionToClient;
import Server.EchoServer;

public class RegisterNewVisitorStrategy implements MessageStrategy {

	@Override
	public void execute(Message msg, ConnectionToClient client, EchoServer server) {
		try {
			// 1. Extract the raw data (ArrayList of Strings) from the client's message
			@SuppressWarnings("unchecked")
			ArrayList<String> visitorData = (ArrayList<String>) msg.getData();

			// 2. Pass the data to the DBController to execute the INSERT query
			boolean isRegistered = server.getDatabase().registerNewVisitor(visitorData);

			// 3. Package the boolean result into a new Message and send it back to the
			// client
			Message responseMsg = new Message("VISITOR_REGISTRATION_RESULT", isRegistered);
			client.sendToClient(responseMsg);

			// Print to server console for debugging
			System.out.println("Server: Registration attempt for visitor ID " + visitorData.get(0) + " resulted in: "
					+ isRegistered);

		} catch (Exception e) {
			System.out.println("Server Error: Failed to execute RegisterNewVisitorStrategy.");
			e.printStackTrace();

			// Optionally send a failure message back to the client if an exception occurs
			try {
				client.sendToClient(new Message("VISITOR_REGISTRATION_RESULT", false));
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}
}