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
		ArrayList<String> fullData = (ArrayList<String>) message.getData();

		String visitorId = fullData.get(0);
		String parkName = fullData.get(5);
		String visitDate = fullData.get(6);
		String visitTime = fullData.get(7);
		int visitorCount = Integer.parseInt(fullData.get(8));

		server.log("[STRATEGY] Processing registration and order for Visitor ID: " + visitorId);
		server.log("[STRATEGY] Checking availability for " + parkName + " on " + visitDate + " at " + visitTime);

		// =========================================================
		// STEP 1: CAPACITY / AVAILABILITY CHECK
		// =========================================================
		boolean isAvailable = server.getDatabase().isTimeSlotAvailable(parkName, visitDate, visitTime, visitorCount);

		if (!isAvailable) {
			server.log("[STRATEGY] Time slot is full! Denying order.");
			try {
				// Send the specific message back so the UI can show the Waiting List / Change
				// Date popup
				client.sendToClient(new Message("TIME_SLOT_FULL", null));
			} catch (Exception e) {
				e.printStackTrace();
			}
			return; // STOP EXECUTION! Do not register the visitor or create the order.
		}

		// =========================================================
		// STEP 2: PREPARE DATA ARRAYS
		// =========================================================
		// visitorData expects: [visitorId, firstName, lastName, phone, email]
		ArrayList<String> visitorData = new ArrayList<>();
		visitorData.add(fullData.get(0)); // visitorId
		visitorData.add(fullData.get(1)); // firstName
		visitorData.add(fullData.get(2)); // lastName
		visitorData.add(fullData.get(4)); // phone (Index 4 from client)
		visitorData.add(fullData.get(3)); // email (Index 3 from client)

		// orderData expects: [visitorId, parkName, visitDate, visitTime, visitorCount,
		// email, orderType]
		ArrayList<String> orderData = new ArrayList<>();
		orderData.add(fullData.get(0)); // visitorId
		orderData.add(fullData.get(5)); // parkName
		orderData.add(fullData.get(6)); // visitDate
		orderData.add(fullData.get(7)); // visitTime
		orderData.add(fullData.get(8)); // visitorCount
		orderData.add(fullData.get(3)); // email
		orderData.add("Individual"); // orderType (Hardcoded for new casual/registered visitors)

		// =========================================================
		// STEP 3: EXECUTE DATABASE INSERTS
		// =========================================================
		Message response;

		// Register the visitor first
		boolean isVisitorRegistered = server.getDatabase().registerNewVisitor(visitorData);

		if (!isVisitorRegistered) {
			server.log("[STRATEGY] Failed to register visitor. ID might already exist.");
			response = new Message("REGISTER_AND_ORDER_FAIL", "Could not register visitor. ID may already exist.");
		} else {
			// Visitor registered successfully, now create the order!
			boolean isOrderCreated = server.getDatabase().createNewOrder(orderData);

			if (isOrderCreated) {
				server.log("[STRATEGY] Successfully registered visitor and created new order.");
				response = new Message("REGISTER_AND_ORDER_SUCCESS", visitorData);
			} else {
				server.log("[STRATEGY] Visitor registered, but order creation failed.");
				response = new Message("REGISTER_AND_ORDER_FAIL", "Visitor registered, but order creation failed.");
			}
		}

		// =========================================================
		// STEP 4: SEND FINAL RESPONSE TO CLIENT
		// =========================================================
		try {
			client.sendToClient(response);
		} catch (Exception e) {
			server.log("[ERROR] Failed to send response to client: " + e.getMessage());
			e.printStackTrace();
		}
	}
}