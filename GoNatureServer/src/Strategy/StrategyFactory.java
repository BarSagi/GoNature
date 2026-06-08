package Strategy;

public class StrategyFactory {

	public static MessageStrategy getStrategy(String command) {

		switch (command) {

		case "GET_ORDERS":
			return new GetOrdersStrategy();

		case "UPDATE_ORDER":
			return new UpdateOrderStrategy();

		case "CHECK_VISITOR_ORDERS":
			return new CheckVisitorOrdersStrategy();

		case "SUBMIT_NEW_ORDER":
			return new SubmitNewOrderStrategy();

		case "REGISTER_NEW_VISITOR":
			return new RegisterNewVisitorStrategy();
			
		case "CHECK_EMPLOYEE_INFO":
			return new CheckEmployeeInfoStrategy();

		case "GET_VISIT_REPORT":
		    return new GetVisitReportStrategy();
		    
		default:
			return null;
		}
	}
}