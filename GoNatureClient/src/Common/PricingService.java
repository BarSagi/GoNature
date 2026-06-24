package Common;

/**
 * Service class responsible for calculating the final price of a park visit.
 * The calculation is based on the base price, the type of visit, the number of visitors,
 * and additional discount flags such as prepaid status and subscriber status.
 */
public class PricingService {

	/**
	 * The base full price for a single visitor without any discounts.
	 */
	private double fullPrice = 100;

	/**
	 * Calculates the total price for a visit based on the specified parameters.
	 *
	 * @param visitType     The type of the visit (e.g., "REGULAR_PREORDER", "REGULAR_NOT_PREORDER", "GUIDE_PREORDER", "GUIDE_NOT_PREORDER").
	 * @param numOfVisitors The total number of visitors in the group.
	 * @param prepaid       A flag indicating whether the order was paid in advance (true) or will be paid at the park (false).
	 * @param subscriber    A flag indicating whether the visitor is a registered subscriber.
	 * @return The final calculated total price after applying all relevant discounts.
	 */
	public double calculatePrice(String visitType, int numOfVisitors, boolean prepaid, boolean subscriber) {

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

		return numOfVisitors * fullPrice * (1 - discount / 100.0);
	}
}