package Strategy;

import java.util.ArrayList;

import Common.Message;
import Common.Order;
import OCSFUtils.ConnectionToClient;
import Server.EchoServer;

public class QuickSearchStrategy implements MessageStrategy {

	@Override
	public void execute(Message message, ConnectionToClient client, EchoServer server) throws Exception {
		// 1. Extract the search input (ID or Order Number) from the client's message
		String searchInput = (String) message.getData();
		
		// 2. Query the database using DBController
		ArrayList<Order> searchResults = server.getDatabase().quickSearchOrders(searchInput);
		
		// 3. Create a response message. Even if empty, we send it so the client knows it finished.
		Message returnMsg = new Message("QUICK_SEARCH_RESULT", searchResults);
		
		// 4. Send the result back to the specific client
		client.sendToClient(returnMsg);
	}

}