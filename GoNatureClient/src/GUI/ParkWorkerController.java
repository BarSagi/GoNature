package GUI;

import Client.ClientUI;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

public class ParkWorkerController {

    @FXML
    public void initialize() {
        System.out.println("ParkWorker screen loaded");
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