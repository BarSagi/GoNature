package Strategy;

import java.util.ArrayList;

import Common.Message;
import OCSFUtils.ConnectionToClient;
import Server.EchoServer;

/**
 * Strategy class responsible for fetching dashboard data for a specific park.
 * The strategy receives a park name from the client, retrieves the park dashboard
 * data from the database, and sends the result back to the client.
 */
public class GetParkDashboardStrategy implements MessageStrategy {

	/**
	 * Executes the get park dashboard command.
	 * The method extracts the park name from the message, fetches the dashboard
	 * data from the database, creates a response message, and sends it back
	 * to the requesting client.
	 *
	 * @param message the message received from the client
	 * @param client the client connection that sent the message
	 * @param server the server that handles the request and provides database access
	 * @throws Exception if an error occurs while fetching or sending the dashboard data
	 */
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