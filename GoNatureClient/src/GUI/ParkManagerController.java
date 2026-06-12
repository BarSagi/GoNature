package GUI;

import Client.ClientUI;
import Client.GoNatureClient;
import Common.Message;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;

import java.io.IOException;

public class ParkManagerController {

	@FXML
	private Label welcomeLabel;

	@FXML
	private Label parkLabel;

	@FXML
	private StackPane contentArea;

	@FXML
	public void initialize() {

		if (GoNatureClient.currentEmployee != null) {

			String fullName = GoNatureClient.currentEmployee.getFirstName() + " "
					+ GoNatureClient.currentEmployee.getLastName();

			welcomeLabel.setText("Welcome " + fullName + "!");
			parkLabel.setText("Park: " + GoNatureClient.currentEmployee.getAffiliation());

		} else {
			welcomeLabel.setText("Welcome!");
			parkLabel.setText("Park: Unknown");
		}
	}

	// =========================
	// NAVIGATION ONLY
	// =========================

	@FXML
	void showVisitReports(ActionEvent event) {
		loadPanel("/GUI/ParkManagerVisitReportsPanel.fxml");
	}

	@FXML
	void showUsageReports(ActionEvent event) {
		loadPanel("/GUI/ParkManagerUsageReportsPanel.fxml");
	}

	@FXML
	void showSubmitRequest(ActionEvent event) {
		loadPanel("/GUI/ParkManagerSubmitRequestPanel.fxml");
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

	// =========================
	// PANEL LOADER
	// =========================

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