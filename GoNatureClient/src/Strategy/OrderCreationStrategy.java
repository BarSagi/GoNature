package Strategy;

import Common.Message;
import javafx.application.Platform;
import javafx.scene.control.Alert;

public class OrderCreationStrategy implements MessageStrategy {

    @Override
    public void execute(Message message) {

        Boolean success = (Boolean) message.getData();

        Platform.runLater(() -> {

            // =========================
            // SUCCESS
            // =========================
            if (Boolean.TRUE.equals(success)) {

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Order Approved");
                alert.setHeaderText(null);
                alert.setContentText("Your order has been approved successfully.");
                alert.showAndWait();
                return;
            }

            // =========================
            // FAILURE
            // =========================
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Order Failed");
            alert.setHeaderText(null);
            alert.setContentText("We could not create your order. Please try again.");
            alert.showAndWait();
        });
    }
}