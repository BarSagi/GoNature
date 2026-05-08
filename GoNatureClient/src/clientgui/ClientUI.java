package clientgui;

import client.ClientLogic;

import javax.swing.*;
import java.awt.*;

public class ClientUI extends JFrame {
    private static final long serialVersionUID = 1L;

    private JTextField hostField;
    private JTextField portField;
    private JTextField orderNumberField;
    private JTextField orderDateField;
    private JTextField visitorsField;
    private JTextArea statusArea;

    private JButton connectButton;
    private JButton loadButton;
    private JButton updateButton;

    private ClientLogic clientLogic;

    public ClientUI() {
        setTitle("GoNature Client");
        setSize(600, 450);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        clientLogic = new ClientLogic(this);

        JPanel topPanel = new JPanel(new GridLayout(6, 2, 5, 5));

        hostField = new JTextField("localhost");
        portField = new JTextField("5555");
        orderNumberField = new JTextField();
        orderDateField = new JTextField();
        visitorsField = new JTextField();

        connectButton = new JButton("Connect");
        loadButton = new JButton("Load Order");
        updateButton = new JButton("Update Order");

        topPanel.add(new JLabel("Host:"));
        topPanel.add(hostField);
        topPanel.add(new JLabel("Port:"));
        topPanel.add(portField);
        topPanel.add(new JLabel("Order Number:"));
        topPanel.add(orderNumberField);
        topPanel.add(new JLabel("Order Date (yyyy-mm-dd):"));
        topPanel.add(orderDateField);
        topPanel.add(new JLabel("Number Of Visitors:"));
        topPanel.add(visitorsField);
        topPanel.add(connectButton);
        topPanel.add(loadButton);

        statusArea = new JTextArea();
        statusArea.setEditable(false);

        add(topPanel, BorderLayout.NORTH);
        add(new JScrollPane(statusArea), BorderLayout.CENTER);
        add(updateButton, BorderLayout.SOUTH);

        connectButton.addActionListener(e ->
                clientLogic.connect(hostField.getText().trim(), Integer.parseInt(portField.getText().trim()))
        );

        loadButton.addActionListener(e ->
                clientLogic.getOrder(Integer.parseInt(orderNumberField.getText().trim()))
        );

        updateButton.addActionListener(e ->
                showMessage("Update button pressed - update logic will be added next.")
        );
    }

    public void showMessage(String text) {
        statusArea.append(text + "\n");
    }
}