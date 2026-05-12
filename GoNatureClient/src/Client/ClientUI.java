package Client;

import GUI.ClientController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

// client will run this program
public class ClientUI extends Application {

	public static ClientController controller;

	public static OrderClient client;

	private static Stage mainStage;

	@Override
	public void start(Stage primaryStage) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/GUI/Connection.fxml"));

			Scene scene = new Scene(loader.load());

			controller = loader.getController();

			primaryStage.setTitle("Connect to Server");
			primaryStage.setScene(scene);
			primaryStage.show();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void startClient(String ip, int port) {
		try {
			client = new OrderClient(ip, port);
			client.openConnection();
			System.out.println("Connected to " + ip + ":" + port);

			FXMLLoader loader = new FXMLLoader(ClientUI.class.getResource("/GUI/Client.fxml"));
			Scene scene = new Scene(loader.load());
			controller = loader.getController();

			mainStage.setTitle("Order Client");
			mainStage.setScene(scene);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		launch(args);
	}
}