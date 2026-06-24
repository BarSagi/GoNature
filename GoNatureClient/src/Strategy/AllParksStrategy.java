package Strategy;

import java.util.ArrayList;
import Common.Message;
import GUI.CreateOrderController;
import GUI.DeptManagerCancellationReportPanelController;
import GUI.DeptManagerVisitDurationReportController;
import GUI.NewVisitorOrderController;
import GUI.ParkWorkerCreateOrderController;
import GUI.VisitorOrdersScreenController;
import GUI.DeptManagerParkDashboardController;
import javafx.application.Platform;

/**
 * Handles a message containing the list of all parks in the system.
 * <p>
 * This strategy receives the park names from the server and loads them into
 * every relevant screen controller that is currently active.
 */
public class AllParksStrategy implements MessageStrategy {

	/**
	 * Executes the strategy for handling the list of all parks.
	 * <p>
	 * The message data is expected to contain an {@code ArrayList<String>}
	 * with the names of the parks. The park list is then passed to each
	 * relevant controller instance on the JavaFX application thread.
	 *
	 * @param msg the message received from the server containing the park list
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void execute(Message msg) {

		ArrayList<String> parks = (ArrayList<String>) msg.getData();

		Platform.runLater(() -> {
			if (DeptManagerVisitDurationReportController.instance != null) {
				DeptManagerVisitDurationReportController.instance.loadParks(parks);
			}
			if (DeptManagerParkDashboardController.instance != null) {
				DeptManagerParkDashboardController.instance.loadParks(parks);
			}
			if (DeptManagerCancellationReportPanelController.instance != null) {
				DeptManagerCancellationReportPanelController.instance.loadParks(parks);
			}
			if (ParkWorkerCreateOrderController.instance != null) {
				ParkWorkerCreateOrderController.instance.loadParks(parks);
			}
			if (CreateOrderController.instance != null) {
				CreateOrderController.instance.loadParks(parks);
			}
			if (NewVisitorOrderController.instance != null) {
				NewVisitorOrderController.instance.loadParks(parks);
			}
			if (VisitorOrdersScreenController.instance != null) {
				VisitorOrdersScreenController.instance.loadParks(parks);
			}
		});
	}
}