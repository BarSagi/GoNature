package Strategy;

import java.util.ArrayList;

import Common.Message;
import GUI.ServiceRepSearchEmployeePanelController;
import javafx.application.Platform;

public class EmployeeDetailsResultStrategy implements MessageStrategy {

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