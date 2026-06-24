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
 * Controller for the Park Manager's main dashboard. Manages navigation between
 * park-specific reports, orders, and requests, and handles the session logout
 * process.
 */
public class ParkManagerController {

	@FXML
	private Label welcomeLabel;

	@FXML
	private Label parkLabel;

	@FXML
	private StackPane contentArea;

	/**
	 * Initializes the view, sets the personalized welcome message, fetches initial
	 * dashboard data, and sets the window to maximized.
	 */
	@FXML
	public void initialize() {

		if (GoNatureClient.currentEmployee != null) {

			String fullName = GoNatureClient.currentEmployee.getFirstName() + " "
					+ GoNatureClient.currentEmployee.getLastName();

			welcomeLabel.setText("Welcome " + fullName + "!");
			parkLabel.setText("Park: " + GoNatureClient.currentEmployee.getAffiliation());

			// Request dashboard data from the server
			try {
				Message msg = new Message("GET_PARK_DASHBOARD", GoNatureClient.currentEmployee.getAffiliation());
				ClientUI.send(msg);
			} catch (Exception e) {
				System.out.println("Error requesting dashboard data.");
				e.printStackTrace();
			}

			loadPanel("/GUI/ParkDashboard.fxml");

		} else {
			welcomeLabel.setText("Welcome!");
			parkLabel.setText("Park: Unknown");
		}

		Platform.runLater(() -> {
			Stage stage = (Stage) contentArea.getScene().getWindow();
			if (stage != null) {
				stage.setMaximized(true);
			}
		});
	}

	/**
	 * Navigates to the visit reports panel.
	 *
	 * @param event The action event.
	 */
	@FXML
	void showVisitReports(ActionEvent event) {
		loadPanel("/GUI/ParkManagerVisitReportsPanel.fxml");
	}

	/**
	 * Navigates to the usage reports panel.
	 *
	 * @param event The action event.
	 */
	@FXML
	void showUsageReports(ActionEvent event) {
		loadPanel("/GUI/ParkManagerUsageReportsPanel.fxml");
	}

	/**
	 * Navigates to the park orders panel.
	 *
	 * @param event The action event.
	 */
	@FXML
	void showParkOrders(ActionEvent event) {
		loadPanel("/GUI/ParkWorkerViewOrders.fxml");
	}

	/**
	 * Navigates to the parameter request submission panel.
	 *
	 * @param event The action event.
	 */
	@FXML
	void showSubmitRequest(ActionEvent event) {
		loadPanel("/GUI/ParkManagerSubmitRequestPanel.fxml");
	}

	/**
	 * Navigates back to the main park dashboard.
	 *
	 * @param event The action event.
	 */
	@FXML
	void showParkDashboard(ActionEvent event) {
		loadPanel("/GUI/ParkDashboard.fxml");
	}

	/**
	 * Handles the logout process, notifies the server, and returns to the login
	 * route.
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
	 * Loads a specified FXML panel into the content area.
	 *
	 * @param fxmlPath The path to the FXML file to be loaded.
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