package client;

import clientgui.ClientUI;
import common.Message;
import common.MessageType;

import java.io.IOException;

public class ClientLogic {

    private ClientUI clientUI;
    private ClientConsole clientConsole;

    public ClientLogic(ClientUI clientUI) {
        this.clientUI = clientUI;
    }

    public void connect(String host, int port) {
        try {
            clientConsole = new ClientConsole(host, port);
            clientConsole.openConnection();
            clientUI.showMessage("Connected to server");
        } catch (IOException e) {
            clientUI.showMessage("Connection failed: " + e.getMessage());
        }
    }

    public void getOrder(int orderNumber) {
        try {
            Message msg = new Message(MessageType.GET_ORDER, orderNumber);
            clientConsole.sendToServer(msg);
        } catch (IOException e) {
            clientUI.showMessage("Failed to send request: " + e.getMessage());
        }
    }

}