package Strategy;

import java.util.ArrayList;

import Common.Message;
import Common.Order;
import OCSFUtils.ConnectionToClient;
import Server.EchoServer;

/**
 * Strategy class responsible for performing a quick order search.
 * The strategy receives a search input from the client, searches matching orders
 * in the database, and sends the results back to the client.
 */
public class QuickSearchStrategy implements MessageStrategy {

	/**
	 * Executes the quick search command.
	 * The method extracts the search input from the message, searches for matching
	 * orders by visitor ID or order number, and sends the search results back
	 * to the client.
	 *
	 * @param message the message received from the client
	 * @param client the client connection that sent the message
	 * @param server the server that handles the request and provides database access
	 * @throws Exception if an error occurs while searching or sending the results
	 */
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