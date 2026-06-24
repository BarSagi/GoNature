package Server;

import javafx.application.Application;
import javafx.stage.Stage;

import GUI.ServerPortFrameController;
import GUI.ServerConsoleController;

/**
 * Main JavaFX application class for starting the server side of the system.
 * This class opens the server port screen and starts the server after a valid
 * port number is entered.
 */
public class ServerUI extends Application {

	public static EchoServer server; // this will hold a reference to the server

	/**
	 * Main method that launches the JavaFX application.
	 *
	 * @param args command line arguments
	 * @throws Exception if the application fails to launch
	 */
	public static void main(String args[]) throws Exception {
		launch();
	} // end main

	/**
	 * Starts the JavaFX server UI.
	 * Opens the screen where the user enters the server port number.
	 *
	 * @param primaryStage the main stage of the application
	 * @throws Exception if the port screen cannot be loaded
	 */
	@Override
	public void start(Stage primaryStage) throws Exception {
		ServerPortFrameController frame = new ServerPortFrameController(); // create a frame to input port number
		frame.start(primaryStage);
	}

	/**
	 * Starts the server using the given port number.
	 * If the port is invalid or the server cannot listen for clients,
	 * an error message is shown in the server console.
	 *
	 * @param p the port number as a string
	 */
	public static void runServer(String p) {
		int port = 0; // port to listen on

		try {
			port = Integer.parseInt(p);

		} catch (Exception e) {
			// FIXED: Changed to ServerConsoleController
			if (ServerConsoleController.instance != null) {
				ServerConsoleController.instance.showError("ERROR - port parsing!");
			}
			return;
		}

		server = new EchoServer(port);

		try {
			server.listen(); // Start listening for connections
		} catch (Exception e) {
			// FIXED: Changed to ServerConsoleController
			if (ServerConsoleController.instance != null) {
				ServerConsoleController.instance.showError("ERROR - Could not listen for clients!");
			}
			return;
		}
	}
}