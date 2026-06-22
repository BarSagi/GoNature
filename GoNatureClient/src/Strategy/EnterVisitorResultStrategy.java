package Strategy;

import Common.Message;
import GUI.ParkWorkerEnterVisitorController;
import javafx.application.Platform;

public class EnterVisitorResultStrategy implements MessageStrategy {

	@Override
	public void execute(Message message) {
		String resultStatus = (String) message.getData();

		Platform.runLater(() -> {
			if (ParkWorkerEnterVisitorController.instance != null) {
				if (resultStatus.equals("Success") || resultStatus.startsWith("Success_Pay_") || resultStatus.equals("PaymentUpdated")) {
					ParkWorkerEnterVisitorController.instance.showStatus(resultStatus);
				} else {
					ParkWorkerEnterVisitorController.instance.showStatus(
							"Entry Denied!\nReason: " + resultStatus + "\n\n" + "Please verify the following:\n"
									+ "• The ID/Order number was entered correctly.\n"
									+ "• The order status is set to 'Approved'.\n"
									+ "• The current time is within 30 minutes of the scheduled visit.");
				}
			}
		});
	}
}