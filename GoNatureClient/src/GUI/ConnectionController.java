package GUI;

import Client.ClientUI;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import java.util.regex.Pattern;

/**
 * Controller for the connection screen. Manages user input for server IP and
 * port, and handles the connection logic.
 */
public class ConnectionController {

	/**
	 * Text field for IP address input.
	 */
	@FXML
	private TextField txtIP;

	/**
	 * Text field for server port input.
	 */
	@FXML
	private TextField txtPort;

	/**
	 * Label to show status/error messages to the user.
	 */
	@FXML
	private Label errorLabel;

	/**
	 * The Connect button, used to trigger the connection process.
	 */
	@FXML
	private Button btnConnect;

	/**
	 * Validates input and attempts to connect to the server. Disables the connect
	 * button during the attempt to prevent duplicate connection threads. * @param
	 * event The action event from the Connect button.
	 */
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

		String ipv4Pattern = "^((25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}"
				+ "(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$";
		String ipv6Pattern = "^[0-9a-fA-F:]+$";

		if (!Pattern.matches(ipv4Pattern, ip) && !Pattern.matches(ipv6Pattern, ip)) {
			errorLabel.setText("Invalid IP address");
			return;
		}

		// Disable the button to prevent multiple clicks while connecting
		btnConnect.setDisable(true);

		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					ClientUI.startClient(ip, port);
				} catch (Exception e) {
					// In case of error, re-enable the button on the UI thread
					Platform.runLater(new Runnable() {
						@Override
						public void run() {
							btnConnect.setDisable(false);
							errorLabel.setText("Connection failed");
						}
					});
				}
			}
		}).start();
	}

	/**
	 * Exits the application. * @param event The action event from the Exit button.
	 */
	@FXML
	void exit(ActionEvent event) {
		System.exit(0);
	}

	/**
	 * Displays error messages in the GUI. * @param msg The error message string.
	 */
	public void showErrorInGUI(String msg) {
		errorLabel.setText(msg);
	}
}