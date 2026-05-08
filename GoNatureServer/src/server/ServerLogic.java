package server;

import common.Message;
import common.MessageType;
import common.Order;
import common.UpdateOrderRequest;
import database.DBController;
import ocsf.server.ConnectionToClient;
import servergui.ServerUI;

import java.io.IOException;
import java.sql.Date;
import java.sql.SQLException;

public class ServerLogic {

    private DBController dbController;
    private ServerUI serverUI;

    public ServerLogic(DBController dbController, ServerUI serverUI) {
        this.dbController = dbController;
        this.serverUI = serverUI;
    }

    public void handleMessage(Message message, ConnectionToClient client) {
        try {
            switch (message.getType()) {
                case GET_ORDER:
                    handleGetOrder(message, client);
                    break;

                case UPDATE_ORDER:
                    handleUpdateOrder(message, client);
                    break;

                default:
                    sendError(client, "Unsupported message type.");
                    break;
            }
        } catch (Exception e) {
            sendError(client, "Server error: " + e.getMessage());
        }
    }

    private void handleGetOrder(Message message, ConnectionToClient client) throws SQLException, IOException {
        if (!(message.getData() instanceof Integer)) {
            sendError(client, "GET_ORDER requires Integer order number.");
            return;
        }

        int orderNumber = (Integer) message.getData();
        Order order = dbController.getOrderByNumber(orderNumber);

        if (order == null) {
            sendError(client, "Order not found.");
            return;
        }

        client.sendToClient(new Message(MessageType.ORDER_RESULT, order));
        serverUI.log("Order " + orderNumber + " sent to client.");
    }

    private void handleUpdateOrder(Message message, ConnectionToClient client) throws SQLException, IOException {
        if (!(message.getData() instanceof UpdateOrderRequest)) {
            sendError(client, "UPDATE_ORDER requires UpdateOrderRequest.");
            return;
        }

        UpdateOrderRequest request = (UpdateOrderRequest) message.getData();

        boolean updated = dbController.updateOrder(
                request.getOrderNumber(),
                Date.valueOf(request.getOrderDate()),
                request.getNumberOfVisitors()
        );

        if (updated) {
            client.sendToClient(new Message(MessageType.UPDATE_RESULT, "Order updated successfully."));
            serverUI.log("Order " + request.getOrderNumber() + " updated.");
        } else {
            sendError(client, "Update failed. Order not found.");
        }
    }

    private void sendError(ConnectionToClient client, String text) {
        try {
            client.sendToClient(new Message(MessageType.ERROR, text));
            serverUI.log("Error sent: " + text);
        } catch (IOException e) {
            serverUI.log("Failed sending error to client.");
        }
    }
}