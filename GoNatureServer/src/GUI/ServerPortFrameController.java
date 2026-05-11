package GUI;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import Server.ServerUI;

public class ServerPortFrameController {

	@FXML
	private TextField portxt;

	@FXML
	private Button btnDone;

	@FXML
	private Button btnExit;

	private String getPort() {
		return portxt.getText();
	}

	@FXML
	public void Done(ActionEvent event) {

		String portNumber = getPort();

		if (portNumber == null || portNumber.trim().isEmpty()) {

			System.out.println("You must enter a port number");
			return;
		}

		try {
			ServerUI.runServer(portNumber);

			((Node) event.getSource()).getScene().getWindow().hide();

		} catch (NumberFormatException e) {

			System.out.println("Port must be a number");
		}
	}

	@FXML
	public void Exit(ActionEvent event) {

		System.out.println("Exit Server");

		System.exit(0);
	}

	public void start(Stage primaryStage) throws Exception {

		Parent root = FXMLLoader.load(getClass().getResource("/gui/ServerPort.fxml"));

		Scene scene = new Scene(root);

		primaryStage.setTitle("Server Port Setup");
		primaryStage.setScene(scene);

		primaryStage.show();
	}
}