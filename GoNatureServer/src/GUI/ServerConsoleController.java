package GUI;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import Server.EchoServer;
import Server.ServerUI;

public class ServerConsoleController {

	// Singleton instance so the backend server can talk to this screen
	public static ServerConsoleController instance;

	@FXML
	private TextArea logArea;

	@FXML
	public void initialize() {
		instance = this; // Initialize the instance as soon as the screen loads
	}

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

	@FXML
	public void exit(ActionEvent event) {
		System.out.println("Exit Server");
		System.exit(0);
	}

	public void showError(String msg) {
		logArea.appendText(msg + "\n");
	}

	public void log(String msg) {
		Platform.runLater(() -> logArea.appendText(msg + "\n"));
	}
}