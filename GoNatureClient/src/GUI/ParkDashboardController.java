package GUI;

import Client.ClientUI;
import Client.GoNatureClient;
import Common.Message;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

/**
 * Controller for the park dashboard view. Displays real-time data regarding
 * park capacity, casual gaps, average stay duration, and current visitor count.
 */
public class ParkDashboardController {

	/**
	 * Static instance of this controller for external access.
	 */
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

	/**
	 * Initializes the dashboard. If the user is a park manager or worker, it
	 * automatically loads their park's dashboard.
	 */
	@FXML
	public void initialize() {
		instance = this;

		Platform.runLater(() -> {
			String role = GoNatureClient.currentEmployee.getRole();

			if (role != null && !role.equals("DeptManager")) {
				currentDisplayedPark = GoNatureClient.currentEmployee.getAffiliation();
				refreshDashboard(null);
			} else {
				parkNameLabel.setText("Please select a park");
				statusLabel.setText("");
			}
		});
	}

	/**
	 * Refreshes the dashboard data by requesting the latest metrics from the
	 * server.
	 *
	 * @param event The action event.
	 */
	@FXML
	void refreshDashboard(ActionEvent event) {
		try {
			if (currentDisplayedPark == null || currentDisplayedPark.isEmpty()) {
				return;
			}

			statusLabel.setStyle("-fx-text-fill: #2980b9;");
			statusLabel.setText("Fetching park data...");
			parkNameLabel.setText("Park: " + currentDisplayedPark);

			Message msg = new Message("GET_PARK_DASHBOARD", currentDisplayedPark);
			ClientUI.send(msg);

		} catch (Exception e) {
			statusLabel.setStyle("-fx-text-fill: red;");
			statusLabel.setText("Error requesting data.");
			e.printStackTrace();
		}
	}

	/**
	 * Updates the dashboard view for a specific park.
	 *
	 * @param parkName The name of the park to display.
	 */
	public void loadDashboardForPark(String parkName) {
		this.currentDisplayedPark = parkName;
		refreshDashboard(null);
	}

	/**
	 * Updates the UI labels with fresh data received from the server.
	 *
	 * @param parkName        The name of the park.
	 * @param maxCapacity     The park's maximum capacity.
	 * @param casualGap       The gap setting for casual visitors.
	 * @param avgStay         The average stay duration in minutes.
	 * @param currentVisitors The current number of visitors in the park.
	 */
	public void updateDashboardData(String parkName, int maxCapacity, int casualGap, int avgStay, int currentVisitors) {
		Platform.runLater(() -> {
			parkNameLabel.setText("Park: " + parkName);
			maxCapacityLabel.setText(String.valueOf(maxCapacity));
			casualGapLabel.setText(String.valueOf(casualGap));
			avgStayDurationLabel.setText(String.valueOf(avgStay));
			currentVisitorsLabel.setText(String.valueOf(currentVisitors));

			statusLabel.setStyle("-fx-text-fill: #27ae60;");
			statusLabel.setText("Data is up to date.");

			javafx.animation.PauseTransition pause = new javafx.animation.PauseTransition(
					javafx.util.Duration.seconds(3));
			pause.setOnFinished(e -> statusLabel.setText(""));
			pause.play();
		});
	}
}