package server;

import database.DBController;
import servergui.ServerUI;

import javax.swing.SwingUtilities;

public class ServerMain {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                ServerUI serverUI = new ServerUI();
                serverUI.setVisible(true);

                DBController dbController = new DBController();
                dbController.connect();
                serverUI.log("DB status: CONNECTED");

                GoNatureServer server = new GoNatureServer(5555, dbController, serverUI);
                server.listen();
                serverUI.log("Server started on port 5555");

            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}