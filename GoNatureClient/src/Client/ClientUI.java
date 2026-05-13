package Client;

import GUI.ClientController;
import GUI.ConnectionController;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

// client will run this program
public class ClientUI extends Application {

	public static ConnectionController connectionController;
	public static ClientController clientController;
	
	public static OrderClient client;

	private static Stage mainStage;
	
	public static volatile boolean uiReady = false;

	@Override
	public void start(Stage primaryStage) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/GUI/Connection.fxml")); // load the graphical file

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
	        } catch (Exception e) {}
	    }

	    // try to connect
	    client = new OrderClient(ip, port);
	    client.openConnection();

	    // if connection failed
	    if (!client.isConnected()) {
	        throw new Exception("Connection failed");
	    }

	    // load the UI
	    FXMLLoader loader = new FXMLLoader(ClientUI.class.getResource("/GUI/Client.fxml"));

	    Scene scene = new Scene(loader.load());

	    clientController = loader.getController();

	    uiReady = true;

	    Platform.runLater(new Runnable() {

	        @Override
	        public void run() {

	            mainStage.setTitle("Order Client");
	            mainStage.setScene(scene);
	        }
	    });
	}

	public static void main(String[] args) {
		launch(); // call start method
	}
}