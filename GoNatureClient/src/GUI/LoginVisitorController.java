package GUI;

import Client.ClientUI;
import Common.Message;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

public class LoginVisitorController {

	@FXML
	private TextField idField;

	@FXML
	void loginVisitor(ActionEvent event) {
		String id = idField.getText();

		// 1. Basic validation
		if (id == null || id.trim().isEmpty() || !id.matches("\\d+")) {
			System.out.println("Invalid input: Please enter a valid ID number.");
			return;
		}

		System.out.println("Attempting to log in visitor with ID: " + id);

		// 2. Save the ID globally so the OrderCreation/History screens can use it later
		// (Assuming you have a variable like loggedInVisitorId in your ClientUI or a
		// Session class)
		// ClientUI.visitorID = id;
		// CURRENTLY UNUSED MAYBE LATER -------------------------------

		// 3. Send a message to the server asking for this visitor's orders
		// We package the command and the ID into your Message object
		Message msg = new Message("CHECK_VISITOR_ORDERS", id);

		try {
			ClientUI.send(msg);
		} catch (Exception e) {
			System.out.println("Error sending message to server");
			e.printStackTrace();
		}

		// Notice: We DO NOT change the screen here!
		// We must wait for the server to check the DB and reply.
		// The screen change will happen in the Client's handleMessageFromServer method.
	}

	@FXML
	void goBack(ActionEvent event) {
		ClientUI.changeScreen("/GUI/LoginRoute.fxml", "GoNature - Choose Role");
	}
}