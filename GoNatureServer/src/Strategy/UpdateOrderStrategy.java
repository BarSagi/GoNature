package Strategy;

import java.io.IOException;

import Common.Message;
import Common.Order;
import OCSFUtils.ConnectionToClient;
import Server.EchoServer;

public class UpdateOrderStrategy implements MessageStrategy {

	@Override
	public void execute(Message message, ConnectionToClient client, EchoServer server) {
		Order orderToUpdate = (Order) message.getData();

		// 1. Extract values and convert Date/Time to Strings for the new method
		int parkId = orderToUpdate.getParkId();
		String dateStr = orderToUpdate.getVisitDate().toString();
		String timeStr = orderToUpdate.getVisitTime().toString();
		int visitors = orderToUpdate.getVisitorCount();
		int orderId = orderToUpdate.getOrderId();

		// 2. Check capacity using the new overloaded method
		// Notice we pass 'orderId' at the end so the database knows to ignore their old
		// spot!
		boolean isAvailable = server.getDatabase().hasRoomInSlot(parkId, dateStr, timeStr, visitors, orderId);

		if (!isAvailable) {
			try {
				client.sendToClient(new Message("UPDATE_FAILED", "Not enough space"));
				return;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		// 3. Send the Order to the DBController to execute the UPDATE query
		boolean isSuccess = server.getDatabase().updateOrder(orderToUpdate);

		// 4. Send the result back to the Client
		try {
			if (isSuccess) {
				client.sendToClient(new Message("UPDATE_SUCCESS", null));
			} else {
				client.sendToClient(new Message("UPDATE_FAILED", "Order ID not found or database error."));
			}
		} catch (Exception e) {
			System.out.println("Error sending UPDATE result back to client.");
			e.printStackTrace();
		}
	}
}