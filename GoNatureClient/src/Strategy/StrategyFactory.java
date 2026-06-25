package Strategy;

/**
 * Creates the appropriate message handling strategy according to the received
 * command.
 * <p>
 * This factory is used on the client side to match each server command with the
 * correct strategy class that knows how to handle it.
 */
public class StrategyFactory {

	/**
	 * Returns the strategy that matches the given command.
	 * <p>
	 * If the command is not recognized, this method returns {@code null}.
	 *
	 * @param command the command received from the server
	 * @return the matching {@code MessageStrategy}, or {@code null} if no strategy
	 *         matches
	 */
	public static MessageStrategy getStrategy(String command) {

		switch (command) {

		case "FORCE_LOGOUT":
			return new ForceLogoutStrategy();

		case "IDLE_WARNING":
			return new IdleWarningStrategy();

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

		case "PRICE_RESULT_PREORDER":
			return new PriceResultPreorderStrategy();

		case "PRICE_RESULT_CASUAL":
			return new PriceResultCasualStrategy();

		case "VISITOR_TYPE_RESULT":
			return new VisitorTypeResultStrategy();

		case "PARK_DASHBOARD_DATA":
			return new ParkDashboardDataStrategy();

		case "QUICK_SEARCH_RESULT":
			return new QuickSearchResultStrategy();

		case "CONFIRM_ORDER_RESULT":
			return new ConfirmOrderResultStrategy();

		case "VISITOR_EMAIL_RESULT":
			return new VisitorEmailResult();

		case "RETURN_NOTIFICATIONS":
			return new ReturnNotificationsStrategy();

		case "GET_ALL_REPORTS_RESULT":
			return new GetAllReportsResultStrategy();

		case "SAVE_REPORT_RESULT":
			return new SaveReportResultStrategy();

		case "VISITOR_DETAILS_RESULT":
			return new VisitorDetailsResultStrategy();

		case "UPDATE_VISITOR_DETAILS_RESULT":
			return new UpdateVisitorDetailsResultStrategy();

		case "EMPLOYEE_DETAILS_RESULT":
			return new EmployeeDetailsResultStrategy();

		case "SUBSCRIBER_DETAILS_RESULT":
			return new SubscriberDetailsResultStrategy();

		default:
			return null;
		}
	}
}