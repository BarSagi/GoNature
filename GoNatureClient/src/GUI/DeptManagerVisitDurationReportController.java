package GUI;

import Client.ClientUI;
import Common.Message;
import Common.Visit;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.collections.FXCollections;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

public class DeptManagerVisitDurationReportController {

	public static DeptManagerVisitDurationReportController instance;

	@FXML
	private ComboBox<Integer> monthCombo;

	@FXML
	private ComboBox<Integer> yearCombo;

	@FXML
	private ComboBox<String> parkCombo;

	@FXML
	private BarChart<String, Number> barChart;

	@FXML
	private CategoryAxis xAxis;

	@FXML
	private NumberAxis yAxis;

	private final List<String> timeSlots = Arrays.asList("09:00-10:00", "10:01-11:00", "11:01-12:00", "12:01-13:00",
			"13:01-14:00", "14:01-15:00", "15:01-16:00");

	@FXML
	public void initialize() {
		instance = this;

		monthCombo.getItems().addAll(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12);

		for (int y = 2020; y <= 2030; y++) {
			yearCombo.getItems().add(y);
		}
		
		xAxis.setCategories(FXCollections.observableArrayList(timeSlots));

		Message msg = new Message("GET_ALL_PARKS", null);
		try {
			ClientUI.client.sendToServer(msg);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@FXML
	void generateReport(ActionEvent event) {
		Integer month = monthCombo.getValue();
		Integer year = yearCombo.getValue();
		String park = parkCombo.getValue();

		if (month == null || year == null || park == null) {
			System.out.println("Please select park, month and year");
			return;
		}

		ArrayList<Object> data = new ArrayList<>();
		data.add(park);
		data.add(month);
		data.add(year);

		Message msg = new Message("GET_VISIT_DURATION_REPORT", data);
		try {
			ClientUI.client.sendToServer(msg);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings("unchecked")
	public void showReport(List<Visit> visits) {
		if (visits == null)
			return;

		Platform.runLater(() -> {

			barChart.setAnimated(false);
			barChart.getData().clear();

			XYChart.Series<String, Number> regularSeries = new XYChart.Series<>();
			regularSeries.setName("Regular Groups");

			XYChart.Series<String, Number> organizedSeries = new XYChart.Series<>();
			organizedSeries.setName("Organized Groups");

			Map<String, List<Double>> regularMap = initMap();
			Map<String, List<Double>> organizedMap = initMap();

			for (Visit v : visits) {

				if (v.getEntryTime() == null || v.getExitTime() == null)
					continue;

				LocalDateTime entry = v.getEntryTime().toLocalDateTime();
				LocalDateTime exit = v.getExitTime().toLocalDateTime();
				LocalDateTime closingTime = entry.toLocalDate().atTime(17, 0);

				if (exit.isAfter(closingTime)) {
					exit = closingTime;
				}
				long minutes = Duration.between(entry, exit).toMinutes();
				if (minutes <= 0)
					continue;

				double duration = minutes / 60.0;

				String slot = getTimeSlot(entry);
				if (slot == null)
					continue;

				if ("OrganizedGroup".equalsIgnoreCase(v.getOrderType())) {
					organizedMap.get(slot).add(duration);
				} else {
					regularMap.get(slot).add(duration);
				}
			}

			for (String slot : timeSlots) {

				regularSeries.getData().add(new XYChart.Data<>(slot, average(regularMap.get(slot))));

				organizedSeries.getData().add(new XYChart.Data<>(slot, average(organizedMap.get(slot))));
			}

			barChart.getData().addAll(regularSeries, organizedSeries);

			barChart.applyCss();
			barChart.layout();

			addLabelsInsideBars(regularSeries);
			addLabelsInsideBars(organizedSeries);

			yAxis.setAutoRanging(true);
		});
	}

	private void addLabelsInsideBars(XYChart.Series<String, Number> series) {

		for (XYChart.Data<String, Number> data : series.getData()) {

			Node node = data.getNode();
			if (!(node instanceof StackPane))
				continue;

			StackPane bar = (StackPane) node;

			double value = data.getYValue().doubleValue();
			if (value <= 0)
				continue;

			int hours = (int) value;
			int minutes = (int) Math.round((value - hours) * 60);

			String labelText = (hours == 0) ? minutes + "m"
					: (minutes == 0) ? hours + "h" : hours + "h " + minutes + "m";

			Text textNode = new Text(labelText);
			textNode.setStyle("-fx-font-size: 11px; -fx-font-weight: bold; -fx-fill: white;");
			textNode.setMouseTransparent(true);

			bar.getChildren().add(textNode);

			StackPane.setAlignment(textNode, Pos.TOP_CENTER);
			StackPane.setMargin(textNode, new Insets(5, 0, 0, 0));
		}
	}

	private Map<String, List<Double>> initMap() {
		Map<String, List<Double>> map = new LinkedHashMap<>();
		for (String slot : timeSlots) {
			map.put(slot, new ArrayList<>());
		}
		return map;
	}

	private String getTimeSlot(LocalDateTime entry) {
		int minutes = entry.getHour() * 60 + entry.getMinute();

		if (minutes >= 540 && minutes <= 600)
			return "09:00-10:00";
		if (minutes >= 601 && minutes <= 660)
			return "10:01-11:00";
		if (minutes >= 661 && minutes <= 720)
			return "11:01-12:00";
		if (minutes >= 721 && minutes <= 780)
			return "12:01-13:00";
		if (minutes >= 781 && minutes <= 840)
			return "13:01-14:00";
		if (minutes >= 841 && minutes <= 900)
			return "14:01-15:00";
		if (minutes >= 901 && minutes <= 960)
			return "15:01-16:00";

		return null;
	}

	private double average(List<Double> list) {
		if (list == null || list.isEmpty())
			return 0;
		return list.stream().mapToDouble(Double::doubleValue).average().orElse(0);
	}

	public void loadParks(List<String> parks) {
		Platform.runLater(() -> {
			parkCombo.getItems().clear();
			parkCombo.getItems().addAll(parks);
		});
	}
}