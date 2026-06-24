package GUI;

import Client.ClientUI;
import Client.GoNatureClient;
import Common.Message;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.application.Platform;
import java.util.ArrayList;
import javafx.scene.control.Alert;

public class ParkWorkerEnterVisitorController {

	public static ParkWorkerEnterVisitorController instance;

	@FXML
	private TextField identifierField;

	@FXML
	private Label statusLabel;

	@FXML
	public void initialize() {
		instance = this;
		statusLabel.setText("");
	}

	@FXML
	void confirmEntry(ActionEvent event) {
		String identifier = identifierField.getText().trim();

		if (identifier.isEmpty()) {
			statusLabel.setText("Please enter Order ID, Visitor ID, or QR Code.");
			return;
		}

		if (!identifier.matches("^[A-Z0-9]{1,9}$")) {
			statusLabel.setText("Invalid input: Max 9 uppercase alphanumeric characters only.");
			statusLabel.setStyle("-fx-text-fill: red;");
			return;
		}

		try {
			ArrayList<String> data = new ArrayList<>();
			data.add(identifier);

			String currentParkId = GoNatureClient.currentEmployee.getAffiliation();
			data.add(currentParkId);

			Message msg = new Message("ENTER_VISITOR", data);
			ClientUI.client.sendToServer(msg);

			statusLabel.setText("Entry request sent to server...");
			statusLabel.setStyle("-fx-text-fill: blue;");
		} catch (Exception e) {
			statusLabel.setText("Failed to send entry request.");
			statusLabel.setStyle("-fx-text-fill: red;");
			e.printStackTrace();
		}
	}

	public void showStatus(String text) {
		Platform.runLater(() -> {
			if (text.startsWith("Success_Pay_")) {
				statusLabel.setStyle("-fx-text-fill: green;");
				statusLabel.setText("Visitor entered successfully! Payment required.");
				identifierField.clear();

				String[] parts = text.split("_");
				String priceStr = parts[2];
				String orderIdStr = parts[3];

				Alert alert = new Alert(Alert.AlertType.WARNING);
				alert.setTitle("Collection Required");
				alert.setHeaderText("Unpaid Order - Collect Payment Now");
				alert.setContentText("The visitor has not paid yet.\nTotal price to collect: " + priceStr + " NIS");
				alert.showAndWait();

				try {
					statusLabel.setText("Recording payment in database...");
					Message updatePaidMsg = new Message("UPDATE_ORDER_PAID", orderIdStr);
					ClientUI.client.sendToServer(updatePaidMsg);
				} catch (Exception e) {
					statusLabel.setText("Failed to send payment update to server.");
					statusLabel.setStyle("-fx-text-fill: red;");
					e.printStackTrace();
				}

			} else if (text.equals("Success")) {
				statusLabel.setStyle("-fx-text-fill: green;");
				statusLabel.setText("Visitor entered successfully! (Paid)");
				identifierField.clear();
				
			    Alert alert = new Alert(Alert.AlertType.INFORMATION);
			    alert.setTitle("Information Dialog");
			    alert.setHeaderText(null);
			    alert.setContentText("Client has already paid!");
			    alert.showAndWait();
			} else {
				statusLabel.setStyle("-fx-text-fill: red;");
				statusLabel.setText(text);
			}
		});
	}
}