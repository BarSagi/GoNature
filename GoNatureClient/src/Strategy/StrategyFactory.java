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

		case "RETURN_VISITOR_ORDERS_AND_DATA":
			return new ReturnVisitorOrdersAndDataStrategy();

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

		case "TIME_SLOT_FULL":
			return new TimeSlotFullStrategy();

		case "REGISTER_AND_ORDER_FAIL":
			return new RegisterOrderFailStrategy();

		case "REGISTER_AND_ORDER_SUCCESS":
			return new RegisterOrderSuccessStrategy();

		case "ALREADY_LOGGED_IN":
			return new AlreadyLoggedInStrategy();

		case "VISIT_REPORT_RESULT":
			return new VisitReportStrategy();

		case "USAGE_REPORT_RESULT":
			return new UsageReportStrategy();

		case "GET_VISIT_DURATION_REPORT":
			return new VisitDurationReportStrategy();

		case "VISIT_DURATION_REPORT_RESULT":
			return new VisitDurationReportResultStrategy();

		case "ALL_PARKS_RESULT":
			return new AllParksStrategy();

		case "PARK_ORDERS_RESULT":
			return new ParkOrdersResultStrategy();

		case "CREATE_CASUAL_VISIT_RESULT":
			return new CasualVisitResultStrategy();

		case "UPDATE_SUCCESS":
			return new UpdateSuccessStrategy();

		case "UPDATE_FAILED":
			return new UpdateFailedStrategy();

		case "ORDER_CANCEL_RESULT":
			return new OrderCancelResultStrategy();

		case "ADD_TO_WAITING_LIST_RESULT":
			return new AddToWaitingListResultStrategy();

		case "CANCELLATION_REPORT_RESULT":
			return new CancellationReportStrategy();

		case "RETURN_PENDING_REQUESTS":
		    return new PendingRequestsResultStrategy();

		case "APPROVE_REQUEST_RESULT":
		    return new ApproveRequestResultStrategy();

		case "REJECT_REQUEST_RESULT":
		    return new RejectRequestResultStrategy();
		    
		case "RETURN_PARK_ORDERS":
		    return new ParkOrdersResultStrategy();
		    
		default:
			return null;
		}
	}
}