package GUI;

import Client.ClientUI;
import Client.GoNatureClient;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;

import java.io.IOException;

public class DeptManagerController {

    @FXML
    private Label welcomeLabel;

    @FXML
    private Label departmentLabel;

    @FXML
    private StackPane contentArea;

    @FXML
    public void initialize() {
        if (GoNatureClient.currentEmployee != null) {
            String fullName = GoNatureClient.currentEmployee.getFirstName() + " "
                    + GoNatureClient.currentEmployee.getLastName();

            welcomeLabel.setText("Welcome " + fullName + "!");
            departmentLabel.setText("Department: " + GoNatureClient.currentEmployee.getAffiliation());
        } else {
            welcomeLabel.setText("Welcome!");
            departmentLabel.setText("Department: Unknown");
        }
    }

    @FXML
    void showPendingRequests(ActionEvent event) {
        loadPanel("/DeptManagerPendingRequestsPanel.fxml");
    }

    @FXML
    void showApproveReject(ActionEvent event) {
        loadPanel("/GUI/DeptManagerApproveRejectPanel.fxml");
    }

    @FXML
    void showReports(ActionEvent event) {
        loadPanel("/GUI/DeptManagerReportsPanel.fxml");
    }
    
    @FXML
    void showVisitDurationReport(ActionEvent event) {
    	loadPanel("/GUI/DeptManagerVisitDurationReportPanel.fxml");
    }

    @FXML
    void showCancellationReport(ActionEvent event) {
    	loadPanel("/GUI/DeptManagerCancellationReportPanel.fxml");
    }
    
    @FXML
    void handleLogout(ActionEvent event) {
        ClientUI.changeScreen("/GUI/LoginRoute.fxml", "GoNature - Choose Role");
    }

    private void loadPanel(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent subPanel = loader.load();

            contentArea.getChildren().clear();
            contentArea.getChildren().add(subPanel);

        } catch (IOException e) {
            e.printStackTrace();
            contentArea.getChildren().clear();
            contentArea.getChildren().add(new Label("Error: Could not load the requested form."));
        }
    }
}
