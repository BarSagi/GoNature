package PricingService;

import java.time.LocalDate;

import Server.EchoServer;

public class PricingService {

	private double fullPrice = 100;

	public double calculatePrice(String visitType, int numOfVisitors, boolean prepaid, boolean subscriber, int parkId,
			LocalDate orderDate, EchoServer server) {

		double discount = 0;

		switch (visitType) {

		case "REGULAR_PREORDER":
			discount = 15;
			break;

		case "REGULAR_NOT_PREORDER":
			discount = 0;
			break;

		case "GUIDE_PREORDER":
			discount = 25;
			numOfVisitors -= 1;

			if (prepaid) {
				discount += 12;
			}
			break;

		case "GUIDE_NOT_PREORDER":
			discount = 10;
			break;
		}

		if (subscriber) {
			discount += 10;
		}

		double promotionsDiscount = server.getDatabase().getActivePromotionsDiscount(parkId, orderDate);
		
		discount=Math.min(discount+promotionsDiscount, 100);

		return numOfVisitors * fullPrice * (1 - discount / 100.0);
	}
}