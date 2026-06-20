package Strategy;

import Common.Message;
import GUI.ParkWorkerCreateCasualVisitController;
import javafx.application.Platform;

public class CasualVisitResultStrategy implements MessageStrategy {

	@Override
	public void execute(Message message) {
		// The server sends a boolean indicating if the registration succeeded
		boolean isSuccess = (boolean) message.getData();

		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				if (ParkWorkerCreateCasualVisitController.instance != null) {
					if (isSuccess) {
						ParkWorkerCreateCasualVisitController.instance.handleRegistrationResult(true, null);
					} else {
						ParkWorkerCreateCasualVisitController.instance.handleRegistrationResult(false,
								"Registration failed.\nThe park is currently at full capacity.");
					}
				}
			}
		});
	}
}