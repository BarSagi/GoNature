package GUI;

import Client.ClientUI;
import Client.GoNatureClient;
import Common.Message;
import Common.ReportImage;
import Common.UsageReportData;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.WritableImage;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

/**
 * Controller for the park manager's usage reports panel. Handles criteria
 * selection, report generation, and heatmap visualization of park usage data.
 */
public class ParkManagerUsageReportsPanelController {

	/**
	 * Static instance of this controller for external access.
	 */
	public static ParkManagerUsageReportsPanelController instance;

	@FXML
	private ComboBox<Integer> monthCombo;

	@FXML
	private ComboBox<Integer> yearCombo;

	@FXML
	private GridPane heatmapGrid;

	@FXML
	private VBox legendBox;

	@FXML
	private Button saveReportBtn;

	/**
	 * Initializes the controller, populates combo boxes, and initializes the
	 * legend.
	 */
	@FXML
	public void initialize() {

		instance = this;

		for (int m = 1; m <= 12; m++) {
			monthCombo.getItems().add(m);
		}

		for (int y = 2020; y <= 2030; y++) {
			yearCombo.getItems().add(y);
		}

		initLegend();

		saveReportBtn.setVisible(false);
		saveReportBtn.setManaged(false);
	}

	/**
	 * Initializes the legend view to explain the heatmap colors.
	 */
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
		legend.getChildren().addAll(greenBox, greenText, redBox, redText);

		legendBox.getChildren().add(legend);
	}

	/**
	 * Requests the usage report from the server based on the selected park, month,
	 * and year.
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

		Message msg = new Message("GET_USAGE_REPORT", data);

		try {
			ClientUI.send(msg);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Displays the usage report data in the heatmap grid.
	 *
	 * @param report The list of usage report data points.
	 */
	public void showReport(ArrayList<UsageReportData> report) {
		Platform.runLater(() -> {

			heatmapGrid.getChildren().clear();

			boolean hasData = report != null && !report.isEmpty();

			if (!hasData) {
				saveReportBtn.setVisible(false);
				saveReportBtn.setManaged(false);
				return;
			}

			saveReportBtn.setVisible(true);
			saveReportBtn.setManaged(true);

			for (UsageReportData d : report) {
				Label cell = new Label(String.valueOf(d.getDay()));
				cell.setMinSize(40, 40);
				cell.setAlignment(Pos.CENTER);
				if (d.isFull()) {
					cell.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white;");
				} else {
					cell.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white;");
				}
				int col = (d.getDay() - 1) % 7;
				int row = (d.getDay() - 1) / 7;
				heatmapGrid.add(cell, col, row);
			}
		});
	}

	/**
	 * Captures the current heatmap grid as a PNG image byte array for saving.
	 *
	 * @return The image as a byte array.
	 */
	public byte[] captureUsageReportImage() {
		WritableImage snapshot = heatmapGrid.snapshot(null, null);
		BufferedImage bufferedImage = SwingFXUtils.fromFXImage(snapshot, null);

		try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
			ImageIO.write(bufferedImage, "png", out);
			return out.toByteArray();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Saves the generated usage report to the server.
	 *
	 * @param event The action event.
	 */
	@FXML
	void saveReport(ActionEvent event) {
		byte[] image = captureUsageReportImage();
		String park = GoNatureClient.currentEmployee.getAffiliation();
		int month = monthCombo.getValue();
		int year = yearCombo.getValue();

		ReportImage report = new ReportImage("USAGE_REPORT", park, month, year, image);

		Message msg = new Message("SAVE_REPORT", report);

		try {
			ClientUI.send(msg);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}