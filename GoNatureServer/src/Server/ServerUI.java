package Server;

import javafx.application.Application;
import javafx.stage.Stage;

import GUI.ServerPortFrameController;

public class ServerUI extends Application {

	public static void main(String args[]) throws Exception {
		launch(args);
	} // end main

	@Override
	public void start(Stage primaryStage) throws Exception {
		ServerPortFrameController aFrame = new ServerPortFrameController(); // create a frame to input port number

		aFrame.start(primaryStage);
	}

	public static void runServer(String p) {
		int port = 0; // port to listen on

		try {
			port = Integer.parseInt(p); // Set port to 5555

		} catch (Exception e) {
			System.out.println("ERROR - port parsing!");
			e.printStackTrace();
		}

		EchoServer sv = new EchoServer(port);

		try {
			sv.listen(); // Start listening for connections
		} catch (Exception ex) {
			System.out.println("ERROR - Could not listen for clients!");
		}
	}

}
