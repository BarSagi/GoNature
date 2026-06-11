package Strategy;

public class StrategyFactory {

	public static MessageStrategy getStrategy(String command) {

		switch (command) {

		case "VISITOR_REGISTRATION_RESULT":
			return new VisitorRegistrationStrategy();

		case "ORDER_CREATION_RESULT":
			return new OrderCreationStrategy();

		case "RETURN_VISITOR_ORDERS":
			return new ReturnVisitorOrdersStrategy();

		case "EMPLOYEE_ROLE_RESULT":
			return new EmployeeInfoStrategy();

		case "ENTER_VISITOR_RESULT":
			return new EnterVisitorResultStrategy();

		case "EXIT_VISITOR_RESULT":
			return new ExitVisitorResultStrategy();

		case "REGISTER_FAMILY_SUBSCRIBER_RESULT":
		    return new RegisterFamilySubscriberResultStrategy();

		case "REGISTER_GROUP_GUIDE_RESULT":
		    return new RegisterGroupGuideResultStrategy();
		
		case "SUBMIT_PARK_REQUEST_RESULT":
		    return new SubmitParkRequestResultStrategy();
		   
		case "GET_PARK_CURRENT_VALUE_RESULT":
		    return new GetParkCurrentValueResultStrategy();
		    
		default:
			return null;
		}
	}
}