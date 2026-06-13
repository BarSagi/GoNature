package Strategy;

import java.util.ArrayList;

import Common.Message;
import GUI.DeptManagerVisitDurationReportController;
import javafx.application.Platform;

public class AllParksStrategy implements MessageStrategy {

	@SuppressWarnings("unchecked")
	@Override
	public void execute(Message msg) {

		ArrayList<String> parks = (ArrayList<String>) msg.getData();

		if (DeptManagerVisitDurationReportController.instance != null) {

			Platform.runLater(() -> {
				DeptManagerVisitDurationReportController.instance.loadParks(parks);
			});
		}
	}
}
