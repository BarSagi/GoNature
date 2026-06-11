package Strategy;

import java.util.ArrayList;

import Common.Message;
import OCSFUtils.ConnectionToClient;
import Server.EchoServer;

public class CheckEmployeeInfoStrategy implements MessageStrategy {

	@Override
	public void execute(Message msg, ConnectionToClient client, EchoServer server) throws Exception {
		try {
			@SuppressWarnings("unchecked")
			ArrayList<String> employeeData = (ArrayList<String>) msg.getData();

			ArrayList<String> fullEmployeeData = server.getDatabase().getEmployeeInfo(employeeData);

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