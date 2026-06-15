package GUI;

import Common.Message;
import Client.ClientUI;
import Client.GoNatureClient;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.GridPane;
import javafx.scene.shape.Rectangle;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class DeptManagerCancellationReportPanelController {

    @FXML
    private ComboBox<String> parkCombo;

    @FXML
    private ComboBox<Integer> monthCombo;

    @FXML
    private ComboBox<Integer> yearCombo;

    @FXML
    private GridPane heatMapGrid;

    public void initialize() {

        // חודשים
        for (int i = 1; i <= 12; i++) {
            monthCombo.getItems().add(i);
        }

        // שנים
        for (int i = 2020; i <= 2030; i++) {
            yearCombo.getItems().add(i);
        }

        loadParks();
    }

    private void loadParks() {
        try {
            Message msg = new Message("GET_ALL_PARKS", new ArrayList<>());
            ClientUI.client.sendToServer(msg);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    void generateReport(ActionEvent event) {

        String park = parkCombo.getValue();
        Integer month = monthCombo.getValue();
        Integer year = yearCombo.getValue();

        if (park == null || month == null || year == null) {
            System.out.println("Missing input");
            return;
        }

        ArrayList<Object> data = new ArrayList<>();
        data.add(park);
        data.add(month);
        data.add(year);

        Message msg = new Message("GET_CANCELLATION_REPORT", data);

        try {
            ClientUI.client.sendToServer(msg);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showReport(ArrayList<Common.CancellationReportData> report) {

        Platform.runLater(() -> {

            heatMapGrid.getChildren().clear();

            if (report == null || report.isEmpty()) return;

            Map<Integer, Double> dayMap = new HashMap<>();

            int maxDay = 0;

            for (Common.CancellationReportData r : report) {
                dayMap.put(r.getDayOfMonth(), r.getValue());
                maxDay = Math.max(maxDay, r.getDayOfMonth());
            }

            for (int day = 1; day <= maxDay; day++) {

                double value = dayMap.getOrDefault(day, 0.0);

                Rectangle cell = new Rectangle(45, 45);
                cell.setFill(getColor(value));
                cell.setArcWidth(10);
                cell.setArcHeight(10);

                int col = (day - 1) % 7;
                int row = (day - 1) / 7;

                heatMapGrid.add(cell, col, row);
            }
        });
    }

    private Color getColor(double v) {

        if (v == 0) return Color.LIGHTGREEN;
        if (v < 2) return Color.YELLOWGREEN;
        if (v < 5) return Color.YELLOW;
        if (v < 10) return Color.ORANGE;
        return Color.RED;
    }
}