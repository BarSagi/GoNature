package Strategy;

import java.util.ArrayList;

import Common.Message;
import OCSFUtils.ConnectionToClient;
import Server.EchoServer;

public class CheckEmployeeInfoStrategy implements MessageStrategy {

	@Override
	public void execute(Message msg, ConnectionToClient client, EchoServer server) throws Exception {
		try {
			// 1. Extract the raw data (ArrayList of Strings) from the client's message
			@SuppressWarnings("unchecked")
			ArrayList<String> employeeData = (ArrayList<String>) msg.getData();

			// 2. Pass the data to the DBController to execute the INSERT query
			String role = server.getDatabase().getEmployeeRole(employeeData);

			// 3. Package the boolean result into a new Message and send it back to the
			// client
			Message responseMsg = new Message("EMPLOYEE_ROLE_RESULT", role);
			client.sendToClient(responseMsg);

			// Print to server console for debugging
			System.out.println("Server: Role fetch attempt attempt for employee's username " + employeeData.get(0) + " resulted in: "
					+ role);

		} catch (Exception e) {
			System.out.println("Server Error: Failed to execute CheckEmployeeInfoStrategy.");
			e.printStackTrace();

			// Optionally send a failure message back to the client if an exception occurs
			try {
				client.sendToClient(new Message("EMPLOYEE_ROLE_RESULT", null));
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}

	}

}
