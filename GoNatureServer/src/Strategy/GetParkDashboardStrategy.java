package Strategy;

import java.util.ArrayList;

import Common.Message;
import Database.DBController;
import OCSFUtils.ConnectionToClient;
import Server.EchoServer;

public class GetParkDashboardStrategy implements MessageStrategy {

	@Override
	public void execute(Message message, ConnectionToClient client, EchoServer server) throws Exception {
		// Extract the park name from the message data
		String parkName = (String) message.getData();
		
		// Fetch the dashboard data from the database using the DBController
		ArrayList<String> dashboardData =server.getDatabase().getParkDashboardData(parkName);
		
		// Create a response message with the fetched data
		Message returnMsg = new Message("PARK_DASHBOARD_DATA", dashboardData);
		
		// Send the response back to the specific client who requested it
		client.sendToClient(returnMsg);
	}

}