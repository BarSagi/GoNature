package client;

import clientgui.ClientUI;

import java.io.IOException;
import java.util.ArrayList;

/*Handle the client-side ligic and communication with the server*/

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
    //get a request to get an order by its number
    public void getOrder(int orderNumber) {
        try {
        	ArrayList<String> msg = new ArrayList<>();
            msg.add("GET_ORDER");
            msg.add(String.valueOf(orderNumber));
            
            clientConsole.sendToServer(msg);
        } catch (IOException e) {
            clientUI.showMessage("Failed to send request: " + e.getMessage());
        }
    }
    //sends a request to update order date and number of visitors
    public void updateOrder(int orderNumber, String orderDate, int numberOfVisitors) {
        try {
            ArrayList<String> msg = new ArrayList<>();
            msg.add("UPDATE_ORDER");
            msg.add(String.valueOf(orderNumber));
            msg.add(orderDate);
            msg.add(String.valueOf(numberOfVisitors));
            
            clientConsole.sendToServer(msg);
        } catch (IOException e) {
            clientUI.showMessage("Failed to send update: " + e.getMessage());
        }
    }

}