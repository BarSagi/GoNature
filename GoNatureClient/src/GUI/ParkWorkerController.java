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
 * Controller for the Park Worker's main dashboard. Manages navigation between
 * park-related actions such as creating orders, registering visitor
 * entries/exits, and viewing the park dashboard.
 */
public class ParkWorkerController {

	@FXML
	private Label welcomeLabel;

	@FXML
	private Label parkNameLabel;

	@FXML
	private StackPane contentArea;

	/**
	 * Initializes the controller, sets the user's welcome information, requests
	 * initial dashboard data, and maximizes the application window.
	 */
	@FXML
	public void initialize() {
		if (GoNatureClient.currentEmployee != null) {
			String fullName = GoNatureClient.currentEmployee.getFirstName() + " "
					+ GoNatureClient.currentEmployee.getLastName();

			welcomeLabel.setText("Welcome " + fullName + "!");
			parkNameLabel.setText("Park: " + GoNatureClient.currentEmployee.getAffiliation());

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
			parkNameLabel.setText("Park: Unknown");
		}

		Platform.runLater(() -> {
			Stage stage = (Stage) contentArea.getScene().getWindow();
			if (stage != null) {
				stage.setMaximized(true);
			}
		});
	}

	/**
	 * Navigates to the order creation panel.
	 * 
	 * @param event The action event.
	 */
	@FXML
	void createOrder(ActionEvent event) {
		loadPanel("/GUI/ParkWorkerCreateOrder.fxml");
	}

	/**
	 * Navigates to the order viewing panel.
	 * 
	 * @param event The action event.
	 */
	@FXML
	void viewOrders(ActionEvent event) {
		loadPanel("/GUI/ParkWorkerViewOrders.fxml");
	}

	/**
	 * Navigates to the casual visit creation panel.
	 * 
	 * @param event The action event.
	 */
	@FXML
	void createCasualVisit(ActionEvent event) {
		loadPanel("/GUI/ParkWorkerCreateCasualVisit.fxml");
	}

	/**
	 * Navigates to the visitor entry registration panel.
	 * 
	 * @param event The action event.
	 */
	@FXML
	void enterVisitor(ActionEvent event) {
		loadPanel("/GUI/ParkWorkerEnterVisitor.fxml");
	}

	/**
	 * Navigates to the visitor exit registration panel.
	 * 
	 * @param event The action event.
	 */
	@FXML
	void exitVisitor(ActionEvent event) {
		loadPanel("/GUI/ParkWorkerExitVisitor.fxml");
	}

	/**
	 * Navigates to the park dashboard view.
	 * 
	 * @param event The action event.
	 */
	@FXML
	void showParkDashboard(ActionEvent event) {
		loadPanel("/GUI/ParkDashboard.fxml");
	}

	/**
	 * Navigates to the worker's personal details panel.
	 * 
	 * @param event The action event.
	 */
	@FXML
	void showMyDetails(ActionEvent event) {
		loadPanel("/GUI/ParkWorkerMyDetailsPanel.fxml");
	}

	/**
	 * Handles the logout process, notifies the server, and returns to the login
	 * route.
	 * 
	 * @param event The action event.
	 */
	@FXML
	void goBack(ActionEvent event) {
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