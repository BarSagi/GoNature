package GUI;

import Client.ClientUI;
import Common.Message;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.util.ArrayList;

public class ParkWorkerExitVisitorController {

	public static ParkWorkerExitVisitorController instance;

	@FXML
	private TextField visitorIdField;

	@FXML
	private Label statusLabel;

	@FXML
	public void initialize() {
		instance = this;
		statusLabel.setText("");
	}

	@FXML
	void confirmExit(ActionEvent event) {
		String visitorId = visitorIdField.getText().trim();

		if (visitorId.isEmpty()) {
			statusLabel.setText("Please enter visitor ID.");
			return;
		}
		
		if (!visitorId.matches("\\d{9}")) {
			statusLabel.setText("Visitor ID must be exactly 9 digits.");
			return;
		}

		try {
			ArrayList<String> data = new ArrayList<>();
			data.add(visitorId);

			Message msg = new Message("EXIT_VISITOR", data);
			ClientUI.client.sendToServer(msg);

			statusLabel.setText("Exit request sent.");
		} catch (Exception e) {
			statusLabel.setText("Failed to send exit request.");
			e.printStackTrace();
		}
	}

	public void showStatus(String text) {
		statusLabel.setText(text);
	}
}