package Strategy;

import java.util.ArrayList;

import Common.Message;
import Common.Order;
import GUI.ServiceRepSearchController;
import javafx.application.Platform;

public class QuickSearchResultStrategy implements MessageStrategy {

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