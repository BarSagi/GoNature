package Strategy;

import Common.Message;
import GUI.CreateOrderController;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

public class PriceResultPreorderStrategy implements MessageStrategy {

    @Override
    public void execute(Message message) {

        Double price = (Double) message.getData();

        CreateOrderController.lastCalculatedPrice = price;

        Platform.runLater(() -> {

            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("SIMULATION");
            alert.setHeaderText("Order Price");
            alert.setContentText("Total price: " + price + " ₪\n\nProceed to payment?");

            ButtonType pay = new ButtonType("Pay");
            alert.getButtonTypes().setAll(pay);

            alert.showAndWait();
        });
    }

    /*private void handlePaymentFlow(double price) {

        Platform.runLater(() -> {

            Alert paymentAlert = new Alert(Alert.AlertType.INFORMATION);
            paymentAlert.setTitle("Payment Method");

            String method = CreateOrderController.selectedPaymentMethod;

            if ("Credit Card".equals(method)) {
                paymentAlert.setHeaderText("Payment Successful");
                paymentAlert.setContentText("Paid " + price + " ₪ by Credit Card");
            } else {
                paymentAlert.setHeaderText("Cash Payment");
                paymentAlert.setContentText("You will pay " + price + " ₪ on arrival");
            }

            paymentAlert.showAndWait();
        });
    }*/
}