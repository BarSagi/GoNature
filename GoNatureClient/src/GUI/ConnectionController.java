package GUI;

import Client.ClientUI;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class ConnectionController {

	@FXML
	private TextField txtIP;

	@FXML
	private TextField txtPort;

	@FXML
	private Label errorLabel;

	@FXML
	void connectToServer(ActionEvent event) {
		errorLabel.setText("");

		try {
			String ip = txtIP.getText();

			if (ip.trim().isEmpty() || txtPort.getText().trim().isEmpty()) {
				errorLabel.setText("Please enter IP and Port.");
				return;
			}

			int port = Integer.parseInt(txtPort.getText());
			ClientUI.startClient(ip, port);

		} catch (Exception e) {
			errorLabel.setText("Connection failed!");
			e.printStackTrace();
		}
	}

	@FXML
	void exit(ActionEvent event) {
		System.exit(0);
	}
}