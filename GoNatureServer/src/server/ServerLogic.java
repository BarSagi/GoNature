package server;

import database.DBController;
import ocsf.server.ConnectionToClient;
import servergui.ServerUI;
import java.io.IOException;
import java.util.ArrayList;

/*Handles the server-side business logic*/
public class ServerLogic {

    private DBController dbController;
    private ServerUI serverUI;

    public ServerLogic(DBController dbController, ServerUI serverUI) {
        this.dbController = dbController;
        this.serverUI = serverUI;
    }

    public void handleMessage(ArrayList<String> request, ConnectionToClient client) {
        if (request == null || request.isEmpty()) {
            sendError(client, "Empty request received.");
            return;
        }

        String action = request.get(0);

        try {
            switch (action) {
                case "GET_ORDER":
                    handleGetOrder(request, client);
                    break;

                case "UPDATE_ORDER":
                    handleUpdateOrder(request, client);
                    break;

                default:
                    sendError(client, "Unknown request.");
                    break;
            }
        } catch (Exception e) {
            sendError(client, "Server error: " + e.getMessage());
        }
    }

    private void handleGetOrder(ArrayList<String> request, ConnectionToClient client) throws Exception {
        int orderNumber = Integer.parseInt(request.get(1));

        ArrayList<String> result = dbController.getOrderByNumber(orderNumber);

        if (result == null) {
            sendError(client, "Order not found.");
            return;
        }

        client.sendToClient(result);
        serverUI.log("Order " + orderNumber + " sent to client.");
    }

    private void handleUpdateOrder(ArrayList<String> request, ConnectionToClient client) throws Exception {
        int orderNumber = Integer.parseInt(request.get(1));
        String orderDate = request.get(2);
        int numberOfVisitors = Integer.parseInt(request.get(3));

        boolean updated = dbController.updateOrder(orderNumber, orderDate, numberOfVisitors);

        if (updated) {
            ArrayList<String> response = new ArrayList<>();
            response.add("UPDATE_RESULT");
            response.add("Order updated successfully.");
            client.sendToClient(response);
            serverUI.log("Order " + orderNumber + " updated.");
        } else {
            sendError(client, "Update failed. Order not found.");
        }
    }

    private void sendError(ConnectionToClient client, String text) {
        try {
            ArrayList<String> error = new ArrayList<>();
            error.add("ERROR");
            error.add(text);
            client.sendToClient(error);
            serverUI.log("Error sent: " + text);
        } catch (IOException e) {
            serverUI.log("Failed sending error to client.");
        }
    }
}