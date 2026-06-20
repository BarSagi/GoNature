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

public class ServiceRepSearchController {
	
    @FXML
    private TextField searchField;

    @FXML
    private Label searchErrorLabel;
    
    @FXML
    private ListView<Order> resultsListView;
    
    public static ServiceRepSearchController instance;
    
    @FXML
    public void initialize() {
        instance = this;
        setupCustomListView(); 
    }
    
    @FXML
    void handleQuickSearch(ActionEvent event) {
        String searchInput = searchField.getText().trim();
        searchErrorLabel.setText(""); // Clear previous errors

        // Validation: Ensure the field is not empty
        if (searchInput.isEmpty()) {
            searchErrorLabel.setText("Please enter an ID or Order Number.");
            return;
        }

        // Validation: Ensure the input contains only numbers
        if (!searchInput.matches("\\d+")) {
            searchErrorLabel.setText("Please enter numbers only.");
            return;
        }

        try {
            // Provide visual feedback while searching
            searchErrorLabel.setStyle("-fx-text-fill: #2980b9;"); // Blue color
            searchErrorLabel.setText("Searching...");

            // Send the search request to the server
            // The server should check if it's an Order ID or Visitor ID and return the details
            Message msg = new Message("QUICK_SEARCH_RECORD", searchInput);
            ClientUI.client.sendToServer(msg);
            
        } catch (Exception e) {
            searchErrorLabel.setStyle("-fx-text-fill: #e74c3c;"); // Red color
            searchErrorLabel.setText("Error connecting to server.");
            e.printStackTrace();
        }
    }
    
    public void handleSearchResults(ArrayList<Order> searchResults) {
        if (searchResults == null || searchResults.isEmpty()) {
            // Hide the list completely and show error
            resultsListView.setVisible(false);
            resultsListView.setManaged(false); 
            
            searchErrorLabel.setStyle("-fx-text-fill: #e74c3c;"); // Red color
            searchErrorLabel.setText("No records found for this ID/Order Number.");
        } else {
            // Show success message
            searchErrorLabel.setStyle("-fx-text-fill: #27ae60;"); // Green color
            searchErrorLabel.setText("Found " + searchResults.size() + " records.");
            
            // Reveal the list right below the search bar and update its data
            resultsListView.setVisible(true);
            resultsListView.setManaged(true); 
            
            resultsListView.getItems().clear();
            resultsListView.getItems().addAll(searchResults);
        }
    }
    
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
                    // Left side: ID and Date
                    Label idLabel = new Label("Order #" + order.getOrderId() + " | Park ID: " + order.getParkId());
                    idLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: #2c3e50;");
                    
                    Label dateLabel = new Label("Date: " + order.getVisitDate() + " at " + order.getVisitTime());
                    dateLabel.setStyle("-fx-text-fill: #7f8c8d; -fx-font-size: 13px;");
                    
                    javafx.scene.layout.VBox leftSection = new javafx.scene.layout.VBox(3, idLabel, dateLabel);
                    leftSection.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

                    // Right side: Status and Visitors
                    Label countLabel = new Label("Visitors: " + order.getVisitorCount());
                    countLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 13px; -fx-text-fill: #34495e;");
                    
                    Label statusTag = new Label(order.getOrderStatus().toUpperCase());
                    statusTag.setStyle("-fx-background-color: #2980b9; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 11px; -fx-padding: 3 8 3 8; -fx-background-radius: 4;");
                    
                    javafx.scene.layout.VBox rightSection = new javafx.scene.layout.VBox(5, countLabel, statusTag);
                    rightSection.setAlignment(javafx.geometry.Pos.CENTER_RIGHT);

                    // Main layout
                    javafx.scene.layout.HBox mainLayout = new javafx.scene.layout.HBox(leftSection);
                    javafx.scene.layout.HBox.setHgrow(leftSection, javafx.scene.layout.Priority.ALWAYS); 
                    mainLayout.getChildren().add(rightSection);
                    
                    mainLayout.setPadding(new javafx.geometry.Insets(10));
                    mainLayout.setStyle("-fx-background-color: white; -fx-border-color: #ecf0f1; -fx-border-width: 0 0 1px 0;");

                    setGraphic(mainLayout);
                }
            }
        });
    }
}