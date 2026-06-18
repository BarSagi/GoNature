package Strategy;

import java.util.ArrayList;

import Common.Message;
import PricingService.PricingService;
import OCSFUtils.ConnectionToClient;
import Server.EchoServer;

public class CalculatePricePreorderStrategy implements MessageStrategy {

	@Override
	public void execute(Message msg, ConnectionToClient client, EchoServer server) throws Exception {

		try {
			@SuppressWarnings("unchecked")
			ArrayList<String> data = (ArrayList<String>) msg.getData();

			String visitType = data.get(0);
			int numOfVisitors = Integer.parseInt(data.get(1));
			boolean prepaid = Boolean.parseBoolean(data.get(2));
			boolean subscriber = Boolean.parseBoolean(data.get(3));

			PricingService pricingService = new PricingService();

			double price = pricingService.calculatePrice(visitType, numOfVisitors, prepaid, subscriber);

			client.sendToClient(new Message("PRICE_RESULT_PREORDER", price));

		} catch (Exception e) {
			e.printStackTrace();

			client.sendToClient(new Message("PRICE_RESULT_PREORDER", null));
		}
	}
}