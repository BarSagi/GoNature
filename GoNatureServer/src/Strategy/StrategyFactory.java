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

		case "REGISTER_AND_ORDER":
			return new RegisterAndOrderStrategy();

		case "GET_USAGE_REPORT":
			return new GetUsageReportStrategy();

		case "FETCH_VISITOR_ORDERS":
			return new FetchVisitorOrdersStrategy();

		case "CLIENT_LOGOUT":
			return new ClientLogoutStrategy();

		case "GET_VISIT_DURATION_REPORT":
			return new GetVisitDurationReportStrategy();

		case "GET_ALL_PARKS":
			return new GetAllParksStrategy();

		case "CREATE_CASUAL_VISIT":
			return new CreateCasualVisitStrategy();

		case "CANCEL_ORDER":
			return new CancelOrderStrategy();

		case "ADD_TO_WAITING_LIST":
			return new AddToWaitingListStrategy();

		case "GET_CANCELLATION_REPORT":
			return new GetCancellationReportStrategy();

		case "GET_PENDING_REQUESTS":
		    return new GetPendingRequestsStrategy();

		case "APPROVE_REQUEST":
		    return new ApproveRequestStrategy();

		case "REJECT_REQUEST":
		    return new RejectRequestStrategy();
		    
		case "CALCULATE_PRICE_PREORDER":
			return new CalculatePricePreorderStrategy();
			
		case "CALCULATE_PRICE_CASUAL":
		    return new CalculatePriceCasualStrategy();
		    
		case "GET_VISITOR_TYPE":
			return new GetVisitorTypeStrategy();
			
		case "GET_PARK_DASHBOARD":
			return new GetParkDashboardStrategy();
		    
		default:
			return null;
		}
	}
}