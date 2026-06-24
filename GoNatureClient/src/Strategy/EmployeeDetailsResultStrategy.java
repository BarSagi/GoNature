package Strategy;

import java.util.ArrayList;

import Common.Message;
import GUI.ServiceRepSearchEmployeePanelController;
import javafx.application.Platform;

/**
 * Handles the server response containing employee details.
 * <p>
 * This strategy receives employee information from the server and loads it
 * into the service representative employee search screen.
 */
public class EmployeeDetailsResultStrategy implements MessageStrategy {

	/**
	 * Executes the strategy for displaying employee details.
	 * <p>
	 * The message data is expected to contain an {@code ArrayList<String>}
	 * with the employee information.
	 *
	 * @param message the message received from the server containing employee details
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void execute(Message message) {
		ArrayList<String> employeeInfo = (ArrayList<String>) message.getData();

		Platform.runLater(() -> {
			if (ServiceRepSearchEmployeePanelController.instance != null) {
				ServiceRepSearchEmployeePanelController.instance.loadEmployeeDetails(employeeInfo);
			}
		});
	}
}