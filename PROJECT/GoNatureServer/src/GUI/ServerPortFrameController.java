package GUI;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import Server.ServerUI;

/**
 * Controller for the server port setup screen. Handles receiving the port
 * number from the user, starting the server, and switching to the server
 * console screen.
 */
public class ServerPortFrameController {

	@FXML
	private TextField portxt;

	@FXML
	private Label errorLabel; // Displays errors before the console loads

	/**
	 * Opens the server port setup screen.
	 *
	 * @param primaryStage the main stage of the application
	 * @throws Exception if the FXML file cannot be loaded
	 */
	public void start(Stage primaryStage) throws Exception {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/GUI/ServerPort.fxml"));
		Parent root = loader.load();
		Scene scene = new Scene(root);
		primaryStage.setTitle("Server Port Setup");
		primaryStage.setScene(scene);
		primaryStage.show();
	}

	/**
	 * Handles the Done button click. Validates the port number, opens the server
	 * console screen, and starts the server using the entered port.
	 *
	 * @param event the action event triggered by the Done button
	 */
	@FXML
	public void done(ActionEvent event) {
		String portNumber = portxt.getText();

		if (portNumber == null || portNumber.trim().isEmpty()) {
			errorLabel.setText("You must enter a port number!");
			return;
		}

		try {
			// 1. Swap the screen to the Server Console first
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/GUI/ServerConsole.fxml"));
			Parent root = loader.load();
			Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
			Scene scene = new Scene(root);
			stage.setScene(scene);
			stage.setTitle("GoNature Server Console");
			stage.show();

			stage.centerOnScreen();

			// 2. Start the server (Logs will now go to the new console screen)
			ServerUI.runServer(portNumber.trim());

		} catch (Exception e) {
			errorLabel.setText("Error starting server!");
			e.printStackTrace();
		}
	}

	/**
	 * Exits the application.
	 *
	 * @param event the action event triggered by the Exit button
	 */
	@FXML
	public void exit(ActionEvent event) {
		System.exit(0);
	}
}