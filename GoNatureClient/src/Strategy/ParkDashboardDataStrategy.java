package Strategy;

import java.util.ArrayList;

import Common.Message;
import GUI.ParkDashboardController;

public class ParkDashboardDataStrategy implements MessageStrategy {

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
