package GUI;

import Common.Message;
import Common.UsageReportData;
import Client.ClientUI;
import Client.GoNatureClient;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.chart.*;
import javafx.scene.control.ComboBox;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ParkManagerUsageReportsPanelController {

    public static ParkManagerUsageReportsPanelController instance;

    @FXML
    private ComboBox<Integer> yearCombo;

    @FXML
    private BarChart<String, Number> barChart;

    @FXML
    private CategoryAxis xAxis;

    @FXML
    private NumberAxis yAxis;

    public void initialize() {

        instance = this;

        for (int y = 2020; y <= 2030; y++) {
            yearCombo.getItems().add(y);
        }
        yearCombo.getSelectionModel().select(0);
    }

    @FXML
    void generateReport(ActionEvent event) {

        String park = GoNatureClient.currentEmployee.getAffiliation();
        Integer year = yearCombo.getValue();

        if (year == null) {
            System.out.println("Please select a year!");
            return;
        }

        ArrayList<Object> data = new ArrayList<>();
        data.add(park);
        data.add(year);

        Message msg = new Message("GET_USAGE_REPORT", data);

        try {
            ClientUI.client.sendToServer(msg);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showReport(ArrayList<UsageReportData> report) {
        if (report == null) return;

        Platform.runLater(() -> {
            barChart.setAnimated(false);
            barChart.getData().clear();

            xAxis.setAutoRanging(true);

            XYChart.Series<String, Number> series = new XYChart.Series<>();
            series.setName("Under Capacity %");

            Map<Integer, Double> map = new HashMap<>();
            for (UsageReportData r : report) {
                map.put(r.getMonth(), r.getPercentUnderCapacity());
            }

            for (int i = 1; i <= 12; i++) {
                String monthLabel = monthName(i);
                double value = map.getOrDefault(i, 0.0);

                XYChart.Data<String, Number> dataPoint = new XYChart.Data<>(monthLabel, value);
                series.getData().add(dataPoint);

                dataPoint.nodeProperty().addListener((observable, oldNode, newNode) -> {
                    if (newNode != null) {
                        javafx.scene.control.Label label = 
                                new javafx.scene.control.Label(String.format("%.1f%%", dataPoint.getYValue().doubleValue()));
                        
                        label.setStyle("-fx-font-size: 11px; -fx-font-weight: bold; -fx-text-fill: black;");
                        label.setMinSize(javafx.scene.control.Label.USE_PREF_SIZE, javafx.scene.control.Label.USE_PREF_SIZE);
                        label.setTranslateY(-15); 
                        label.setMouseTransparent(true);

                        javafx.scene.layout.StackPane stack = (javafx.scene.layout.StackPane) newNode;
                        stack.getChildren().add(label);
                        
                        javafx.scene.layout.StackPane.setAlignment(label, javafx.geometry.Pos.TOP_CENTER);
                    }
                });
            }

            barChart.getData().add(series);

            yAxis.setAutoRanging(false);
            yAxis.setLowerBound(0);
            yAxis.setUpperBound(120);
            yAxis.setTickUnit(10);
        });
    }
    private String monthName(int m) {
        return switch (m) {
            case 1 -> "Jan";
            case 2 -> "Feb";
            case 3 -> "Mar";
            case 4 -> "Apr";
            case 5 -> "May";
            case 6 -> "Jun";
            case 7 -> "Jul";
            case 8 -> "Aug";
            case 9 -> "Sep";
            case 10 -> "Oct";
            case 11 -> "Nov";
            case 12 -> "Dec";
            default -> "";
        };
    }
}