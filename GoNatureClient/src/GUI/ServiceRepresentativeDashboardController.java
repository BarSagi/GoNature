package GUI;

import Client.ClientUI;
import Client.GoNatureClient;
import Common.Message;

import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;

public class ServiceRepresentativeDashboardController {

	@FXML
	private Label welcomeLabel;

	@FXML
	private Label departmentLabel;

	@FXML
	private StackPane contentArea; // Container where sub-panels will be loaded

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
	}

	/**
	 * Sidebar Button Action: Loads the Family Subscriber registration panel
	 */
	@FXML
	void showRegisterSubscriberPanel(ActionEvent event) {
		loadPanel("/GUI/ServiceRepRegisterSubscriberPanel.fxml");
	}

	/**
	 * Sidebar Button Action: Loads the Group Guide registration panel
	 */
	@FXML
	void showRegisterGuidePanel(ActionEvent event) {
		loadPanel("/GUI/ServiceRepRegisterGuidePanel.fxml");
	}

	/**
	 * Sidebar Button Action: Logs out and returns to the main login route
	 */
	@FXML
	void handleLogout(ActionEvent event) {
		// In a real application, you might also want to clear currentEmployee session
		// data here
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
	 * contentArea
	 * 
	 * @param fxmlPath The path to the inner FXML file
	 */
	private void loadPanel(String fxmlPath) {
		try {
			// Load the FXML file dynamically
			FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
			Parent subPanel = loader.load();

			// Clear current view inside the StackPane and replace it with the new sub-panel
			contentArea.getChildren().clear();
			contentArea.getChildren().add(subPanel);

		} catch (IOException e) {
			e.printStackTrace();
			// Fallback display in case the FXML failed to load
			contentArea.getChildren().clear();
			contentArea.getChildren().add(new Label("Error: Could not load the requested form."));
		}
	}
}