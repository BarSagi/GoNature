package Strategy;

import java.util.ArrayList;
import Common.Message;
import GUI.CreateOrderController;
import GUI.DeptManagerCancellationReportPanelController;
import GUI.DeptManagerVisitDurationReportController;
import GUI.ParkWorkerCreateOrderController;
import GUI.DeptManagerParkDashboardController;
import GUI_Visitor.NewVisitorOrderController;
import GUI_Visitor.VisitorOrdersScreenController;
import javafx.application.Platform;

public class AllParksStrategy implements MessageStrategy {

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