package GUI;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import Server.ServerUI;

// controller class for the server port. this class handles port input
public class ServerPortFrameController {
	public static ServerPortFrameController instance;
	@FXML
	private TextField portxt;

	@FXML
	private Button btnDone;

	@FXML
	private Button btnExit;

	@FXML
	private Label errorLabel;

	private String getPort() {
		return portxt.getText();
	}

	// this method will handle the user pressing "done"
	@FXML
	public void done(ActionEvent event) {

		errorLabel.setText("");
		String portNumber = getPort();

		if (portNumber == null || portNumber.trim().isEmpty()) { // make sure the input is valid
			errorLabel.setText("You must enter a port number");
			return;
		}

		try {
			// start the server with the given port
			ServerUI.runServer(portNumber.trim());

		} catch (Exception e) {
			errorLabel.setText("Error starting server");
		}
	}

	// handles the exit button
	@FXML
	public void exit(ActionEvent event) {
		System.out.println("Exit Server");
		System.exit(0);
	}

	// start the window display
	public void start(Stage primaryStage) throws Exception {

		FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/ServerPort.fxml"));

		Parent root = loader.load();

		ServerPortFrameController controller = loader.getController();
		instance = controller;

		Scene scene = new Scene(root);

		primaryStage.setTitle("Server Port Setup");
		primaryStage.setScene(scene);
		primaryStage.show();
	}

	// this method will show errors regarding the server GUI
	public void showError(String msg) {
		errorLabel.setText(msg);
	}

	// this method will show the messages the server recieves in the server GUI
	public void log(String msg) {
		javafx.application.Platform.runLater(new Runnable() {
			@Override
			public void run() {
				errorLabel.setText(msg); // או append אם תשדרג ל-TextArea
			}
		});
	}
}