package Strategy;

import Common.Message;
import Entity.Employee;

import java.util.ArrayList;

import Client.ClientUI;
import Client.GoNatureClient;
import GUI.LoginEmployeeController;
import javafx.application.Platform;

public class EmployeeInfoStrategy implements MessageStrategy {
    @Override
    public void execute(Message message) {
        @SuppressWarnings("unchecked")
		ArrayList<String> employeeData = (ArrayList<String>) message.getData();

        if (employeeData == null || employeeData.isEmpty()) {
            Platform.runLater(() -> LoginEmployeeController.instance.showError("Invalid username or password"));
            return;
        }

        // 1. Extract data (based on the DB order we built earlier)
        int id = Integer.parseInt(employeeData.get(0));
        String firstName = employeeData.get(1);
        String lastName = employeeData.get(2);
        String email = employeeData.get(3);
        String username = employeeData.get(4);
        // get(5) is password, we skip saving it to the object
        String role = employeeData.get(6);
        String affiliation = employeeData.get(7);

        // 2. Create the object and save it globally in the session
        GoNatureClient.currentEmployee = new Employee(id, firstName, lastName, email, username, role, affiliation);

        System.out.println("Logged in successfully. Role = " + role);

        // 3. Navigate based on role
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