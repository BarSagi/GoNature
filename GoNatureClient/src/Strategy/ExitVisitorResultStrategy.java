package Strategy;

import Common.Message;
import GUI_Visitor.VisitorOrdersScreenController; // Make sure this import is correct!
import GUI.ParkWorkerExitVisitorController; // Make sure this import is correct!
import javafx.application.Platform;
import javafx.scene.control.Alert;

public class ExitVisitorResultStrategy implements MessageStrategy {

	@Override
	public void execute(Message message) {
		boolean success = (boolean) message.getData();

		Platform.runLater(() -> {

			// ==========================================================
			// SCENARIO 1: The Park Worker is using the Exit Screen
			// ==========================================================
			if (ParkWorkerExitVisitorController.instance != null) {
				if (success) {
					ParkWorkerExitVisitorController.instance.showStatus("Visitor exited successfully.");
				} else {
					ParkWorkerExitVisitorController.instance
							.showStatus("Failed to exit visitor. Make sure the ID was typed correctly.");
				}
			}

			// ==========================================================
			// SCENARIO 2: The Visitor is using the Orders Screen
			// ==========================================================
			if (VisitorOrdersScreenController.instance != null) {
				if (success) {
					Alert alert = new Alert(Alert.AlertType.INFORMATION);
					alert.setTitle("Exit Successful");
					alert.setHeaderText(null);
					alert.setContentText("Thank you for visiting! Your exit has been recorded successfully.");
					alert.showAndWait();

					// Refresh the table so the order status changes to "Fulfilled"!
					VisitorOrdersScreenController.instance.refreshOrders(null);
				} else {
					Alert alert = new Alert(Alert.AlertType.ERROR);
					alert.setTitle("Exit Failed");
					alert.setHeaderText(null);
					alert.setContentText("There was an error recording your exit. Please verify your order status.");
					alert.showAndWait();
				}
			}

		});
	}
}