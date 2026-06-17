package GUI;

import Common.Message;
import Common.UsageReportData;
import Client.ClientUI;
import Client.GoNatureClient;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.ArrayList;

public class ParkManagerUsageReportsPanelController {

    public static ParkManagerUsageReportsPanelController instance;

    @FXML
    private ComboBox<Integer> monthCombo;

    @FXML
    private ComboBox<Integer> yearCombo;

    @FXML
    private GridPane heatmapGrid;

    @FXML
    private VBox legendBox;

    public void initialize() {

        instance = this;

        for (int m = 1; m <= 12; m++) {
            monthCombo.getItems().add(m);
        }

        for (int y = 2020; y <= 2030; y++) {
            yearCombo.getItems().add(y);
        }

        initLegend();
    }

    private void initLegend() {

        legendBox.getChildren().clear();

        HBox legend = new HBox(15);
        legend.setAlignment(Pos.CENTER);

        Label greenBox = new Label("  ");
        greenBox.setStyle("-fx-background-color: #2ecc71; -fx-min-width: 20; -fx-min-height: 20;");

        Label greenText = new Label("Not Full");

        Label redBox = new Label("  ");
        redBox.setStyle("-fx-background-color: #e74c3c; -fx-min-width: 20; -fx-min-height: 20;");

        Label redText = new Label("Full");

        legend.getChildren().addAll(
                greenBox, greenText,
                redBox, redText
        );

        legendBox.getChildren().add(legend);
    }

    @FXML
    void generateReport(ActionEvent event) {

        String park = GoNatureClient.currentEmployee.getAffiliation();

        Integer month = monthCombo.getValue();
        Integer year = yearCombo.getValue();

        ArrayList<Object> data = new ArrayList<>();
        data.add(park);
        data.add(month);
        data.add(year);

        Message msg = new Message("GET_USAGE_REPORT", data);

        try {
            ClientUI.client.sendToServer(msg);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showReport(ArrayList<UsageReportData> report) {

        Platform.runLater(() -> {

            heatmapGrid.getChildren().clear();

            for (UsageReportData d : report) {

                Label cell = new Label(String.valueOf(d.getDay()));

                cell.setMinSize(40, 40);
                cell.setAlignment(Pos.CENTER);

                if (d.isFull()) {
                    cell.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white;");
                }
                else {
                    cell.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white;");
                }

                int col = (d.getDay() - 1) % 7;
                int row = (d.getDay() - 1) / 7;

                heatmapGrid.add(cell, col, row);
            }
        });
    }
}