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
 * Controller for the service representative's main dashboard. Manages
 * navigation between various service functionalities, including
 * subscriber/guide registration, search operations, and general session
 * management.
 */
public class ServiceRepresentativeDashboardController {

	@FXML
	private Label welcomeLabel;

	@FXML
	private Label departmentLabel;

	@FXML
	private StackPane contentArea;

	/**
	 * Initializes the view, sets the personalized welcome message based on the
	 * current employee, and maximizes the window.
	 */
	@FXML
	public void initialize() {

		// Set user details once upon login
		if (GoNatureClient.currentEmployee != null) {
			welcomeLabel.setText("Welcome, " + GoNatureClient.currentEmployee.getFirstName() + "!");
			departmentLabel.setText(GoNatureClient.currentEmployee.getAffiliation());
		} else {
			welcomeLabel.setText("Welcome!");
			departmentLabel.setText("Central Office");
		}

		Platform.runLater(() -> {
			Stage stage = (Stage) contentArea.getScene().getWindow();
			if (stage != null) {
				stage.setMaximized(true);
			}
		});

		loadPanel("/GUI/ServiceRepSearch.fxml");
	}

	/**
	 * Navigates to the subscriber registration panel.
	 *
	 * @param event The action event.
	 */
	@FXML
	void showRegisterSubscriberPanel(ActionEvent event) {
		loadPanel("/GUI/ServiceRepRegisterSubscriberPanel.fxml");
	}

	/**
	 * Navigates to the group guide registration panel.
	 *
	 * @param event The action event.
	 */
	@FXML
	void showRegisterGuidePanel(ActionEvent event) {
		loadPanel("/GUI/ServiceRepRegisterGuidePanel.fxml");
	}

	/**
	 * Navigates to the casual visit creation panel.
	 *
	 * @param event The action event.
	 */
	@FXML
	void showCreateCasualVisitPanel(ActionEvent event) {
		loadPanel("/GUI/ServiceRepCreateCasualVisit.fxml");
	}

	/**
	 * Navigates to the quick search panel.
	 *
	 * @param event The action event.
	 */
	@FXML
	void showQuickSearchPanel(ActionEvent event) {
		loadPanel("/GUI/ServiceRepSearch.fxml");
	}

	/**
	 * Navigates to the subscriber search panel.
	 *
	 * @param event The action event.
	 */
	@FXML
	void showSearchSubscriberPanel(ActionEvent event) {
		loadPanel("/GUI/ServiceRepSearchSubscriber.fxml");
	}

	/**
	 * Navigates to the employee search panel.
	 *
	 * @param event The action event.
	 */
	@FXML
	void showSearchEmployeePanel(ActionEvent event) {
		loadPanel("/GUI/ServiceRepSearchEmployeePanel.fxml");
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
	 * Helper method to dynamically load and switch FXML sub-panels inside the
	 * content area.
	 *
	 * @param fxmlPath The path to the inner FXML file to load.
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