package GUI;

import Client.ClientUI;
import Client.GoNatureClient;
import Common.Message;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

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
        } else {
            welcomeLabel.setText("Welcome!");
        }
        Platform.runLater(() -> {
            // Get the current window (Stage) using one of the nodes (contentArea)
            Stage stage = (Stage) contentArea.getScene().getWindow();
            if (stage != null) {
                stage.setMaximized(true);
            }
        });
    }

    @FXML
    void showApproveReject(ActionEvent event) {
        loadPanel("/GUI/DeptManagerApproveRejectPanel.fxml");
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
    	try {
			String userID = GoNatureClient.currentEmployee.getEmployeeId();
			GoNatureClient.currentEmployee = null;
			Message msg = new Message("CLIENT_LOGOUT", userID);

			try {
				ClientUI.send(msg);
			} catch (Exception e) {
				System.out.println("Error sending message to server");
				e.printStackTrace();
			}
			ClientUI.changeScreen("/GUI/LoginRoute.fxml", "GoNature Login");
		} catch (Exception e) {
			e.printStackTrace();
		}
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
