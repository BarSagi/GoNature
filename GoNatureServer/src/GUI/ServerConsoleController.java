package GUI;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import Server.EchoServer;
import Server.ServerUI;

/**
 * Controller for the server console screen.
 * Handles displaying server logs, connected clients, errors, and exiting the server.
 */
public class ServerConsoleController {

	/**
	 * Singleton instance that allows the backend server to access this controller.
	 */
	public static ServerConsoleController instance;

	@FXML
	private TextArea logArea;

	/**
	 * Initializes the controller when the FXML screen is loaded.
	 * Sets the current controller instance.
	 */
	@FXML
	public void initialize() {
		instance = this; // Initialize the instance as soon as the screen loads
	}

	/**
	 * Displays information about the currently connected clients.
	 *
	 * @param event the action event triggered by the button click
	 */
	@FXML
	void showClients(ActionEvent event) {
		EchoServer server = ServerUI.server;
		if (server == null) {
			logArea.appendText("Server not running\n");
			return;
		}
		String info = server.getConnectedClientInfo();
		logArea.appendText(info);
	}

	/**
	 * Exits the server application.
	 *
	 * @param event the action event triggered by the button click
	 */
	@FXML
	public void exit(ActionEvent event) {
		System.out.println("Exit Server");
		System.exit(0);
	}

	/**
	 * Displays an error message in the server console.
	 *
	 * @param msg the error message to display
	 */
	public void showError(String msg) {
		logArea.appendText(msg + "\n");
	}

	/**
	 * Adds a log message to the server console.
	 * Uses the JavaFX application thread to safely update the UI.
	 *
	 * @param msg the log message to display
	 */
	public void log(String msg) {
		Platform.runLater(() -> logArea.appendText(msg + "\n"));
	}
}