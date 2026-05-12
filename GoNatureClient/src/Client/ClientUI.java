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

    @Override
    public void start(Stage primaryStage) throws Exception {

        FXMLLoader loader =
                new FXMLLoader(getClass().getResource("/GUI/Client.fxml"));

        Scene scene = new Scene(loader.load());

        controller = loader.getController();

        primaryStage.setTitle("Order Client");
        primaryStage.setScene(scene);
        primaryStage.show();

        client = new OrderClient("localhost", 5555);

        client.openConnection();
        System.out.println("Connected!");
    }

    public static void main(String[] args) {

        launch(args);
    }
}