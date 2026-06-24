package Strategy;

import Common.Message;
import GUI.ParkWorkerExitVisitorController; 
import GUI.VisitorOrdersScreenController;
import javafx.application.Platform;
import javafx.scene.control.Alert;

public class ExitVisitorResultStrategy implements MessageStrategy {

    @Override
    public void execute(Message message) {
        // התיקון הקריטי: אנחנו קולטים String מהשרת, לא boolean!
        String resultMessage = (String) message.getData();
        
        // נבדוק האם הפעולה הצליחה (לפי תחילת המחרוזת שהשרת שלח)
        boolean success = resultMessage.startsWith("Success");

        Platform.runLater(() -> {

            // ==========================================================
            // SCENARIO 1: The Park Worker is using the Exit Screen
            // ==========================================================
            if (ParkWorkerExitVisitorController.instance != null) {
                if (success) {
                    ParkWorkerExitVisitorController.instance.showStatus("Visitor(s) exited successfully. Park capacity updated.");
                } else {
                    // מציגים גם את השגיאה המדויקת מהשרת, וגם הצעות לבדיקה
                    ParkWorkerExitVisitorController.instance.showStatus(
                            resultMessage + "\n\nPlease verify:\n1. The ID/QR is correct.\n2. The visitor is checked in.\n3. Amount doesn't exceed group size.");
                }
            }

            // ==========================================================
            // SCENARIO 2: The Visitor is using the Orders Screen
            // ==========================================================
            if (VisitorOrdersScreenController.instance != null) {
                if (success) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Exit Successful");
                    alert.setHeaderText("We hope you enjoyed your visit!");
                    alert.setContentText("Your exit has been recorded successfully, and your order is now fulfilled. Thank you for visiting GoNature!");
                    alert.showAndWait();

                    // Refresh the table so the order status changes to "Fulfilled"!
                    VisitorOrdersScreenController.instance.refreshOrders(null);
                } else {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Exit Process Failed");
                    alert.setHeaderText("Unable to record exit at this time.");
                    alert.setContentText(resultMessage + "\n\nPossible reasons:\n"
                            + "• You have already been marked as exited.\n"
                            + "• Your entry was not properly recorded earlier today.\n\n"
                            + "Please approach a park worker at the gate for manual assistance.");
                    alert.showAndWait();
                }
            }

        });
    }
}