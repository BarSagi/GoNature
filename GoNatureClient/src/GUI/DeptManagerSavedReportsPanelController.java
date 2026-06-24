package GUI;

import Client.ClientUI;
import Common.Message;
import Common.ReportImage;
import javafx.application.Platform;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import java.util.List;

/**
 * Controller for the saved reports panel in the department manager interface.
 * Manages the display of generated reports in a table and allows viewing
 * reports by selecting them from the list.
 */
public class DeptManagerSavedReportsPanelController {

	/**
	 * Static instance of this controller for external access.
	 */
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

	/**
	 * Observable list to hold the report image entities for the table view.
	 */
	private final ObservableList<ReportImage> reportsList = FXCollections.observableArrayList();

	/**
	 * Initializes the controller, sets up cell value factories, and configures the
	 * selection listener to open a preview window.
	 */
	@FXML
	public void initialize() {

		instance = this;

		typeColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getReportType()));
		monthColumn.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getMonth()).asObject());
		yearColumn.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getYear()).asObject());
		dateColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getCreatedAt()));

		reportsTable.setItems(reportsList);

		// Selection listener: opens preview window upon row selection
		reportsTable.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
			if (newVal != null) {
				openImageWindow(newVal);
			}
		});

		loadReports();
	}

	/**
	 * Refreshes the reports table by clearing the current selection and reloading
	 * data from the server.
	 *
	 * @param event The action event triggered by the refresh button.
	 */
	@FXML
	void refreshReports(ActionEvent event) {
		System.out.println("Refreshing reports table...");

		reportsTable.getSelectionModel().clearSelection();

		loadReports();
	}

	/**
	 * Fetches the list of saved reports from the server.
	 */
	private void loadReports() {
		Message msg = new Message("GET_ALL_REPORTS", null);

		try {
			ClientUI.send(msg);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Updates the table view with the list of reports received from the server.
	 *
	 * @param reports The list of reports to display.
	 */
	public void setReports(List<ReportImage> reports) {
		Platform.runLater(() -> {
			reportsList.clear();
			reportsList.addAll(reports);
		});
	}

	/**
	 * Opens a new stage to preview the selected report image.
	 *
	 * @param report The report image entity to display.
	 */
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