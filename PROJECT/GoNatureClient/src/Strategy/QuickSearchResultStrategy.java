package Strategy;

import java.util.ArrayList;

import Common.Message;
import Common.Order;
import GUI.ServiceRepSearchController;
import javafx.application.Platform;

/**
 * Handles the server response containing quick search results.
 * <p>
 * This strategy receives a list of orders from the server and passes it
 * to the service representative search screen.
 */
public class QuickSearchResultStrategy implements MessageStrategy {

	/**
	 * Executes the strategy for handling quick search results.
	 * <p>
	 * The message data is expected to contain an {@code ArrayList<Order>}
	 * with the orders that match the search request.
	 *
	 * @param message the message received from the server containing the search results
	 */
	@Override
	public void execute(Message message) {
		// 1. Extract the ArrayList of orders sent from the server
		@SuppressWarnings("unchecked")
		ArrayList<Order> searchResults = (ArrayList<Order>) message.getData();
		
		// 2. Pass the data to the UI controller on the JavaFX Application Thread
		Platform.runLater(() -> {
			if (ServiceRepSearchController.instance != null) {
				ServiceRepSearchController.instance.handleSearchResults(searchResults);
			}
		});
	}

}