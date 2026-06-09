package Pricing;

public class PricingService {

	private double fullPrice = 100;

	public double calculatePrice(String visitType, int numOfVisitors, boolean prepaid, boolean subscriber) {

		double discount = 0;

		switch (visitType) {

		case "INDIVIDUAL_PREORDER":
			discount = 15;
			break;

		case "INDIVIDUAL_NOT_PREORDER":
			discount = 0;
			break;

		case "GROUP_PREORDER":
			discount = 25;

			if (prepaid) {
				discount += 12;
			}
			break;

		case "GROUP_NOT_PREORDER":
			discount = 10;
			break;
		}

		if (subscriber) {
			discount += 10;
		}

		return numOfVisitors * fullPrice * (1 - discount / 100.0);
	}
}