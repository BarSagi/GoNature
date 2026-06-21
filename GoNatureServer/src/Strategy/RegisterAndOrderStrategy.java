package Strategy;

import Common.Message;
import Common.Order;
import OCSFUtils.ConnectionToClient;
import Server.EchoServer;

import java.util.ArrayList;

public class RegisterAndOrderStrategy implements MessageStrategy {

	@Override
	public void execute(Message message, ConnectionToClient client, EchoServer server) {

		// 1. Extract the full list of data sent by the Client
		@SuppressWarnings("unchecked")
		ArrayList<Object> fullData = (ArrayList<Object>) message.getData();

		// =========================================================
		// Index 0 = ArrayList<String> (Visitor Info)
		// Index 1 = Order (Order Object)
		// =========================================================
		@SuppressWarnings("unchecked")
		ArrayList<String> visitorData = (ArrayList<String>) fullData.get(0);
		Order receivedOrder = (Order) fullData.get(1);

		String visitorId = visitorData.get(0);
		String email = visitorData.get(3); // Based on your client code, email is at index 3

		server.log("[STRATEGY] Processing registration and order for Visitor ID: " + visitorId);

		// =========================================================
		// STEP 1: PREPARE ORDER DATA ARRAY
		// Your DBController.createNewOrder expects a specific String array:
		// [visitorId, parkName, visitDate, visitTime, visitorCount, email, orderType]
		// =========================================================
		ArrayList<String> orderData = new ArrayList<>();
		orderData.add(visitorId); // visitorId

		// Translate parkId back to parkName for the DBController
		String parkName = "Unknown";
		if (receivedOrder.getParkId() == 1)
			parkName = "Karmel";
		else if (receivedOrder.getParkId() == 2)
			parkName = "Banias";
		else if (receivedOrder.getParkId() == 3)
			parkName = "Yarkon";
		orderData.add(parkName); // parkName

		orderData.add(receivedOrder.getVisitDate().toString()); // visitDate
		orderData.add(receivedOrder.getVisitTime().toString()); // visitTime
		orderData.add(String.valueOf(receivedOrder.getVisitorCount())); // visitorCount
		orderData.add(email); // email
		orderData.add(receivedOrder.getOrderType()); // orderType

		// =========================================================
		// STEP 2: EXECUTE DATABASE INSERTS (WITH ROLLBACK)
		// =========================================================
		Message response = null;

		// 1. Register the Visitor FIRST (To satisfy Foreign Key constraints)
		boolean isVisitorRegistered = server.getDatabase().registerNewVisitor(visitorData);

		if (!isVisitorRegistered) {
			server.log("[STRATEGY] Failed to register visitor. ID might already exist.");
			response = new Message("REGISTER_AND_ORDER_FAIL", "Could not register visitor. ID may already exist.");
		} else {

			// 2. Call your createNewOrder method!
			String orderResult = server.getDatabase().createNewOrder(orderData);

			if (orderResult != null && orderResult.startsWith("Approved")) {
				// SUCCESS!
				server.log("[STRATEGY] Successfully registered visitor and created new order.");
				response = new Message("REGISTER_AND_ORDER_SUCCESS", visitorData);

			} else if (orderResult != null && orderResult.startsWith("Full")) {
				// PARK IS FULL!
				server.log("[STRATEGY] Time slot is full! Rolling back visitor registration...");

				// Rollback the visitor so we don't leave ghost data!
				server.getDatabase().deleteVisitor(visitorId);

				// Extract the alternative times (Splits "Full|10:00, 11:00" into just "10:00,
				// 11:00")
				String[] parts = orderResult.split("\\|");
				String alternatives = parts.length > 1 ? parts[1] : "";

				// Send the specific message back with the alternatives as the payload!
				response = new Message("TIME_SLOT_FULL", alternatives);

			} else {
				// FAILED (Database Error)
				server.log("[STRATEGY] Order creation failed! Rolling back visitor registration...");
				server.getDatabase().deleteVisitor(visitorId);
				response = new Message("REGISTER_AND_ORDER_FAIL",
						"Order creation failed. Visitor registration was rolled back.");
			}
		}

		// =========================================================
		// STEP 3: SEND FINAL RESPONSE TO CLIENT
		// =========================================================
		try {
			if (response != null) {
				client.sendToClient(response);
			}
		} catch (Exception e) {
			server.log("[ERROR] Failed to send response to client: " + e.getMessage());
			e.printStackTrace();
		}
	}
}