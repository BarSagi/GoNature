package servergui;

import javax.swing.*;
import java.awt.*;
import java.util.LinkedHashMap;
import java.util.Map;

public class ServerUI extends JFrame {
    private static final long serialVersionUID = 1L;

    private JTextArea logArea;
    private DefaultListModel<String> clientsModel;
    private Map<String, String> clientsMap;

    public ServerUI() {
        setTitle("GoNature Server");
        setSize(750, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        clientsMap = new LinkedHashMap<>();

        logArea = new JTextArea();
        logArea.setEditable(false);

        clientsModel = new DefaultListModel<>();
        JList<String> clientsList = new JList<>(clientsModel);

        JSplitPane splitPane = new JSplitPane(
                JSplitPane.HORIZONTAL_SPLIT,
                new JScrollPane(logArea),
                new JScrollPane(clientsList)
        );
        splitPane.setDividerLocation(500);

        add(splitPane, BorderLayout.CENTER);
    }

    public void log(String text) {
        SwingUtilities.invokeLater(() -> logArea.append(text + "\n"));
    }

    public void addClient(String key, String value) {
        SwingUtilities.invokeLater(() -> {
            clientsMap.put(key, value);
            refreshClientsList();
        });
    }

    public void removeClient(String key) {
        SwingUtilities.invokeLater(() -> {
            clientsMap.remove(key);
            refreshClientsList();
        });
    }

    private void refreshClientsList() {
        clientsModel.clear();
        for (String value : clientsMap.values()) {
            clientsModel.addElement(value);
        }
    }
}