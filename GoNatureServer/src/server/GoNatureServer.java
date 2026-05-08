package server;

import common.Message;
import database.DBController;
import ocsf.server.AbstractServer;
import ocsf.server.ConnectionToClient;
import servergui.ServerUI;

import java.io.IOException;
import java.net.InetAddress;

public class GoNatureServer extends AbstractServer {

    private ServerLogic serverLogic;
    private ServerUI serverUI;

    public GoNatureServer(int port, DBController dbController, ServerUI serverUI) {
        super(port);
        this.serverUI = serverUI;
        this.serverLogic = new ServerLogic(dbController, serverUI);
    }

    @Override
    protected void clientConnected(ConnectionToClient client) {
        try {
            InetAddress address = client.getInetAddress();
            String key = address.getHostAddress() + ":" + client.getId();
            String value = "CONNECTED | IP: " + address.getHostAddress() + " | Host: " + address.getHostName();
            client.setInfo("ClientKey", key);
            serverUI.addClient(key, value);
            serverUI.log("Client connected: " + value);
        } catch (Exception e) {
            serverUI.log("Client connected.");
        }
    }

    @Override
    protected synchronized void clientDisconnected(ConnectionToClient client) {
        processClientDisconnection(client, "Disconnected normally");
    }

    @Override
    protected synchronized void clientException(ConnectionToClient client, Throwable exception) {
        processClientDisconnection(client, "Disconnected unexpectedly");
        try {
            client.close();
        } catch (IOException e) {
            serverUI.log("Error while closing client socket.");
        }
    }

    private void processClientDisconnection(ConnectionToClient client, String reason) {
        try {
            if (client.getInfo("Disconnected") == null) {
                client.setInfo("Disconnected", true);
                Object keyObj = client.getInfo("ClientKey");
                if (keyObj != null) {
                    serverUI.removeClient(keyObj.toString());
                }
                serverUI.log("Processing disconnection: " + reason);
            }
        } catch (Exception e) {
            serverUI.log("Disconnection handling failed.");
        }
    }

    @Override
    public void handleMessageFromClient(Object msg, ConnectionToClient client) {
        if (!(msg instanceof Message)) {
            serverUI.log("Received invalid object from client.");
            return;
        }

        Message message = (Message) msg;
        serverUI.log("Received: " + message);
        serverLogic.handleMessage(message, client);
    }
}