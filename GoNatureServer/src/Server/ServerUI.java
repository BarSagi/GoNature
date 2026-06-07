package Server;

import javafx.application.Application;
import javafx.stage.Stage;

import GUI.ServerPortFrameController;
import GUI.ServerConsoleController;

public class ServerUI extends Application {

	public static EchoServer server; // this will hold a reference to the server

	public static void main(String args[]) throws Exception {
		launch();
	} // end main

	@Override
	public void start(Stage primaryStage) throws Exception {
		ServerPortFrameController frame = new ServerPortFrameController(); // create a frame to input port number
		frame.start(primaryStage);
	}

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