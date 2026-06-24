package Strategy;

import Common.Message;
import Client.ClientUI;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import java.util.ArrayList;
import java.util.Optional;

/**
 * Handles the server response when the selected time slot is full.
 * <p>
 * This strategy displays a warning alert with alternative time slots if they
 * exist, and allows the user to join the waiting list or choose another date.
 */
public class TimeSlotFullStrategy implements MessageStrategy {

	/**
	 * Executes the strategy for handling a full time slot response.
	 * <p>
	 * The message data is expected to contain an {@code ArrayList<Object>}
	 * where the first element contains alternative time slots, and the second
	 * element contains the original order data.
	 *
	 * @param message the message received from the server containing alternatives and order data
	 */
	@Override
	public void execute(Message message) {

		// 1. Extract the data array sent from the server
		@SuppressWarnings("unchecked")
		ArrayList<Object> data = (ArrayList<Object>) message.getData();
		String alternatives = (String) data.get(0);

		@SuppressWarnings("unchecked")
		ArrayList<String> originalOrderData = (ArrayList<String>) data.get(1);

		Platform.runLater(() -> {
			Alert alert = new Alert(Alert.AlertType.WARNING);
			alert.setTitle("Time Slot Unavailable");
			alert.setHeaderText("Park Capacity Reached");

			// 2. Build the dynamic message
			StringBuilder contentText = new StringBuilder();
			contentText.append("We're sorry, this time slot is completely booked.\n\n");

			if (alternatives != null && !alternatives.trim().isEmpty()) {
				contentText.append("However, we have room at the following alternative times on this date:\n");
				contentText.append(alternatives).append("\n\n");
			} else {
				contentText.append("Unfortunately, there are no other available time slots on this date.\n\n");
			}

			contentText.append("Would you like to enter the Waiting List or choose another date?");
			alert.setContentText(contentText.toString());

			// 3. Create Custom Buttons for the Alert
			ButtonType waitingListBtn = new ButtonType("Join Waiting List");
			ButtonType cancelBtn = new ButtonType("Choose Another Date", ButtonBar.ButtonData.CANCEL_CLOSE);

			alert.getButtonTypes().setAll(waitingListBtn, cancelBtn);

			// 4. Show the alert and wait for the user to click a button
			Optional<ButtonType> result = alert.showAndWait();

			// 5. If the user chose to join the waiting list, send the request to the
			// server!
			if (result.isPresent() && result.get() == waitingListBtn) {
				Message waitListMsg = new Message("ADD_TO_WAITING_LIST", originalOrderData);
				try {
					ClientUI.send(waitListMsg);
				} catch (Exception e) {
					System.out.println("Failed to send ADD_TO_WAITING_LIST message");
					e.printStackTrace();
				}
			}
		});
	}
}