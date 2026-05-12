package Client;

import GUI.ClientController;
import GUI.ConnectionController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

// client will run this program
public class ClientUI extends Application {

	public static ConnectionController connectionController;
	public static ClientController controller;

	public static OrderClient client;

	private static Stage mainStage;

	@Override
	public void start(Stage primaryStage) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/GUI/Connection.fxml"));

			Scene scene = new Scene(loader.load());

			connectionController = loader.getController();

			primaryStage.setTitle("Connect to Server");
			primaryStage.setScene(scene);
			primaryStage.show();
			mainStage = primaryStage;

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void startClient(String ip, int port) throws Exception {
		if (client != null) {
			try {
				client.closeConnection();
			} catch (Exception e) {}
		}
		client = new OrderClient(ip, port);
		client.openConnection();
		System.out.println("Connected to " + ip + ":" + port);

		FXMLLoader loader = new FXMLLoader(ClientUI.class.getResource("/GUI/Client.fxml"));
		Scene scene = new Scene(loader.load());
		controller = loader.getController();

		javafx.application.Platform.runLater(new Runnable() {

		    @Override
		    public void run() {

		        mainStage.setTitle("Order Client");
		        mainStage.setScene(scene);
		    }
		});
	}

	public static void main(String[] args) {
		launch(args);
	}
}