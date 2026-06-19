package Strategy;

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

			boolean subscriber = "Subscriber".equals(visitorType);
			boolean guide = "Guide".equals(visitorType);

			String visitType;

			if (guide) {
				visitType = "GUIDE_PREORDER";
			} else {
				visitType = "REGULAR_PREORDER";
			}

			boolean prepaid = "Credit Card".equals(paymentMethod);

			PricingService pricingService = new PricingService();

			double price = pricingService.calculatePrice(
					visitType,
					numOfVisitors,
					prepaid,
					subscriber
			);

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