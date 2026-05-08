package client;

import clientgui.ClientUI;

import javax.swing.SwingUtilities;

public class ClientMain {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            ClientUI clientUI = new ClientUI();
            clientUI.setVisible(true);
        });
    }
}