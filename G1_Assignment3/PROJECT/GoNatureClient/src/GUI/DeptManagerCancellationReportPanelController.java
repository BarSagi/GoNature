package GUI;

import Common.Message;
import Common.CancellationReportData;
import Client.ClientUI;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controller for the cancellation report display in the department manager
 * panel.
 */
public class DeptManagerCancellationReportPanelController {

	public static DeptManagerCancellationReportPanelController instance;

	@FXML
	private ComboBox<String> parkCombo;

	@FXML
	private ComboBox<Integer> monthCombo;

	@FXML
	private ComboBox<Integer> yearCombo;

	@FXML
	private GridPane heatMapGrid;

	@FXML
	private HBox legendContainer;

	/**
	 * Initializes combo boxes and UI elements.
	 */
	@FXML
	public void initialize() {
		instance = this;

		for (int i = 1; i <= 12; i++) {
			monthCombo.getItems().add(i);
		}

		for (int i = 2020; i <= 2030; i++) {
			yearCombo.getItems().add(i);
		}

		loadParks();
		drawLegend(0.0, 0.0);
	}

	/**
	 * Fetches available parks from the server.
	 */
	private void loadParks() {
		try {
			Message msg = new Message("GET_ALL_PARKS", new ArrayList<>());
			ClientUI.send(msg);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Generates the cancellation report based on user selection. * @param event The
	 * action event.
	 */
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
			ClientUI.send(msg);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Displays the report data in the heatmap grid. * @param report The report data
	 * to display.
	 */
	public void showReport(ArrayList<CancellationReportData> report) {
		Platform.runLater(() -> {
			heatMapGrid.getChildren().clear();

			if (report == null || report.isEmpty()) {
				heatMapGrid.getChildren().clear();

				Label emptyLabel = new Label("No data for selected month");
				emptyLabel.setFont(Font.font("System", FontWeight.BOLD, 16));
				emptyLabel.setTextFill(Color.web("#e74c3c"));

				StackPane container = new StackPane(emptyLabel);
				container.setAlignment(Pos.CENTER);

				heatMapGrid.add(container, 0, 1, 7, 1);

				drawLegend(0.0, 0.0);
				return;
			}

			Map<Integer, Double> dayMap = new HashMap<>();
			double totalCancellations = 0;
			double maxCancellations = 0;

			for (CancellationReportData r : report) {
				dayMap.put(r.getDayOfMonth(), r.getValue());
				totalCancellations += r.getValue();

				if (r.getValue() > maxCancellations) {
					maxCancellations = r.getValue();
				}
			}

			String[] weekDays = { "Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat" };
			for (int i = 0; i < weekDays.length; i++) {
				Label dayHeader = new Label(weekDays[i]);
				dayHeader.setFont(Font.font("System", FontWeight.BOLD, 12));
				dayHeader.setTextFill(Color.web("#34495e"));
				dayHeader.setAlignment(Pos.CENTER);
				dayHeader.setPrefWidth(60);
				heatMapGrid.add(dayHeader, i, 0);
			}

			int selectedMonth = monthCombo.getValue() != null ? monthCombo.getValue() : 1;
			int selectedYear = yearCombo.getValue() != null ? yearCombo.getValue() : 2026;
			YearMonth yearMonthObject = YearMonth.of(selectedYear, selectedMonth);
			int daysInMonth = yearMonthObject.lengthOfMonth();

			double monthlyAverage = totalCancellations / daysInMonth;
			drawLegend(monthlyAverage, maxCancellations);

			double cellSize = 60.0;

			for (int day = 1; day <= daysInMonth; day++) {
				double value = dayMap.getOrDefault(day, 0.0);

				Rectangle cell = new Rectangle(cellSize, cellSize);
				cell.setFill(getColor(value, maxCancellations));
				cell.setArcWidth(10);
				cell.setArcHeight(10);

				Label dayLabel = new Label(String.valueOf(day));
				dayLabel.setFont(Font.font("System", FontWeight.BOLD, 14));
				dayLabel.setTextFill(Color.BLACK);

				StackPane cellStack = new StackPane();
				cellStack.getChildren().addAll(cell, dayLabel);
				cellStack.setAlignment(Pos.CENTER);

				int col = (day - 1) % 7;
				int row = ((day - 1) / 7) + 1;

				heatMapGrid.add(cellStack, col, row);
			}
		});
	}

	/**
	 * Returns color based on cancellation percentage.
	 */
	private Color getColor(double value, double max) {
		if (value == 0 || max == 0)
			return Color.LIGHTGREEN;

		double percentage = value / max;

		if (percentage <= 0.25)
			return Color.YELLOWGREEN;

		if (percentage <= 0.50)
			return Color.YELLOW;

		if (percentage <= 0.75)
			return Color.ORANGE;

		return Color.RED;
	}

	/**
	 * Draws the heatmap legend.
	 */
	private void drawLegend(double average, double max) {
		if (legendContainer == null)
			return;

		legendContainer.getChildren().clear();
		legendContainer.setSpacing(15);
		legendContainer.setAlignment(Pos.CENTER);

		String[] labels;
		if (max <= 4) {
			labels = new String[] { "0", "1", "2", "3", "4+" };
		} else {
			labels = new String[] { "0", String.format("1 - %.0f", max * 0.25),
					String.format("%.0f - %.0f", (max * 0.25) + 1, max * 0.50),
					String.format("%.0f - %.0f", (max * 0.50) + 1, max * 0.75),
					String.format("%.0f+", (max * 0.75) + 1) };
		}
		Color[] colors = { Color.LIGHTGREEN, Color.YELLOWGREEN, Color.YELLOW, Color.ORANGE, Color.RED };

		for (int i = 0; i < colors.length; i++) {
			Rectangle rect = new Rectangle(20, 20);
			rect.setFill(colors[i]);
			rect.setArcWidth(5);
			rect.setArcHeight(5);

			Label label = new Label(labels[i]);
			label.setFont(Font.font("System", 12));

			HBox legendItem = new HBox(5);
			legendItem.setAlignment(Pos.CENTER_LEFT);
			legendItem.getChildren().addAll(rect, label);

			legendContainer.getChildren().add(legendItem);
		}

		Separator separator = new Separator();
		separator.setOrientation(Orientation.VERTICAL);
		separator.setPrefHeight(20);
		legendContainer.getChildren().add(separator);

		Label avgLabel = new Label(String.format("Monthly Daily Avg: %.2f | Max: %.0f", average, max));
		avgLabel.setFont(Font.font("System", FontWeight.BOLD, 13));
		avgLabel.setTextFill(Color.web("#2c3e50"));

		legendContainer.getChildren().add(avgLabel);
	}

	/**
	 * Populates the park selection box.
	 */
	public void loadParks(List<String> parks) {
		Platform.runLater(() -> {
			parkCombo.getItems().clear();
			parkCombo.getItems().addAll(parks);
		});
	}
}