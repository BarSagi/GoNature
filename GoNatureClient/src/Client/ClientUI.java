package Client;

import Common.Message;
import Entity.*;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import GUI.ConnectionController;
import javafx.application.Application;
import javafx.application.Platform;
//import GUI.ClientController;

// client will run this program
public class ClientUI extends Application {

	public static ConnectionController connectionController;

	// public static OrderClient aaaclient;
	public static GoNatureClient client;

	private static Stage mainStage;

	public static volatile boolean uiReady = false;

	public static String visitorID;

	public static void main(String[] args) {
		launch(); // call start method
	}

	@Override
	public void start(Stage primaryStage) {
		try {
			// load the graphical file
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/GUI/Connection.fxml"));

			Scene scene = new Scene(loader.load()); // create a new scene

			connectionController = loader.getController(); // get the class that runs the FXML

			primaryStage.setTitle("Connect to Server");
			primaryStage.setScene(scene);
			primaryStage.show();
			mainStage = primaryStage; // save the stage for later use

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void startClient(String ip, int port) throws Exception {
		uiReady = false;

		if (client != null) {
			try {
				client.closeConnection();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		// try to connect
		client = new GoNatureClient(ip, port);
		client.openConnection();

		// if connection failed
		if (!client.isConnected()) {
			throw new Exception("Connection failed");
		}

		// load the UI
		try {
			FXMLLoader loader = new FXMLLoader(ClientUI.class.getResource("/GUI/LoginRoute.fxml"));

			Scene scene = new Scene(loader.load());

			// this has to be changed later to fit somehow
			// clientController = loader.getController();

			uiReady = true;

			Platform.runLater(new Runnable() {

				@Override
				public void run() {

					mainStage.setTitle("Order Client");
					mainStage.setScene(scene);
				}
			});
		} catch (Exception e) {
			System.out.println("Error loading LoginRoute.fxml");
			e.printStackTrace();
		}
	}

	/**
	 * A generic method to switch screens in the application.
	 * 
	 * @param fxmlPath The path to the FXML file
	 * @param title    The title to display at the top of the window
	 */
	public static void changeScreen(String fxmlPath, String title) {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				try {
					FXMLLoader loader = new FXMLLoader(ClientUI.class.getResource(fxmlPath));
					Scene scene = new Scene(loader.load());

					mainStage.setTitle(title);
					mainStage.setScene(scene);

				} catch (Exception e) {
					System.out.println("Error loading screen: " + fxmlPath);
					e.printStackTrace();
				}
			}
		});
	}

	public static void disconnect() {
		if (client != null) {
			client.disconnectClient();
		}
	}

	@Override
	public void stop() {
		try {
			if (client != null && client.isConnected()) {
				client.sendToServer(new Message("DISCONNECT", null));
				client.closeConnection();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		System.out.println("Client application stopped");
	}

}