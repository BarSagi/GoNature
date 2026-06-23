package GUI;

import Client.ClientUI;
import Common.Message;
import Common.ReportImage;
import javafx.application.Platform;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;

import java.util.List;

public class DeptManagerSavedReportsPanelController {

	public static DeptManagerSavedReportsPanelController instance;

	@FXML
	private TableView<ReportImage> reportsTable;

	@FXML
	private TableColumn<ReportImage, String> typeColumn;

	@FXML
	private TableColumn<ReportImage, Integer> monthColumn;

	@FXML
	private TableColumn<ReportImage, Integer> yearColumn;

	@FXML
	private TableColumn<ReportImage, String> dateColumn;

	private final ObservableList<ReportImage> reportsList = FXCollections.observableArrayList();

	@FXML
	public void initialize() {

		instance = this;

		typeColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getReportType()));

		monthColumn.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getMonth()).asObject());

		yearColumn.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getYear()).asObject());

		dateColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getCreatedAt()));

		reportsTable.setItems(reportsList);

		// לחיצה על שורה → פתיחת חלון חדש
		reportsTable.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
			if (newVal != null) {
				openImageWindow(newVal);
			}
		});

		loadReports();
	}

	private void loadReports() {
		Message msg = new Message("GET_ALL_REPORTS", null);

		try {
			ClientUI.client.sendToServer(msg);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void setReports(List<ReportImage> reports) {
		Platform.runLater(() -> {
			reportsList.clear();
			reportsList.addAll(reports);
		});
	}

	private void openImageWindow(ReportImage report) {

		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/GUI/ReportPreview.fxml"));
			Parent root = loader.load();

			ReportPreviewController controller = loader.getController();
			controller.setImage(report.getImage());

			Stage stage = new Stage();
			stage.setTitle("Report Preview");
			stage.setScene(new Scene(root));
			stage.show();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}