package Strategy;

import Common.Message;
import Client.ClientUI;
import GUI.LoginEmployeeController;
import javafx.application.Platform;

public class EmployeeRoleStrategy implements MessageStrategy {
	@Override
	public void execute(Message message) {
		String role = (String) message.getData();

		if (role == null) {
			Platform.runLater(() -> LoginEmployeeController.instance.showError("Invalid username or password"));
			return;
		}

		System.out.println("Role = " + role);

		Platform.runLater(() -> {
			switch (role) {
			case "ParkWorker":
				ClientUI.changeScreen("/GUI/ParkWorker.fxml", "Park Worker");
				break;
			case "ServiceRep":
				ClientUI.changeScreen("/GUI/ServiceRep.fxml", "Service Rep");
				break;
			case "ParkManager":
				ClientUI.changeScreen("/GUI/ParkManager.fxml", "Park Manager");
				break;
			case "DeptManager":
				ClientUI.changeScreen("/GUI/DeptManager.fxml", "Dept Manager");
				break;
			}
		});
	}
}