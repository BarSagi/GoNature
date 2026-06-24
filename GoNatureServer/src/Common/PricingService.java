package Common;

import java.time.LocalDate;

import Server.EchoServer;

/**
 * Service class responsible for calculating visit prices. The price is
 * calculated according to visit type, number of visitors, subscriber status,
 * prepaid status, and active park promotions.
 */
public class PricingService {

	private double fullPrice = 100;

	/**
	 * Calculates the final price for a park visit. The method applies discounts
	 * based on the visit type, subscriber status, prepaid payment, and active
	 * promotions for the selected park and date.
	 *
	 * @param visitType     the type of visit
	 * @param numOfVisitors the number of visitors in the order
	 * @param prepaid       true if the order was prepaid, otherwise false
	 * @param subscriber    true if the visitor is a subscriber, otherwise false
	 * @param parkId        the ID of the selected park
	 * @param orderDate     the date of the order
	 * @param server        the server instance used to access the database
	 * @return the final calculated price after applying all discounts
	 */
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

		discount = Math.min(discount + promotionsDiscount, 100);

		return numOfVisitors * fullPrice * (1 - discount / 100.0);
	}
}