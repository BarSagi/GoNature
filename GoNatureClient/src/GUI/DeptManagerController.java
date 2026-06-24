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

/**
 * Controller for the Department Manager's main dashboard. Manages the
 * navigation between different sub-panels and handles the logout process.
 */
public class DeptManagerController {

	@FXML
	private Label welcomeLabel;

	@FXML
	private StackPane contentArea;

	/**
	 * Initializes the view, sets the welcome message, maximizes the window, and
	 * loads the default dashboard panel.
	 */
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
			Stage stage = (Stage) contentArea.getScene().getWindow();
			if (stage != null) {
				stage.setMaximized(true);
			}
		});

		loadPanel("/GUI/DeptManagerParkDashboard.fxml");
	}

	/**
	 * Loads the approval/rejection panel.
	 * 
	 * @param event The action event.
	 */
	@FXML
	void showApproveReject(ActionEvent event) {
		loadPanel("/GUI/DeptManagerApproveRejectPanel.fxml");
	}

	/**
	 * Loads the visit duration report panel.
	 * 
	 * @param event The action event.
	 */
	@FXML
	void showVisitDurationReport(ActionEvent event) {
		loadPanel("/GUI/DeptManagerVisitDurationReportPanel.fxml");
	}

	/**
	 * Loads the cancellation report panel.
	 * 
	 * @param event The action event.
	 */
	@FXML
	void showCancellationReport(ActionEvent event) {
		loadPanel("/GUI/DeptManagerCancellationReportPanel.fxml");
	}

	/**
	 * Loads the park dashboard panel.
	 * 
	 * @param event The action event.
	 */
	@FXML
	void showParkDashboard(ActionEvent event) {
		loadPanel("/GUI/DeptManagerParkDashboard.fxml");
	}

	/**
	 * Loads the saved reports panel.
	 * 
	 * @param event The action event.
	 */
	@FXML
	void showSavedReports(ActionEvent event) {
		loadPanel("/GUI/DeptManagerSavedReportsPanel.fxml");
	}

	/**
	 * Handles the logout process, notifies the server, and returns to the login
	 * screen.
	 * 
	 * @param event The action event.
	 */
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

	/**
	 * Helper method to load a FXML sub-panel into the content area.
	 * 
	 * @param fxmlPath The resource path to the FXML file.
	 */
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