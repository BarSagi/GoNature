package Strategy;

import java.time.LocalDate;
import java.util.ArrayList;

import Common.Message;
import Common.PricingService;
import OCSFUtils.ConnectionToClient;
import Server.EchoServer;

/**
 * Strategy class responsible for calculating the price for a preorder visit.
 * The strategy receives preorder details from the client, checks the visitor type,
 * calculates the correct price, and sends the result back to the client.
 */
public class CalculatePricePreorderStrategy implements MessageStrategy {

	/**
	 * Executes the preorder price calculation command.
	 * The method extracts the order data from the message, checks the visitor type,
	 * determines the correct visit type and payment status, calculates the final price,
	 * and sends the price result back to the client.
	 *
	 * @param msg the message received from the client
	 * @param client the client connection that sent the message
	 * @param server the server that handles the request and provides database access
	 */
	@Override
	public void execute(Message msg, ConnectionToClient client, EchoServer server) {

		try {

			@SuppressWarnings("unchecked")
			ArrayList<String> data = (ArrayList<String>) msg.getData();

			String visitorId = data.get(0);
			int numOfVisitors = Integer.parseInt(data.get(1));
			String paymentMethod = data.get(2);
			String visitorType = server.getDatabase().getVisitorTypeById(visitorId);

			int parkId = server.getDatabase().getParkIdByName(data.get(3));
			
			LocalDate dateOfOrder = LocalDate.parse(data.get(4));
			
			System.out.println(data);


			boolean subscriber;
			String visitType;

			if ("Guide".equals(visitorType)) {

				visitType = "GUIDE_PREORDER";
				subscriber = false;

			} else if ("Subscriber".equals(visitorType)) {

				visitType = "REGULAR_PREORDER";
				subscriber = true;

			} else {

				visitType = "REGULAR_PREORDER";
				subscriber = false;
			}

			boolean prepaid = "Pay Now".equals(paymentMethod);

			PricingService pricingService = new PricingService();

			double price = pricingService.calculatePrice(visitType, numOfVisitors, prepaid, subscriber, parkId,
					dateOfOrder, server);			
			client.sendToClient(new Message("PRICE_RESULT_PREORDER", price));

		} catch (Exception e) {

			e.printStackTrace();

			try {
				client.sendToClient(new Message("PRICE_RESULT_PREORDER", null));
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}
}