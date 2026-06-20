package Strategy;

import java.time.LocalDate;
import java.util.ArrayList;

import Common.Message;
import PricingService.PricingService;
import OCSFUtils.ConnectionToClient;
import Server.EchoServer;

public class CalculatePricePreorderStrategy implements MessageStrategy {

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

			boolean prepaid = "Credit Card".equals(paymentMethod);

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