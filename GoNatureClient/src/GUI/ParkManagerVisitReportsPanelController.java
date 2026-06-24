package GUI;

import Client.ClientUI;
import Client.GoNatureClient;
import Common.Message;
import Common.ReportImage;
import Common.VisitReportData;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.SnapshotParameters;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.VBox;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

/**
 * Controller for the park manager's visit reports panel. Handles the generation
 * of visit reports based on selected criteria and visualizes the data using a
 * pie chart.
 */
public class ParkManagerVisitReportsPanelController {

	/**
	 * Static instance of this controller for external access.
	 */
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

	@FXML
	private Label noDataLabel;

	@FXML
	private Button saveReportBtn;

	/**
	 * The currently displayed visit report data.
	 */
	private VisitReportData currentReport;

	/**
	 * Initializes the controller and populates the date selection combo boxes.
	 */
	@FXML
	public void initialize() {
		instance = this;

		monthCombo.getItems().addAll(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12);

		for (int y = 2020; y <= 2030; y++) {
			yearCombo.getItems().add(y);
		}
	}

	/**
	 * Processes the visit report data and updates the pie chart view.
	 *
	 * @param report The visit report data to display.
	 */
	public void showReport(VisitReportData report) {

		Platform.runLater(() -> {

			if (report == null || (report.getIndividualVisitors() == 0 && report.getGroupVisitors() == 0)) {
				currentReport = null;

				pieChart.getData().clear();
				totalLabel.setText("");

				noDataLabel.setVisible(true);
				noDataLabel.setManaged(true);

				pieChart.setVisible(false);
				pieChart.setManaged(false);

				saveReportBtn.setVisible(true);
				saveReportBtn.setManaged(true);

				return;
			}

			noDataLabel.setVisible(false);
			noDataLabel.setManaged(false);

			pieChart.setVisible(true);
			pieChart.setManaged(true);

			currentReport = report;

			PieChart.Data individual = new PieChart.Data("Regular Groups", report.getIndividualVisitors());
			PieChart.Data group = new PieChart.Data("Organized Groups", report.getGroupVisitors());

			pieChart.getData().setAll(individual, group);

			pieChart.setLabelsVisible(true);
			pieChart.setLegendVisible(true);

			int individualCount = report.getIndividualVisitors();
			int groupCount = report.getGroupVisitors();
			int total = individualCount + groupCount;

			totalLabel.setText("Total visitors: " + total + " | Regular Groups: " + individualCount
					+ " | Organized Groups: " + groupCount);

			Platform.runLater(() -> {

				if (individual.getNode() != null) {
					individual.getNode().setStyle("-fx-pie-color: #3498db;");
				}

				if (group.getNode() != null) {
					group.getNode().setStyle("-fx-pie-color: #e67e22;");
				}

				pieChart.requestLayout();
			});
		});
	}

	/**
	 * Requests the visit report from the server based on current criteria.
	 *
	 * @param event The action event.
	 */
	@FXML
	void generateReport(ActionEvent event) {
		String park = GoNatureClient.currentEmployee.getAffiliation();

		Integer month = monthCombo.getValue();
		Integer year = yearCombo.getValue();

		ArrayList<Object> data = new ArrayList<>();
		data.add(park);
		data.add(month);
		data.add(year);

		Message msg = new Message("GET_VISIT_REPORT", data);

		try {
			ClientUI.send(msg);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Captures a snapshot of the report container as a byte array image.
	 *
	 * @return The image byte array, or null if capture fails.
	 */
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

	/**
	 * Captures the current report image and saves it to the server.
	 *
	 * @param event The action event.
	 */
	@FXML
	void saveReport(ActionEvent event) {

		if (currentReport == null ||
			(currentReport.getIndividualVisitors() == 0 && currentReport.getGroupVisitors() == 0)) {

			Platform.runLater(() -> {
				Alert alert = new Alert(Alert.AlertType.WARNING);
				alert.setTitle("Save Report");
				alert.setHeaderText(null);
				alert.setContentText("Cannot save an empty report.");
				alert.showAndWait();
			});

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
			ClientUI.send(msg);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}