package GUI;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.AnchorPane;
import java.io.IOException;
import java.util.List;

import javafx.scene.control.Alert;

import Client.ClientUI;
import Common.Message;

public class DeptManagerParkDashboardController {

	public static DeptManagerParkDashboardController instance;

	@FXML
	private AnchorPane selectionPane;

	@FXML
	private AnchorPane dashboardWrapperPane;

	@FXML
	private AnchorPane dashboardContentArea;

	@FXML
	private ComboBox<String> parkCombo;

	private ParkDashboardController dashboardController;

	@FXML
	public void initialize() {
		instance = this;
		try {
			ClientUI.client.sendToServer(new Message("GET_ALL_PARKS", null));
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/GUI/ParkDashboard.fxml"));
			Node dashboardNode = loader.load();

			dashboardController = loader.getController();

			// Inject the loaded FXML into our designated container
			dashboardContentArea.getChildren().add(dashboardNode);

			// Anchor the node to fill the entire content area
			AnchorPane.setTopAnchor(dashboardNode, 0.0);
			AnchorPane.setBottomAnchor(dashboardNode, 0.0);
			AnchorPane.setLeftAnchor(dashboardNode, 0.0);
			AnchorPane.setRightAnchor(dashboardNode, 0.0);

		} catch (IOException e) {
			System.out.println("Error loading Park Dashboard FXML.");
			e.printStackTrace();
		}

		showSelectionPane();
	}

	// ==========================================
	// Action Event Handlers
	// ==========================================

	@FXML
	void onShowDashboardClick(ActionEvent event) {
		String selectedPark = parkCombo.getValue();

		// Prevent action if no park is selected
		if (selectedPark == null) {
			Alert alert = new Alert(Alert.AlertType.WARNING);
			alert.setTitle("Missing Selection");
			alert.setHeaderText(null);
			alert.setContentText("Please select a park from the list. ");
			alert.show();
			return;
		}

		// Tell the pre-loaded dashboard controller to fetch and display data for the
		// selected park
		if (dashboardController != null) {
			dashboardController.loadDashboardForPark(selectedPark);
		}

		// Switch to the dashboard panel
		showDashboardPane();
	}

	@FXML
	void onBackButtonClick(ActionEvent event) {
		// Switch back to the park selection panel
		showSelectionPane();
	}

	// ==========================================
	// Helper Methods for UI Switching
	// ==========================================

	private void showSelectionPane() {
		selectionPane.setVisible(true);
		selectionPane.toFront();
		dashboardWrapperPane.setVisible(false);
	}

	private void showDashboardPane() {
		dashboardWrapperPane.setVisible(true);
		dashboardWrapperPane.toFront();
		selectionPane.setVisible(false);
	}

	public void loadParks(List<String> parks) {
		Platform.runLater(() -> {
			parkCombo.getItems().clear();
			parkCombo.getItems().addAll(parks);
		});
	}
}