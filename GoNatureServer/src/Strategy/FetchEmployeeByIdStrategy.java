package Strategy;

import java.util.ArrayList;

import Common.Message;
import OCSFUtils.ConnectionToClient;
import Server.EchoServer;

public class FetchEmployeeByIdStrategy implements MessageStrategy {

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