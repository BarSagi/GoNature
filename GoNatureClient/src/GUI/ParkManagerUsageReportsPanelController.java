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
	private ComboBox<Integer> monthCombo;

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

		for (int m = 1; m <= 12; m++) {
			monthCombo.getItems().add(m);
		}

		for (int y = 2020; y <= 2030; y++) {
			yearCombo.getItems().add(y);
		}
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

		if (report == null || report.isEmpty())
			return;

		Platform.runLater(() -> {

			barChart.setAnimated(false);
			barChart.getData().clear();

			XYChart.Series<String, Number> series = new XYChart.Series<>();
			series.setName("Average Capacity");

			Map<Integer, Double> map = new HashMap<>();

			for (UsageReportData r : report) {
				map.put(r.getDayOfWeek(), r.getAverageCapacity());
			}

			for (int i = 1; i <= 7; i++) {

				String dayLabel = dayName(i);
				double value = map.getOrDefault(i, 0.0);

				series.getData().add(new XYChart.Data<>(dayLabel, value));
			}

			barChart.getData().add(series);

			Platform.runLater(() -> {
				for (XYChart.Data<String, Number> data : series.getData()) {

					javafx.scene.control.Label label = new javafx.scene.control.Label(
							String.format("%.1f", data.getYValue()));

					label.setStyle("-fx-font-size: 11px; -fx-font-weight: bold;");
					label.setMouseTransparent(true);

					if (data.getNode() instanceof javafx.scene.layout.StackPane stack) {
						stack.getChildren().add(label);
						javafx.scene.layout.StackPane.setAlignment(label, javafx.geometry.Pos.TOP_CENTER);
					}
				}
			});

			yAxis.setAutoRanging(false);
			yAxis.setLowerBound(0);
			yAxis.setUpperBound(120);
			yAxis.setTickUnit(10);
		});
	}

	private String dayName(int d) {
		return switch (d) {
		case 1 -> "Sun";
		case 2 -> "Mon";
		case 3 -> "Tue";
		case 4 -> "Wed";
		case 5 -> "Thu";
		case 6 -> "Fri";
		case 7 -> "Sat";
		default -> "";
		};
	}
}