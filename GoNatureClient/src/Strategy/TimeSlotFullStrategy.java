package Strategy;

import Common.Message;
import javafx.application.Platform;
import javafx.scene.control.Alert;

public class TimeSlotFullStrategy implements MessageStrategy {

	@Override
	public void execute(Message message) {

		Platform.runLater(() -> {
			Alert alert = new Alert(Alert.AlertType.WARNING);
			alert.setTitle("Time Slot Unavailable");
			alert.setHeaderText("Park Capacity Reached");
			alert.setContentText(
					"We're sorry, this time slot is completely booked.\n\nWould you like to enter the Waiting List or choose another date? (Waiting list feature coming soon!)");
			alert.showAndWait();
		});

	}
}