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


// controller class for the server port. this class handles port input
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

	// this method will handle the user pressing "done"
	@FXML
	public void Done(ActionEvent event) {

		String portNumber = getPort();

		if (portNumber == null || portNumber.trim().isEmpty()) { // make sure the input is valid

			System.out.println("You must enter a port number");
			return;
		}

		try {
			// start the server with the given port
			ServerUI.runServer(portNumber);
			// close the window
			((Node) event.getSource()).getScene().getWindow().hide();

		} catch (NumberFormatException e) {

			System.out.println("Port must be a number");
		}
	}
	// handles the exit button
	@FXML
	public void Exit(ActionEvent event) {

		System.out.println("Exit Server");

		System.exit(0);
	}
	// start the window display
	public void start(Stage primaryStage) throws Exception {

		Parent root = FXMLLoader.load(getClass().getResource("/gui/ServerPort.fxml"));

		Scene scene = new Scene(root);

		primaryStage.setTitle("Server Port Setup");
		primaryStage.setScene(scene);

		primaryStage.show();
	}
}