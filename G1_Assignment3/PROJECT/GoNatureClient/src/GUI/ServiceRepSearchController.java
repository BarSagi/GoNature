package GUI;

import java.util.ArrayList;
import Client.ClientUI;
import Common.Message;
import Common.Order;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.geometry.Pos;
import javafx.geometry.Insets;

/**
 * Controller for the service representative's search panel. Enables searching
 * for orders or visitor records using identifiers and displays the search
 * results in a custom list view.
 */
public class ServiceRepSearchController {

	@FXML
	private TextField searchField;

	@FXML
	private Label searchErrorLabel;

	@FXML
	private ListView<Order> resultsListView;

	/**
	 * Static instance of this controller for external access.
	 */
	public static ServiceRepSearchController instance;

	/**
	 * Initializes the controller and sets up the custom list view cell factory.
	 */
	@FXML
	public void initialize() {
		instance = this;
		setupCustomListView();
	}

	/**
	 * Validates the search input and sends a request to the server to find records.
	 *
	 * @param event The action event triggered by the search button.
	 */
	@FXML
	void handleQuickSearch(ActionEvent event) {
		String searchInput = searchField.getText().trim();
		searchErrorLabel.setText(""); // Clear previous errors

		if (searchInput.isEmpty()) {
			searchErrorLabel.setText("Please enter an ID or Order Number.");
			return;
		}

		if (!searchInput.matches("\\d+")) {
			searchErrorLabel.setText("Please enter numbers only.");
			return;
		}

		try {
			searchErrorLabel.setStyle("-fx-text-fill: #2980b9;"); // Blue color
			searchErrorLabel.setText("Searching...");

			Message msg = new Message("QUICK_SEARCH_RECORD", searchInput);
			ClientUI.send(msg);

		} catch (Exception e) {
			searchErrorLabel.setStyle("-fx-text-fill: #e74c3c;"); // Red color
			searchErrorLabel.setText("Error connecting to server.");
			e.printStackTrace();
		}
	}

	/**
	 * Processes and displays the search results in the list view.
	 *
	 * @param searchResults The list of orders returned by the server.
	 */
	public void handleSearchResults(ArrayList<Order> searchResults) {
		if (searchResults == null || searchResults.isEmpty()) {
			resultsListView.setVisible(false);
			resultsListView.setManaged(false);

			searchErrorLabel.setStyle("-fx-text-fill: #e74c3c;"); // Red color
			searchErrorLabel.setText("No records found for this ID/Order Number.");
		} else {
			searchErrorLabel.setStyle("-fx-text-fill: #27ae60;"); // Green color
			searchErrorLabel.setText("Found " + searchResults.size() + " records.");

			resultsListView.setVisible(true);
			resultsListView.setManaged(true);

			resultsListView.getItems().clear();
			resultsListView.getItems().addAll(searchResults);
		}
	}

	/**
	 * Configures the custom cell factory to define how each order is rendered in
	 * the ListView.
	 */
	private void setupCustomListView() {
		resultsListView.setCellFactory(param -> new javafx.scene.control.ListCell<Order>() {
			@Override
			protected void updateItem(Order order, boolean empty) {
				super.updateItem(order, empty);

				if (empty || order == null) {
					setText(null);
					setGraphic(null);
					setStyle("-fx-background-color: transparent;");
				} else {
					Label idLabel = new Label("Order #" + order.getOrderId() + " | Park ID: " + order.getParkId());
					idLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: #2c3e50;");

					Label dateLabel = new Label("Date: " + order.getVisitDate() + " at " + order.getVisitTime());
					dateLabel.setStyle("-fx-text-fill: #7f8c8d; -fx-font-size: 13px;");

					VBox leftSection = new VBox(3, idLabel, dateLabel);
					leftSection.setAlignment(Pos.CENTER_LEFT);

					Label countLabel = new Label("Visitors: " + order.getVisitorCount());
					countLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 13px; -fx-text-fill: #34495e;");

					Label statusTag = new Label(order.getOrderStatus().toUpperCase());
					statusTag.setStyle(
							"-fx-background-color: #2980b9; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 11px; -fx-padding: 3 8 3 8; -fx-background-radius: 4;");

					VBox rightSection = new VBox(5, countLabel, statusTag);
					rightSection.setAlignment(Pos.CENTER_RIGHT);

					HBox mainLayout = new HBox(leftSection);
					HBox.setHgrow(leftSection, Priority.ALWAYS);
					mainLayout.getChildren().add(rightSection);

					mainLayout.setPadding(new Insets(10));
					mainLayout.setStyle(
							"-fx-background-color: white; -fx-border-color: #ecf0f1; -fx-border-width: 0 0 1px 0;");

					setGraphic(mainLayout);
				}
			}
		});
	}
}