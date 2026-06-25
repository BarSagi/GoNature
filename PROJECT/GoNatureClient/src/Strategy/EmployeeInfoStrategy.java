package Strategy;

import java.util.ArrayList;

import Common.Message;
import Entity.Employee;
import Client.ClientUI;
import Client.GoNatureClient;
import GUI.LoginEmployeeController;
import javafx.application.Platform;

/**
 * Handles the server response containing employee login information.
 * <p>
 * This strategy validates the employee data received from the server, creates
 * the current employee object, and redirects the employee to the correct screen
 * according to their role.
 */
public class EmployeeInfoStrategy implements MessageStrategy {

	/**
	 * Executes the strategy for handling employee login data.
	 * <p>
	 * If the received data is empty, an error message is displayed.
	 * Otherwise, the employee details are saved in the client and the relevant
	 * employee dashboard is opened according to the employee role.
	 *
	 * @param message the message received from the server containing employee data
	 */
	@Override
	public void execute(Message message) {
		@SuppressWarnings("unchecked")
		ArrayList<String> employeeData = (ArrayList<String>) message.getData();

		if (employeeData == null || employeeData.isEmpty()) {
			Platform.runLater(() -> LoginEmployeeController.instance.showError("Invalid username or password"));
			return;
		}

		String id = employeeData.get(0);
		String firstName = employeeData.get(1);
		String lastName = employeeData.get(2);
		String email = employeeData.get(3);
		String username = employeeData.get(4);
		String role = employeeData.get(6);
		String affiliation = employeeData.get(7);

		GoNatureClient.currentEmployee = new Employee(id, firstName, lastName, email, username, role, affiliation);

		System.out.println("EmployeeInfoStrategy started");
		System.out.println("Role = " + role);

		Platform.runLater(() -> {
			switch (role) {
			case "ParkWorker":
				ClientUI.changeScreen("/GUI/ParkWorker.fxml", "Park Worker");
				break;

			case "ServiceRep":
				ClientUI.changeScreen("/GUI/ServiceRepresentativeDashboard.fxml", "Service Rep");
				break;

			case "ParkManager":
				ClientUI.changeScreen("/GUI/ParkManager.fxml", "Park Manager");
				break;

			case "DeptManager":
				ClientUI.changeScreen("/GUI/DeptManager.fxml", "Dept Manager");
				break;

			default:
				LoginEmployeeController.instance.showError("Unknown Role: " + role);
			}
		});
	}
}