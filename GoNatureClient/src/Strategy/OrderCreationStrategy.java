package Strategy;

import java.util.ArrayList;
import java.util.Optional;

import Client.ClientUI;
import Common.Message;
import GUI.CreateOrderController;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;

public class OrderCreationStrategy implements MessageStrategy {

    @Override
    public void execute(Message message) {
        String result = (String) message.getData();

        Platform.runLater(() -> {
            if ("Approved".equals(result)) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Order Approved");
                alert.setHeaderText(null);
                alert.setContentText("Your order has been approved successfully.");
                alert.showAndWait();
                return;
            }

            if (result != null && result.startsWith("Full|")) {
                String alternatives = result.substring("Full|".length());

                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Selected Date and Time Full");
                alert.setHeaderText("The selected date and time are fully booked.");
                alert.setContentText(
                        "You can join the waiting list or choose another date/time.\n\nAvailable alternative times:\n"
                                + alternatives);

                ButtonType waitingListButton = new ButtonType("Join Waiting List");
                ButtonType chooseAnotherButton = new ButtonType("Choose Another Time", ButtonBar.ButtonData.CANCEL_CLOSE);

                alert.getButtonTypes().setAll(waitingListButton, chooseAnotherButton);

                Optional<ButtonType> choice = alert.showAndWait();

                if (choice.isPresent() && choice.get() == waitingListButton) {
                    if (CreateOrderController.lastOrderData != null && ClientUI.client != null) {
                        ClientUI.client.addToWaitingList(CreateOrderController.lastOrderData);
                    }
                }

                return;
            }

            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Order Failed");
            alert.setHeaderText(null);
            alert.setContentText("We could not create your order. Please try again.");
            alert.showAndWait();
        });
    }
}