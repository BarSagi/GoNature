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
		    
		case "ENTER_VISITOR":
		    return new EnterVisitorStrategy();

		case "EXIT_VISITOR":
		    return new ExitVisitorStrategy();
		    
		case "REGISTER_FAMILY_SUBSCRIBER":
		    return new RegisterFamilySubscriberStrategy();

		case "REGISTER_GROUP_GUIDE":
		    return new RegisterGroupGuideStrategy();
		
		case "SUBMIT_PARK_REQUEST":
		    return new SubmitParkRequestStrategy();
		    
		case "GET_PARK_CURRENT_VALUE":
		    return new GetParkCurrentValueStrategy();
		    
		default:
			return null;
		}
	}
}