package Server;

import javafx.application.Application;
import javafx.stage.Stage;

import GUI.ServerPortFrameController;

public class ServerUI extends Application {
	public static ServerPortFrameController controller; // this will hold a reference to the gui
	public static void main(String args[]) throws Exception {
		launch(args);
	} // end main

	@Override
	public void start(Stage primaryStage) throws Exception {
		ServerPortFrameController aFrame = new ServerPortFrameController(); // create a frame to input port number
		controller = aFrame;
		aFrame.start(primaryStage);
	}

	public static void runServer(String p) {
		int port = 0; // port to listen on

		try {
			port = Integer.parseInt(p);

		} catch (Exception e) {
			ServerPortFrameController.instance.showError("ERROR - port parsing!");
			return;
		}

		EchoServer sv = new EchoServer(port);

		try {
			sv.listen(); // Start listening for connections
		} catch (Exception e) {
			ServerPortFrameController.instance.showError("ERROR - Could not listen for clients!");
			return;
		}
	}

}
