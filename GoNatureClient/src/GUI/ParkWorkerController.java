package GUI;


import Client.ClientUI;
import Client.GoNatureClient;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class ParkWorkerController {
	@FXML
    private Label welcomeLabel;
	@FXML
    private Label parkNameLabel;
	
    public void initialize() {
        // Check if someone is actually logged in to prevent NullPointerException
        if (GoNatureClient.currentEmployee != null) {
            String firstName = GoNatureClient.currentEmployee.getFirstName();
            welcomeLabel.setText("Welcome, " + firstName + "!");
            String parkName = GoNatureClient.currentEmployee.getAffiliation();
            parkNameLabel.setText("Location: " + parkName);
        }
        else {
            // Fallback just in case
            welcomeLabel.setText("Welcome!");
            parkNameLabel.setText("Location: Unknown");
        }
    }
    @FXML
    void goBack(ActionEvent event) {
        ClientUI.changeScreen("/GUI/LoginRoute.fxml", "GoNature - Choose Role");
    }

    @FXML
    void createOrder(ActionEvent event) {
        ClientUI.changeScreen("/GUI/ParkWorkerCreateOrder.fxml", "GoNature - Park Worker Create Order");
    }

    @FXML
    void viewOrders(ActionEvent event) {
        ClientUI.changeScreen("/GUI/ParkWorkerViewOrders.fxml", "GoNature - Park Worker Orders");
    }

    @FXML
    void enterVisitor(ActionEvent event) {
        ClientUI.changeScreen("/GUI/ParkWorkerEnterVisitor.fxml", "Enter Visitor");
    }

    @FXML
    void exitVisitor(ActionEvent event) {
        ClientUI.changeScreen("/GUI/ParkWorkerExitVisitor.fxml", "Exit Visitor");
    }
}