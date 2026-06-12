package GUI;

import java.util.ArrayList;

import Client.ClientUI;
import Client.GoNatureClient;
import Common.Message;
import Common.VisitReportData;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;

public class ParkManagerVisitReportsPanelController {

	public static ParkManagerVisitReportsPanelController instance;

	@FXML
	private Label totalLabel;

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

	@FXML
	public void initialize() {
		instance = this;
		monthCombo.getItems().addAll(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12);

		for (int y = 2020; y <= 2030; y++) {
			yearCombo.getItems().add(y);
		}
	}

	public void showReport(VisitReportData report) {

		if (report == null)
			return;

		Platform.runLater(() -> {

			barChart.setAnimated(false);
			barChart.getData().clear();

			xAxis.setCategories(javafx.collections.FXCollections.observableArrayList("Individual", "Group"));

			XYChart.Series<String, Number> series = new XYChart.Series<>();
			series.setName("Visitors");

			XYChart.Data<String, Number> individual = new XYChart.Data<>("Individual", report.getIndividualVisitors());
			XYChart.Data<String, Number> group = new XYChart.Data<>("Group", report.getGroupVisitors());

			series.getData().add(individual);
			series.getData().add(group);

			barChart.getData().add(series);

			barChart.applyCss();
			barChart.layout();

			int max = Math.max(report.getIndividualVisitors(), report.getGroupVisitors());

			int upper = roundUp(max);

			yAxis.setAutoRanging(false);
			yAxis.setLowerBound(0);
			yAxis.setUpperBound(upper);
			yAxis.setTickUnit(Math.max(1, upper / 10.0));

			Platform.runLater(() -> {
				addLabel(individual);
				addLabel(group);
				totalLabel.setText("Total visitors: " + (report.getIndividualVisitors() + report.getGroupVisitors()));
			});
		});
	}

	@FXML
	void generateReport(ActionEvent event) {

		int month = monthCombo.getValue();
		int year = yearCombo.getValue();

		String park = GoNatureClient.currentEmployee.getAffiliation();

		ArrayList<Object> data = new ArrayList<>();
		data.add(park);
		data.add(month);
		data.add(year);

		Message msg = new Message("GET_VISIT_REPORT", data);

		try {
			ClientUI.client.sendToServer(msg);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void addLabel(XYChart.Data<String, Number> data) {

		javafx.scene.Node node = data.getNode();
		if (node == null)
			return;

		javafx.scene.control.Label label = new javafx.scene.control.Label(String.valueOf(data.getYValue()));

		label.setStyle("-fx-font-size: 12px; -fx-font-weight: bold;");

		javafx.scene.layout.StackPane stackPane = (javafx.scene.layout.StackPane) node;

		stackPane.getChildren().add(label);

		javafx.scene.layout.StackPane.setAlignment(label, javafx.geometry.Pos.TOP_CENTER);
	}

	private int roundUp(int value) {
		return ((value / 10) + 1) * 10;
	}

}