package Strategy;

import java.util.ArrayList;

import Common.Message;
import OCSFUtils.ConnectionToClient;
import Server.EchoServer;

/**
 * Strategy class responsible for checking employee login information.
 * The strategy receives employee data from the client, checks the employee
 * details in the database, prevents duplicate logins, and sends the result
 * back to the client.
 */
public class CheckEmployeeInfoStrategy implements MessageStrategy {

	/**
	 * Executes the employee information check command.
	 * The method extracts the employee data from the message, checks if the employee
	 * exists in the database, verifies that the user is not already logged in,
	 * and sends the employee role result back to the client.
	 *
	 * @param msg the message received from the client
	 * @param client the client connection that sent the message
	 * @param server the server that handles the request and provides database access
	 * @throws Exception if an error occurs while checking employee information or sending the response
	 */
	@Override
	public void execute(Message msg, ConnectionToClient client, EchoServer server) throws Exception {
		try {
			@SuppressWarnings("unchecked")
			ArrayList<String> employeeData = (ArrayList<String>) msg.getData();
			
			// Extract user ID 
			String userId = employeeData.get(0); 

			ArrayList<String> fullEmployeeData = server.getDatabase().getEmployeeInfo(employeeData);

			// Check if the user exists in the database
			if (fullEmployeeData != null && !fullEmployeeData.isEmpty()) {
				
				// Attempt to register the user as logged in on the server
				boolean loginSuccess = server.loginUser(userId, client);
				
				if (!loginSuccess) {
					// User is already logged in elsewhere
					System.out.println("Server: Login denied for " + userId + " - Already logged in.");
					
					// Send a special message to the client side to trigger an alert
					client.sendToClient(new Message("ALREADY_LOGGED_IN", null));
					return; // Stop execution
				}
			}

			Message responseMsg = new Message("EMPLOYEE_ROLE_RESULT", fullEmployeeData);
			client.sendToClient(responseMsg);

			System.out.println("Server: Employee info fetch attempt for username "
					+ employeeData.get(0) + " resulted in: " + fullEmployeeData);

		} catch (Exception e) {
			System.out.println("Server Error: Failed to execute CheckEmployeeInfoStrategy.");
			e.printStackTrace();

			try {
				client.sendToClient(new Message("EMPLOYEE_ROLE_RESULT", null));
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}
}