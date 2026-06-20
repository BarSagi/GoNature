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
    private Label openCasualSpotsLabel;
    @FXML
    private Label statusLabel;

    @FXML
    public void initialize() {
        instance = this;
        // Request data when the screen is loaded
        Platform.runLater(() -> refreshDashboard(null));
    }

    @FXML
    void refreshDashboard(ActionEvent event) {
        try {
            statusLabel.setStyle("-fx-text-fill: #2980b9;");
            statusLabel.setText("Fetching park data...");
            
            // Assuming currentEmployee knows which park they belong to
            String parkName = GoNatureClient.currentEmployee.getAffiliation();
            Message msg = new Message("GET_PARK_DASHBOARD", parkName);
            ClientUI.client.sendToServer(msg);
            
        } catch (Exception e) {
            statusLabel.setStyle("-fx-text-fill: red;");
            statusLabel.setText("Error requesting data.");
            e.printStackTrace();
        }
    }

    /**
     * This method should be called by the client when it receives the Park details from the server.
     * Ensure you pass the data dynamically. For now, it takes strings for simplicity.
     */
    public void updateDashboardData(String parkName, int maxCapacity, int casualGap, int avgStay, int currentVisitors, int openSpots) {
		Platform.runLater(() -> {
			parkNameLabel.setText("Park: " + parkName);
			maxCapacityLabel.setText(String.valueOf(maxCapacity));
			casualGapLabel.setText(String.valueOf(casualGap));
			avgStayDurationLabel.setText(String.valueOf(avgStay));
			currentVisitorsLabel.setText(String.valueOf(currentVisitors));
			openCasualSpotsLabel.setText(String.valueOf(openSpots));
			
			statusLabel.setStyle("-fx-text-fill: #27ae60;");
			statusLabel.setText("Data is up to date.");
			
			// Clear the success message after 3 seconds
			javafx.animation.PauseTransition pause = new javafx.animation.PauseTransition(javafx.util.Duration.seconds(3));
			pause.setOnFinished(e -> statusLabel.setText(""));
			pause.play();
		});
}
}