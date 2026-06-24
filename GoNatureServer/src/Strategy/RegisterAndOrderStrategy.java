package Strategy;

import Common.Message;
import Common.Order;
import OCSFUtils.ConnectionToClient;
import Server.EchoServer;

import java.util.ArrayList;

/**
 * Strategy class responsible for registering a new visitor and creating an
 * order. The strategy receives visitor data, order data, and payment method
 * from the client, registers the visitor, tries to create the order, and sends
 * the result back to the client.
 */
public class RegisterAndOrderStrategy implements MessageStrategy {

	/**
	 * Executes the register and order command. The method extracts the visitor
	 * information, order object, and payment method, registers the visitor in the
	 * database, creates a new order, handles full time slots, and sends the proper
	 * response back to the client.
	 *
	 * @param message the message received from the client
	 * @param client  the client connection that sent the message
	 * @param server  the server that handles the request and provides database
	 *                access
	 */
	@Override
	public void execute(Message message, ConnectionToClient client, EchoServer server) {

		@SuppressWarnings("unchecked")
		ArrayList<Object> fullData = (ArrayList<Object>) message.getData();

		// =========================================================
		// Index 0 = ArrayList<String> (Visitor Info)
		// Index 1 = Order (Order Object)
		// Index 2 = String (Payment Method)
		// =========================================================
		@SuppressWarnings("unchecked")
		ArrayList<String> visitorData = (ArrayList<String>) fullData.get(0);
		Order receivedOrder = (Order) fullData.get(1);
		String paymentMethod = (String) fullData.get(2);

		String visitorId = visitorData.get(0);
		String email = visitorData.get(3);

		server.log("[STRATEGY] Processing registration and order for Visitor ID: " + visitorId);

		ArrayList<String> orderData = new ArrayList<>();
		orderData.add(visitorId); // 0: visitorId

		String parkName = server.getDatabase().getParkNameById(receivedOrder.getParkId());

		if (parkName == null) {
			parkName = "Unknown";
		}
		orderData.add(parkName); // 1: parkName

		orderData.add(receivedOrder.getVisitDate().toString()); // 2: visitDate
		orderData.add(receivedOrder.getVisitTime().toString()); // 3: visitTime
		orderData.add(String.valueOf(receivedOrder.getVisitorCount())); // 4: visitorCount
		orderData.add(receivedOrder.getOrderType());
		orderData.add(email);
		orderData.add(paymentMethod); // 7: paymentMethod

		Message response = null;

		boolean isVisitorRegistered = server.getDatabase().registerNewVisitor(visitorData);

		if (!isVisitorRegistered) {
			server.log("[STRATEGY] Failed to register visitor. ID might already exist.");
			server.getDatabase().deleteVisitor(visitorId);
			response = new Message("REGISTER_AND_ORDER_FAIL", "Could not register visitor. ID may already exist.");
		} else {

			String orderResult = server.getDatabase().createNewOrder(orderData);
			if (orderResult != null && orderResult.startsWith("Approved")) {
				server.log("[STRATEGY] Successfully registered visitor and created new order.");
				response = new Message("REGISTER_AND_ORDER_SUCCESS", visitorData);

			} else if (orderResult != null && orderResult.startsWith("Full")) {
				server.log("[STRATEGY] Time slot is full! Rolling back visitor registration...");

				String[] parts = orderResult.split("\\|");
				String alternatives = parts.length > 1 ? parts[1] : "";

				ArrayList<Object> timeSlotFullData = new ArrayList<>();
				timeSlotFullData.add(alternatives); // 0: available alternative times
				timeSlotFullData.add(orderData); // 1: original order data for waiting list

				response = new Message("TIME_SLOT_FULL", timeSlotFullData);

			} else {
				server.log("[STRATEGY] Order creation failed! Rolling back visitor registration...");
				server.getDatabase().deleteVisitor(visitorId);
				response = new Message("REGISTER_AND_ORDER_FAIL",
						"Order creation failed. Visitor registration was rolled back.");
			}
		}

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