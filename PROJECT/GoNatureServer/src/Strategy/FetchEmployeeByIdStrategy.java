package Strategy;

import java.util.ArrayList;

import Common.Message;
import OCSFUtils.ConnectionToClient;
import Server.EchoServer;

/**
 * Strategy class responsible for fetching employee details by employee ID.
 * The strategy receives an employee ID from the client, retrieves the employee
 * information from the database, and sends the result back to the client.
 */
public class FetchEmployeeByIdStrategy implements MessageStrategy {

	/**
	 * Executes the fetch employee by ID command.
	 * The method extracts the employee ID from the message, fetches the matching
	 * employee details from the database, and sends the result back to the client.
	 *
	 * @param msg the message received from the client
	 * @param client the client connection that sent the message
	 * @param server the server that handles the request and provides database access
	 * @throws Exception if an error occurs while fetching the employee details or sending the response
	 */
	@Override
	public void execute(Message msg, ConnectionToClient client, EchoServer server) throws Exception {
		try {
			String employeeId = (String) msg.getData();

			ArrayList<String> employeeInfo = server.getDatabase().fetchEmployeeById(employeeId);

			client.sendToClient(new Message("EMPLOYEE_DETAILS_RESULT", employeeInfo));

		} catch (Exception e) {
			e.printStackTrace();
			client.sendToClient(new Message("EMPLOYEE_DETAILS_RESULT", null));
		}
	}
}