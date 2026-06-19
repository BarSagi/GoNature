package Strategy;

import java.util.ArrayList;

import Common.Message;
import PricingService.PricingService;
import OCSFUtils.ConnectionToClient;
import Server.EchoServer;

public class CalculatePriceCasualStrategy implements MessageStrategy {

	@Override
	public void execute(Message msg, ConnectionToClient client, EchoServer server) throws Exception {

		try {

			@SuppressWarnings("unchecked")
			ArrayList<String> data = (ArrayList<String>) msg.getData();

			String visitorId = data.get(0);
			int visitorCount = Integer.parseInt(data.get(1));

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

			double price = pricingService.calculatePrice(visitType, visitorCount, false, subscriber);

			client.sendToClient(new Message("PRICE_RESULT_CASUAL", price));

		}

		catch (Exception e) {

			e.printStackTrace();

			client.sendToClient(new Message("PRICE_RESULT_CASUAL", null));
		}
	}
}