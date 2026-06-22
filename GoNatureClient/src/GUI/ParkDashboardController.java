package GUI;

import Client.ClientUI;
import Client.GoNatureClient;
import Common.Message;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class ParkDashboardController {

	public static ParkDashboardController instance;

	@FXML
	private Label parkNameLabel;
	@FXML
	private Label maxCapacityLabel;
	@FXML
	private Label casualGapLabel;
	@FXML
	private Label avgStayDurationLabel;
	@FXML
	private Label currentVisitorsLabel;
	@FXML
	private Label statusLabel;

	private String currentDisplayedPark;

	@FXML
	public void initialize() {
		instance = this;

		Platform.runLater(() -> {
			// Fetch the role of the currently logged-in employee
			String role = GoNatureClient.currentEmployee.getRole();

			// Check if the user is NOT a Department Manager (using both possible DB strings
			// to be safe)
			if (role != null && !role.equals("DeptManager")) {
				// For Park Managers and Workers, automatically load their affiliated park
				currentDisplayedPark = GoNatureClient.currentEmployee.getAffiliation();
				refreshDashboard(null);
			} else {
				// For Department Managers, wait until a park is selected from the ComboBox.
				// Clear the default labels so the screen doesn't show "Headquarters" or empty
				// dashes.
				parkNameLabel.setText("Please select a park");
				statusLabel.setText("");
			}
		});
	}

	@FXML
	void refreshDashboard(ActionEvent event) {
		try {
			// If no park is selected or set yet, abort the refresh attempt
			if (currentDisplayedPark == null || currentDisplayedPark.isEmpty()) {
				return;
			}

			statusLabel.setStyle("-fx-text-fill: #2980b9;");
			statusLabel.setText("Fetching park data...");
			parkNameLabel.setText("Park: " + currentDisplayedPark);

			Message msg = new Message("GET_PARK_DASHBOARD", currentDisplayedPark);
			ClientUI.client.sendToServer(msg);

		} catch (Exception e) {
			statusLabel.setStyle("-fx-text-fill: red;");
			statusLabel.setText("Error requesting data.");
			e.printStackTrace();
		}
	}

	// ==========================================
	// External method called by the Department Manager screen
	// ==========================================
	public void loadDashboardForPark(String parkName) {
		// Update the current park variable to the one selected in the ComboBox
		this.currentDisplayedPark = parkName;
		// Trigger the refresh method to pull the relevant data from the server
		refreshDashboard(null);
	}

	/**
	 * This method should be called by the client when it receives the Park details
	 * from the server. Ensure you pass the data dynamically. For now, it takes
	 * strings for simplicity. This method is called by the client when it receives
	 * the requested park details from the server.
	 */
	public void updateDashboardData(String parkName, int maxCapacity, int casualGap, int avgStay, int currentVisitors) {
		Platform.runLater(() -> {
			// Update all the labels with the fresh data from the DB
			parkNameLabel.setText("Park: " + parkName);
			maxCapacityLabel.setText(String.valueOf(maxCapacity));
			casualGapLabel.setText(String.valueOf(casualGap));
			avgStayDurationLabel.setText(String.valueOf(avgStay));
			currentVisitorsLabel.setText(String.valueOf(currentVisitors));

			// Show a success message
			statusLabel.setStyle("-fx-text-fill: #27ae60;");
			statusLabel.setText("Data is up to date.");

			// Automatically clear the success message after 3 seconds for a cleaner UI
			javafx.animation.PauseTransition pause = new javafx.animation.PauseTransition(
					javafx.util.Duration.seconds(3));
			pause.setOnFinished(e -> statusLabel.setText(""));
			pause.play();
		});
	}
}