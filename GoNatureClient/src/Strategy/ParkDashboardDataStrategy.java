package Strategy;

import java.util.ArrayList;

import Common.Message;
import GUI.ParkDashboardController;

/**
 * Handles the server response containing park dashboard data.
 * <p>
 * This strategy receives park data from the server, converts the values
 * to their required types, and updates the park dashboard screen.
 */
public class ParkDashboardDataStrategy implements MessageStrategy {

	/**
	 * Executes the strategy for handling park dashboard data.
	 * <p>
	 * The message data is expected to contain an {@code ArrayList<String>}
	 * with the park name, maximum capacity, casual gap, average stay time,
	 * and current number of visitors.
	 *
	 * @param message the message received from the server containing park dashboard data
	 */
	@Override
	public void execute(Message message) {
		// Extract the data sent from the server
		@SuppressWarnings("unchecked")
		ArrayList<String> data = (ArrayList<String>) message.getData();
		
		if (data != null && !data.isEmpty()) {
			// Parse the strings back to their original types
			String parkName = data.get(0);
			int maxCapacity = Integer.parseInt(data.get(1));
			int casualGap = Integer.parseInt(data.get(2));
			int avgStay = Integer.parseInt(data.get(3));
			int currentVisitors = Integer.parseInt(data.get(4));
			
			// Safely pass the data to the UI controller
			// We check if the screen is actually open (instance != null)
			if (ParkDashboardController.instance != null) {
				ParkDashboardController.instance.updateDashboardData(
						parkName, 
						maxCapacity, 
						casualGap, 
						avgStay, 
						currentVisitors
				);
			}
		}
	}

}