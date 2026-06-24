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

public class ParkWorkerController {

	@FXML
	private Label welcomeLabel;

	@FXML
	private Label parkNameLabel;

	@FXML
	private StackPane contentArea;

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
				ClientUI.client.sendToServer(msg);
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
			// Get the current window (Stage) using one of the nodes (contentArea)
			Stage stage = (Stage) contentArea.getScene().getWindow();
			if (stage != null) {
				stage.setMaximized(true);
			}
		});
	}

	@FXML
	void createOrder(ActionEvent event) {
		loadPanel("/GUI/ParkWorkerCreateOrder.fxml");
	}

	@FXML
	void viewOrders(ActionEvent event) {
		loadPanel("/GUI/ParkWorkerViewOrders.fxml");
	}

	@FXML
	void createCasualVisit(ActionEvent event) {
		loadPanel("/GUI/ParkWorkerCreateCasualVisit.fxml");
	}

	@FXML
	void enterVisitor(ActionEvent event) {
		loadPanel("/GUI/ParkWorkerEnterVisitor.fxml");
	}

	@FXML
	void exitVisitor(ActionEvent event) {
		loadPanel("/GUI/ParkWorkerExitVisitor.fxml");
	}

	@FXML
	void showParkDashboard(ActionEvent event) {
		loadPanel("/GUI/ParkDashboard.fxml");
	}

	@FXML
	void showMyDetails(ActionEvent event) {
		loadPanel("/GUI/ParkWorkerMyDetailsPanel.fxml");
	}

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