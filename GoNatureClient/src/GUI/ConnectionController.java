package GUI;

import Client.ClientUI;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import java.util.regex.Pattern;

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
		String ip = txtIP.getText();
		String portNumber = txtPort.getText().trim();
		int port;
		try {
			if (ip.trim().isEmpty() || txtPort.getText().trim().isEmpty()) {
				errorLabel.setText("Please enter IP and Port.");
				return;
			}

			port = Integer.parseInt(portNumber);

		} catch (Exception e) {
			errorLabel.setText("Port number failure");
			return;
		}
		// handle the correct ip configuration
		String ipv4Pattern = "^((25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}"
				+ "(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$";

		String ipv6Pattern = "^[0-9a-fA-F:]+$";

		// check if the ip is formatted correctly
		if (!Pattern.matches(ipv4Pattern, ip) && !Pattern.matches(ipv6Pattern, ip)) {
			errorLabel.setText("Invalid IP address");
			return;
		}
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					ClientUI.startClient(ip, port);
				} catch (Exception e) {
					Platform.runLater(new Runnable() {
						@Override
						public void run() {
							errorLabel.setText("Connection failed");
						}
					});
				}
			}
		}).start();
	}

	@FXML
	void exit(ActionEvent event) {
		System.exit(0);
	}

	public void showErrorInGUI(String msg) {
		errorLabel.setText(msg);
	}
}