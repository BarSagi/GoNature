package GUI;

import java.util.ArrayList;

import Client.ClientUI;
import Client.GoNatureClient;
import Common.Message;
import Common.ReportImage;
import Common.VisitReportData;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.PieChart;
import javafx.scene.layout.VBox;

import javafx.scene.SnapshotParameters;
import javafx.scene.image.WritableImage;
import javafx.embed.swing.SwingFXUtils;

import javax.imageio.ImageIO;
import java.io.ByteArrayOutputStream;
import java.awt.image.BufferedImage;

public class ParkManagerVisitReportsPanelController {

	public static ParkManagerVisitReportsPanelController instance;
	
	@FXML
	private VBox reportContainer;

	@FXML
	private Label totalLabel;

	@FXML
	private ComboBox<Integer> monthCombo;

	@FXML
	private ComboBox<Integer> yearCombo;

	@FXML
	private PieChart pieChart;

	@FXML
	private CategoryAxis xAxis;

	@FXML
	private NumberAxis yAxis;

	private VisitReportData currentReport;

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

		currentReport = report;

		Platform.runLater(() -> {

			pieChart.getData().clear();

			PieChart.Data individual = new PieChart.Data("Regular Groups", report.getIndividualVisitors());

			PieChart.Data group = new PieChart.Data("Organized Groups", report.getGroupVisitors());

			pieChart.getData().addAll(individual, group);

			pieChart.setLabelsVisible(true);
			pieChart.setLegendVisible(true);

			int individualCount = report.getIndividualVisitors();
			int groupCount = report.getGroupVisitors();
			int total = individualCount + groupCount;

			totalLabel.setText("Total visitors: " + total + " | Regular Groups: " + individualCount
					+ " | Organized Groups: " + groupCount);

			individual.nodeProperty().addListener((obs, oldNode, newNode) -> {
				if (newNode != null) {
					newNode.setStyle("-fx-pie-color: #3498db;");
				}
			});

			group.nodeProperty().addListener((obs, oldNode, newNode) -> {
				if (newNode != null) {
					newNode.setStyle("-fx-pie-color: #e67e22;");
				}
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

	private byte[] captureReportImage() {
		try {
			WritableImage snapshot = reportContainer.snapshot(new SnapshotParameters(), null);

			BufferedImage bufferedImage = SwingFXUtils.fromFXImage(snapshot, null);

			ByteArrayOutputStream output = new ByteArrayOutputStream();
			ImageIO.write(bufferedImage, "png", output);

			return output.toByteArray();

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@FXML
	void saveReport(ActionEvent event) {

		if (currentReport == null) {
			System.out.println("No report to save");
			return;
		}

		byte[] imageBytes = captureReportImage();

		if (imageBytes == null) {
			System.out.println("Failed to capture report");
			return;
		}

		ReportImage report = new ReportImage("VISIT_REPORT", GoNatureClient.currentEmployee.getAffiliation(),
				monthCombo.getValue(), yearCombo.getValue(), imageBytes);

		Message msg = new Message("SAVE_REPORT", report);

		try {
			ClientUI.client.sendToServer(msg);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}