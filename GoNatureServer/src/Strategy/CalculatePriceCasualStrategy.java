package Strategy;

import java.time.LocalDate;
import java.util.ArrayList;

import Common.Message;
import PricingService.PricingService;
import OCSFUtils.ConnectionToClient;
import Server.EchoServer;

/**
 * Strategy class responsible for calculating the price for a casual visit.
 * The strategy receives visit details from the client, checks the visitor type,
 * calculates the correct price, and sends the result back to the client.
 */
public class CalculatePriceCasualStrategy implements MessageStrategy {

	/**
	 * Executes the casual price calculation command.
	 * The method extracts the visitor data from the message, gets the park ID
	 * and visitor type from the database, calculates the final price,
	 * and sends the price result back to the client.
	 *
	 * @param msg the message received from the client
	 * @param client the client connection that sent the message
	 * @param server the server that handles the request and provides database access
	 * @throws Exception if an error occurs while calculating the price or sending the response
	 */
	@Override
	public void execute(Message msg, ConnectionToClient client, EchoServer server) throws Exception {

		try {

			@SuppressWarnings("unchecked")
			ArrayList<String> data = (ArrayList<String>) msg.getData();

			String visitorId = data.get(0);
			int visitorCount = Integer.parseInt(data.get(1));

			int parkId = server.getDatabase().getParkIdByName(data.get(2));

			LocalDate dateOfOrder = LocalDate.parse(data.get(3));

			String visitorType = server.getDatabase().getVisitorTypeById(visitorId);

			String visitType;
			boolean subscriber;

			if ("Guide".equals(visitorType)) {

				visitType = "GUIDE_NOT_PREORDER";
				subscriber = false;

			} else if ("Subscriber".equals(visitorType)) {

				visitType = "REGULAR_NOT_PREORDER";
				subscriber = true;

			} else {

				visitType = "REGULAR_NOT_PREORDER";
				subscriber = false;
			}

			PricingService pricingService = new PricingService();

			double price = pricingService.calculatePrice(visitType, visitorCount, false, subscriber, parkId,
					dateOfOrder, server);

			client.sendToClient(new Message("PRICE_RESULT_CASUAL", price));

		}

		catch (Exception e) {

			e.printStackTrace();

			client.sendToClient(new Message("PRICE_RESULT_CASUAL", null));
		}
	}
}