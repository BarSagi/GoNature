package Strategy;

import Common.Message;
import OCSFUtils.ConnectionToClient;
import Server.EchoServer;

import java.util.ArrayList;

public class RegisterAndOrderStrategy implements MessageStrategy {

	@Override
	public void execute(Message message, ConnectionToClient client, EchoServer server) {

		// 1. Extract the full list of data sent by the Client
		@SuppressWarnings("unchecked")
		ArrayList<Object> fullData = (ArrayList<Object>) message.getData();

		String visitorId = (String) fullData.get(0);

		server.log("[STRATEGY] Processing registration and order for Visitor ID: " + visitorId);

		// =========================================================
		// STEP 1: PREPARE DATA ARRAYS
		// =========================================================
		// visitorData expects: [visitorId, firstName, lastName, phone, email]
		ArrayList<String> visitorData = new ArrayList<>();
		visitorData.add((String) fullData.get(0)); // visitorId
		visitorData.add((String) fullData.get(1)); // firstName
		visitorData.add((String) fullData.get(2)); // lastName
		visitorData.add((String) fullData.get(4)); // phone
		visitorData.add((String) fullData.get(3)); // email

		// orderData expects: [visitorId, parkName, visitDate, visitTime, visitorCount,
		// email, orderType]
		ArrayList<String> orderData = new ArrayList<>();
		orderData.add((String) fullData.get(0)); // visitorId
		orderData.add((String) fullData.get(5)); // parkName
		orderData.add((String) fullData.get(7)); // visitDate
		orderData.add((String) fullData.get(8)); // visitTime
		orderData.add((String) fullData.get(9)); // visitorCount
		orderData.add((String) fullData.get(3)); // email
		orderData.add("Individual"); // orderType

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

			// 2. Call your partner's updated createNewOrder method!
			String orderResult = server.getDatabase().createNewOrder(orderData);

			if ("Approved".equals(orderResult)) {
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