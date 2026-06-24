package Database;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import Common.CancellationReportData;
import Common.Order;
import Common.PricingService;
import Common.ReportImage;
import Common.UsageReportData;
import Common.Visit;
import Common.VisitReportData;
import Common.VisitRecord;
import Server.EchoServer;

/**
 * 
 * The {@code DBController} class serves as the central Database Access Object
 * (DAO) for the GoNature server application. It acts as the primary bridge
 * between the server's business logic and the MySQL database, ensuring data
 * integrity, transaction safety, and optimal query execution.
 * <p>
 * Key responsibilities of this controller include:
 * <ul>
 * <li>Managing visitor registrations, family subscriptions, and employee
 * authentication.</li>
 * <li>Processing, updating, and validating park orders, including dynamic
 * waitlist management.</li>
 * <li>Logging physical park entries and exits, while dynamically tracking
 * real-time park capacities.</li>
 * <li>Aggregating statistical data to generate comprehensive reports for
 * management (e.g., Visit, Cancellation, and Usage reports).</li>
 * <li>Executing critical background system maintenance tasks, such as
 * auto-canceling expired orders.</li>
 * </ul>
 * <p>
 * All database interactions are managed efficiently and safely utilizing a
 * custom connection pool, allowing the multi-threaded server to handle
 * concurrent client requests without resource exhaustion.
 * 
 * @author Reut Dahan
 */
public class DBController {

	private DBConnectionPool pool;

	/**
	 * Constructs a new DBController instance and initializes the database
	 * connection pool.
	 *
	 * @param server The EchoServer instance used to configure or initialize the
	 *               connection pool.
	 */
	public DBController(EchoServer server) {
		this.pool = DBConnectionPool.getInstance(server);
	}

	// =========================================================
	// ORDERS - GET ALL ORDERS OF SPECIFIC PARK
	// =========================================================

	/**
	 * Retrieves a list of all orders associated with a specific park. This method
	 * first resolves the provided park name to its corresponding park ID. If the
	 * park is found, it queries the database and maps the result set into Order
	 * objects.
	 *
	 * @param parkName The name of the park for which orders should be retrieved.
	 * @return An {@link ArrayList} of {@link Order} objects containing the park's
	 *         orders. Returns an empty list if the park name is not found or if no
	 *         orders exist.
	 * @throws SQLException If a database access error occurs, or if the SQL
	 *                      execution fails.
	 */
	public ArrayList<Order> getAllParkOrders(String parkName) throws SQLException {
		ArrayList<Order> result = new ArrayList<>();

		// 1. Resolve the parkId using your existing helper method
		int parkId = getParkIdByName(parkName);

		// If the park name doesn't exist, return an empty list early
		if (parkId == -1) {
			System.err.println("[DB WARNING] getAllParkOrders: Park name '" + parkName + "' not found.");
			return result;
		}

		// 2. Simple and clean query selecting directly from Orders using the resolved
		// parkId
		String query = "SELECT * FROM Orders WHERE parkId = ?";

		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			conn = pool.getConnection();
			ps = conn.prepareStatement(query);
			ps.setInt(1, parkId);

			rs = ps.executeQuery();

			while (rs.next()) {
				result.add(new Order(rs.getInt("orderId"), rs.getInt("parkId"), rs.getString("visitorId"),
						rs.getDate("visitDate"), rs.getTime("visitTime"), rs.getInt("visitorCount"),
						rs.getString("email"), rs.getString("orderType"), rs.getString("status"),
						rs.getTimestamp("holdUntil")));
			}

		} finally {
			// Safe resource cleanup to prevent memory and connection leaks
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (ps != null) {
				try {
					ps.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (conn != null) {
				pool.releaseConnection(conn);
			}
		}

		return result;
	}

	// =========================================================
	// FETCH VISITOR
	// =========================================================

	/**
	 * Retrieves the details of a specific visitor from the database based on their
	 * visitor ID. The method executes a query against the Visitors table and maps
	 * the resulting record into an ArrayList of Strings, where each index
	 * represents a specific visitor attribute.
	 *
	 * @param visitorID The unique identifier of the visitor to fetch.
	 * @return An {@link ArrayList} of {@link String} containing the visitor's data
	 *         in the following order: [0] visitorId, [1] firstName, [2] lastName,
	 *         [3] phone, [4] email, [5] visitorType, [6] subscriptionNumber, [7]
	 *         familyMembers, [8] creditCard. Returns {@code null} if the visitor is
	 *         not found or if a database error occurs.
	 */
	public ArrayList<String> fetchVisitor(String visitorID) {
		String query = "SELECT * FROM Visitors WHERE visitorId = ?";

		Connection conn = null;

		try {
			conn = pool.getConnection();

			PreparedStatement ps = conn.prepareStatement(query);

			ps.setString(1, visitorID);

			ResultSet rs = ps.executeQuery();

			if (rs.next()) {
				ArrayList<String> visitor = new ArrayList<>();

				visitor.add(rs.getString("visitorId")); // 0
				visitor.add(rs.getString("firstName")); // 1
				visitor.add(rs.getString("lastName")); // 2
				visitor.add(rs.getString("phone")); // 3
				visitor.add(rs.getString("email")); // 4
				visitor.add(rs.getString("visitorType")); // 5
				visitor.add(String.valueOf(rs.getInt("subscriptionNumber"))); // 6
				visitor.add(String.valueOf(rs.getInt("familyMembers"))); // 7
				visitor.add(rs.getString("creditCard")); // 8

				rs.close();
				ps.close();

				return visitor;
			}

		} catch (SQLException e) {
			e.printStackTrace();

		} finally {
			pool.releaseConnection(conn);
		}

		return null;
	}

	// =========================================================
	// REGISTER VISITOR
	// =========================================================

	/**
	 * Registers a new casual visitor in the database. The method executes an INSERT
	 * query to add the visitor's details to the Visitors table, with the visitor
	 * type explicitly set to 'Casual'.
	 *
	 * @param visitorData An {@link ArrayList} of {@link String} containing the new
	 *                    visitor's details in the following order: [0] visitorId,
	 *                    [1] firstName, [2] lastName, [3] phone, [4] email.
	 * @return {@code true} if the visitor was successfully registered (i.e., at
	 *         least one row was affected), {@code false} if the registration failed
	 *         or if a database error occurred.
	 */
	public boolean registerNewVisitor(ArrayList<String> visitorData) {

		String query = "INSERT INTO Visitors (visitorId, firstName, lastName, phone, email, visitorType) "
				+ "VALUES (?, ?, ?, ?, ?, 'Casual')";

		Connection conn = null;

		try {
			conn = pool.getConnection();

			PreparedStatement ps = conn.prepareStatement(query);

			ps.setString(1, visitorData.get(0));
			ps.setString(2, visitorData.get(1));
			ps.setString(3, visitorData.get(2));
			ps.setString(4, visitorData.get(3));
			ps.setString(5, visitorData.get(4));

			int rows = ps.executeUpdate();

			ps.close();

			return rows > 0;

		} catch (SQLException e) {
			e.printStackTrace();
			return false;

		} finally {
			pool.releaseConnection(conn);
		}
	}

	// =========================================================
	// DELETE VISITOR (Used for Rollbacks)
	// =========================================================

	/**
	 * Deletes a visitor from the database based on their visitor ID. This method is
	 * primarily used for rollback operations to undo a previous registration or
	 * data entry in case a subsequent process fails.
	 *
	 * @param visitorId The unique identifier of the visitor to be deleted.
	 * @return {@code true} if the visitor was successfully deleted (i.e., at least
	 *         one row was affected), {@code false} if the deletion failed, the
	 *         visitor was not found, or a database error occurred.
	 */
	public boolean deleteVisitor(String visitorId) {
		Connection conn = null;
		try {
			conn = pool.getConnection();

			// Note: Check if your table is named 'Visitors' or 'Visitor'
			String query = "DELETE FROM Visitors WHERE visitorId = ?";
			PreparedStatement pstmt = conn.prepareStatement(query);

			pstmt.setString(1, visitorId);

			int rowsAffected = pstmt.executeUpdate();
			pstmt.close();

			return rowsAffected > 0;

		} catch (SQLException e) {
			System.out.println("Error deleting visitor during rollback.");
			e.printStackTrace();
			return false;
		} finally {
			if (conn != null) {
				pool.releaseConnection(conn);
			}
		}
	}

	// =========================================================
	// CANCEL EXPIRED ORDERS (for the thread)
	// =========================================================

	/**
	 * Automatically cancels expired orders by updating their status in the
	 * database. This method is intended to be executed periodically by a background
	 * thread. It identifies orders with a status of 'Approved',
	 * 'PendingConfirmation', or 'WaitingList' and updates them to 'Canceled' if
	 * more than 30 minutes have passed since their scheduled visit date and time.
	 *
	 * @return The number of expired orders that were successfully canceled (rows
	 *         affected). Returns 0 if no orders were eligible for cancellation or
	 *         if a database error occurred.
	 */
	public int cancelExpiredOrders() {
		int rowsAffected = 0;
		Connection conn = null;
		try {
			conn = pool.getConnection();
			// SQL query to cancel orders that are 30+ minutes past visitDate/visitTime
			String query = "UPDATE orders " + "SET orderStatus = 'Canceled' "
					+ "WHERE orderStatus IN ('Approved', 'PendingConfirmation', 'WaitingList') "
					+ "AND ADDDATE(TIMESTAMP(visitDate, visitTime), INTERVAL 30 MINUTE) <= NOW()";

			PreparedStatement stmt = conn.prepareStatement(query);
			rowsAffected = stmt.executeUpdate();
			stmt.close();

		} catch (SQLException e) {
			System.out.println("Error executing auto-cancel query.");
			e.printStackTrace();
		}
		return rowsAffected;
	}

	// =========================================================
	// CREATE ORDER
	// =========================================================

	/**
	 * Creates a new order for a park visit and processes its approval based on
	 * availability. The method parses the provided order data, resolves the park
	 * ID, and checks both physical capacity and the existence of an active waiting
	 * list. If space is available and no queue exists, it attempts to generate a
	 * unique 8-character QR code and register the order as 'Approved' (with up to 3
	 * retries in case of a QR collision). If the park is full or a waiting list is
	 * active, it generates alternative available slots.
	 *
	 * @param orderData An {@link ArrayList} of {@link String} containing the order
	 *                  details in the following order: [0] visitorId, [1] parkName,
	 *                  [2] visitDate, [3] visitTime, [4] visitorCount, [5]
	 *                  orderType, [6] email, [7] payment status (must exactly equal
	 *                  "Pay Now" to be marked as paid).
	 * @return A {@link String} indicating the result of the operation: -
	 *         "Approved": If the order was successfully created and approved. -
	 *         "Full|slot1, slot2...": If the requested slot is unavailable,
	 *         returning a comma-separated list of alternative slots. - "Failed": If
	 *         the park is not found, a database error occurs, or insertion fails
	 *         after 3 attempts.
	 */
	public String createNewOrder(ArrayList<String> orderData) {

		String visitorId = orderData.get(0);
		String parkName = orderData.get(1);
		String visitDate = orderData.get(2);
		String visitTime = orderData.get(3);
		int visitorCount = Integer.parseInt(orderData.get(4));
		String orderType = orderData.get(5);
		String email = orderData.get(6);
		boolean paid = orderData.get(7).equals("Pay Now");
		Connection conn = null;

		try {
			conn = pool.getConnection();

			int parkId = -1;

			PreparedStatement find = conn.prepareStatement("SELECT parkId FROM Parks WHERE parkName = ?");
			find.setString(1, parkName);

			ResultSet rs = find.executeQuery();

			if (rs.next()) {
				parkId = rs.getInt("parkId");
			} else {
				rs.close();
				find.close();
				return "Failed";
			}

			rs.close();
			find.close();

			// ==============================================================
			// We check for physical space AND verify no one is waiting in line
			// ==============================================================
			boolean hasRoom = hasRoomInSlot(parkId, visitDate, visitTime, visitorCount);
			boolean queueExists = isWaitingListActive(parkId, visitDate, visitTime);

			// Only allow immediate approval if there is room AND there is no line!
			if (hasRoom && !queueExists) {

				int attempts = 0;

				// Try up to 3 times in case we get a highly unlucky QR collision
				while (attempts < 3) {
					try {
						// Generate a fresh QR code on every attempt!
						String QR = UUID.randomUUID().toString().replace("-", "").substring(0, 8).toUpperCase();

						String insert = "INSERT INTO Orders "
								+ "(parkId, visitorId, visitDate, visitTime, visitorCount, email, orderType, status, QRCode, paid) "
								+ "VALUES (?, ?, ?, ?, ?, ?, ?, 'Approved', ?, ?)";

						PreparedStatement ps = conn.prepareStatement(insert);

						ps.setInt(1, parkId);
						ps.setString(2, visitorId);
						ps.setString(3, visitDate);
						ps.setString(4, visitTime);
						ps.setInt(5, visitorCount);
						ps.setString(6, email);
						ps.setString(7, orderType);
						ps.setString(8, QR);
						ps.setBoolean(9, paid);

						int rows = ps.executeUpdate();
						ps.close();

						if (rows > 0) {
							return "Approved";
						}

						break; // If no error was thrown but 0 rows updated, break to avoid infinite loop

					} catch (java.sql.SQLIntegrityConstraintViolationException e) {
						// This exception specifically catches UNIQUE constraint violations!
						attempts++;
						System.out.println("SERVER: QR collision detected. Retrying... Attempt: " + attempts);
					}
				}

				// If we fail 3 times, or if something else goes wrong
				return "Failed";
			}

			// ==============================================================
			// IF THE PARK IS FULL, OR IF THERE IS A WAITING LIST:
			// Fall down here to generate alternatives and return "Full|"
			// ==============================================================
			ArrayList<String> alternatives = getAlternativeSlots(parkId, visitDate, visitorCount);
			String joined = String.join(", ", alternatives);

			return "Full|" + joined;

		} catch (SQLException e) {
			e.printStackTrace();
			return "Failed";

		} finally {
			if (conn != null) {
				pool.releaseConnection(conn);
			}
		}
	}

	// =========================================================
	// UPDATE ORDER
	// =========================================================

	/**
	 * Updates the visit details of an existing order in the database. This method
	 * modifies the visit date, visit time, and visitor count for a specific order
	 * identified by its unique order ID.
	 *
	 * @param order An {@link Order} object containing the updated visit date, visit
	 *              time, visitor count, and the target order ID to be updated.
	 * @return {@code true} if the order was successfully updated (i.e., at least
	 *         one row was affected), {@code false} if the update failed, the order
	 *         ID was not found, or a database error occurred.
	 */
	public boolean updateOrder(Order order) {
		// The SQL UPDATE statement
		String query = "UPDATE Orders SET visitDate = ?, visitTime = ?, visitorCount = ? WHERE orderId = ?";

		Connection conn = null;
		PreparedStatement pstmt = null;
		boolean isUpdated = false;

		try {
			conn = pool.getConnection();
			pstmt = conn.prepareStatement(query);

			// Inject the values from the Order object into the ? placeholders
			pstmt.setDate(1, order.getVisitDate());
			pstmt.setTime(2, order.getVisitTime());
			pstmt.setInt(3, order.getVisitorCount());
			pstmt.setInt(4, order.getOrderId());

			// executeUpdate() returns the number of rows changed in the DB
			int rowsAffected = pstmt.executeUpdate();

			// If it changed 1 or more rows, the update was successful!
			isUpdated = rowsAffected > 0;

		} catch (SQLException e) {
			System.out.println("Error updating order in database.");
			e.printStackTrace();
		} finally {
			// 1. Close the statement safely
			if (pstmt != null) {
				try {
					pstmt.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			// 2. CRITICAL: Release the connection back to the pool
			if (conn != null) {
				pool.releaseConnection(conn);
			}
		}

		return isUpdated;
	}

	// =========================================================
	// VISITOR ORDERS
	// =========================================================

	/**
	 * Retrieves a list of all orders associated with a specific visitor, sorted by
	 * visit date in descending order.
	 *
	 * @param visitorId The unique identifier of the visitor whose orders are being
	 *                  fetched.
	 * @return An {@link ArrayList} of {@link Order} objects containing the
	 *         visitor's order history. Returns an empty list if no orders are found
	 *         or if a database error occurs.
	 */
	public ArrayList<Order> getVisitorOrders(String visitorId) {
		ArrayList<Order> list = new ArrayList<>();
		String query = "SELECT * FROM Orders WHERE visitorId = ? ORDER BY visitDate DESC";

		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			conn = pool.getConnection();
			ps = conn.prepareStatement(query);
			ps.setString(1, visitorId);
			rs = ps.executeQuery();

			while (rs.next()) {
				int orderId = rs.getInt("orderId");
				Date visitDate = rs.getDate("visitDate");
				Time visitTime = rs.getTime("visitTime");
				String status = rs.getString("status");

				Order order = new Order(orderId, rs.getInt("parkId"), rs.getString("visitorId"), visitDate, visitTime,
						rs.getInt("visitorCount"), rs.getString("email"), rs.getString("orderType"), status,
						rs.getTimestamp("holdUntil"), rs.getTimestamp("reminderUntil"));

				order.setQrCode(rs.getString("QRCode"));
				list.add(order);
			}
		} catch (SQLException e) {
			System.out.println("Error fetching visitor orders.");
			e.printStackTrace();
		} finally {
			if (rs != null)
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			if (ps != null)
				try {
					ps.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			if (conn != null)
				pool.releaseConnection(conn);
		}

		return list;
	}

	// =========================================================
	// EMPLOYEE INFO
	// =========================================================

	/**
	 * Authenticates an employee and retrieves their profile information from the
	 * database. This method queries the Employees table using the provided username
	 * and password. If a match is found, it maps the employee's record into an
	 * ArrayList of Strings.
	 *
	 * @param empData An {@link ArrayList} of {@link String} containing the login
	 *                credentials: [0] username, [1] password.
	 * @return An {@link ArrayList} of {@link String} containing the employee's
	 *         details in the following order: [0] employeeId, [1] firstName, [2]
	 *         lastName, [3] email, [4] username, [5] password, [6] role, [7]
	 *         affiliation. Returns {@code null} if the credentials are invalid, the
	 *         employee is not found, or a database error occurs.
	 */
	public ArrayList<String> getEmployeeInfo(ArrayList<String> empData) {

		String query = "SELECT * FROM Employees WHERE username = ? AND password = ?";

		Connection conn = null;

		try {
			conn = pool.getConnection();

			PreparedStatement ps = conn.prepareStatement(query);
			ps.setString(1, empData.get(0));
			ps.setString(2, empData.get(1));

			ResultSet rs = ps.executeQuery();

			if (rs.next()) {
				ArrayList<String> employeeInfo = new ArrayList<>();

				employeeInfo.add(String.valueOf(rs.getInt("employeeId")));
				employeeInfo.add(rs.getString("firstName"));
				employeeInfo.add(rs.getString("lastName"));
				employeeInfo.add(rs.getString("email"));
				employeeInfo.add(rs.getString("username"));
				employeeInfo.add(rs.getString("password"));
				employeeInfo.add(rs.getString("role"));
				employeeInfo.add(rs.getString("affiliation"));

				rs.close();
				ps.close();

				return employeeInfo;
			}

			rs.close();
			ps.close();

		} catch (SQLException e) {
			e.printStackTrace();

		} finally {
			pool.releaseConnection(conn);
		}

		return null;
	}

	// =========================================================
	// REPORTS - VISIT REPORT
	// =========================================================

	/**
	 * Generates a visit report for a specific park over a given month and year.
	 * This method aggregates the total number of visitors, categorizing them into
	 * individual visitors (RegularGroup) and organized groups (OrganizedGroup)
	 * using conditional SQL aggregation based on their entry times.
	 *
	 * @param parkId The unique identifier of the target park.
	 * @param month  The numerical month for the report (1-12).
	 * @param year   The year for the report (e.g., 2026).
	 * @return A {@link VisitReportData} object containing the aggregated totals for
	 *         individual and group visitors. Returns a default object with zero
	 *         totals if no data is found or if a database error occurs.
	 */
	public VisitReportData getVisitReport(int parkId, int month, int year) {
		String query = "SELECT "
				+ "SUM(CASE WHEN visitType = 'RegularGroup' THEN actualVisitorCount ELSE 0 END) as individualTotal, "
				+ "SUM(CASE WHEN visitType = 'OrganizedGroup' THEN actualVisitorCount ELSE 0 END) as groupTotal "
				+ "FROM Visits " + "WHERE parkId = ? AND YEAR(entryTime) = ? AND MONTH(entryTime) = ?";

		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			conn = pool.getConnection();
			ps = conn.prepareStatement(query);
			ps.setInt(1, parkId);
			ps.setInt(2, year);
			ps.setInt(3, month);

			rs = ps.executeQuery();

			if (rs.next()) {
				int individualVisitors = rs.getInt("individualTotal");
				int groupVisitors = rs.getInt("groupTotal");

				return new VisitReportData(individualVisitors, groupVisitors);
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (rs != null)
				try {
					rs.close();
				} catch (SQLException e) {
				}
			if (ps != null)
				try {
					ps.close();
				} catch (SQLException e) {
				}
			if (conn != null)
				pool.releaseConnection(conn);
		}

		return new VisitReportData(0, 0);
	}

	// =========================================================
	// ENTER VISITOR (RESERVED)
	// =========================================================

	/**
	 * Processes the entry of a visitor with a reserved order into the park. This
	 * method searches for an approved order matching the provided identifier (which
	 * can be an Order ID, Visitor ID, or QR Code) valid for the current date and
	 * within a 30-minute window of the current time. If a valid order is found, it
	 * logs the visit in the Visits table, updates the order status to 'Entered',
	 * and updates the park's current capacity. Additionally, if the order has not
	 * been paid for, it calculates the required entry fee dynamically using the
	 * PricingService.
	 *
	 * @param identifier A {@link String} representing the visitor's identification.
	 *                   This can be an Order ID (numeric), a Visitor ID, or a QR
	 *                   Code.
	 * @return A {@link String} indicating the result of the entry process: -
	 *         "Success": If the entry was logged successfully and the order was
	 *         already paid. - "Success_Pay_{price}_{orderId}": If the entry was
	 *         logged successfully but payment is required upon entry. - "Order not
	 *         found or invalid time window.": If no matching, valid order is found.
	 *         - "Database error.": If an SQL exception occurs during execution.
	 */
	public String enterVisitor(String identifier) {
		int searchOrderId = -1;
		try {
			searchOrderId = Integer.parseInt(identifier);
		} catch (NumberFormatException e) {
			// not an integer
		}

		String selectQuery = "SELECT parkId, orderId, visitorCount, visitorId, paid, orderType FROM Orders "
				+ "WHERE (orderId = ? OR visitorId = ? OR QRCode = ?) AND status = 'Approved' "
				+ "AND visitDate = CURDATE() "
				+ "AND visitTime BETWEEN SUBTIME(CURTIME(), '00:30:00') AND ADDTIME(CURTIME(), '00:30:00') "
				+ "ORDER BY orderId DESC LIMIT 1";

		String insertQuery = "INSERT INTO Visits (parkId, orderId, visitorId, actualVisitorCount, entryTime, exitTime, currentlyIn, visitType) "
				+ "VALUES (?, ?, ?, ?, NOW(), NULL, ?, ?)";

		String updateOrderQuery = "UPDATE Orders SET status = 'Entered' WHERE orderId = ?";

		Connection conn = null;
		PreparedStatement psSelect = null;
		PreparedStatement psInsert = null;
		PreparedStatement psUpdateOrder = null;
		ResultSet rs = null;

		try {
			conn = pool.getConnection();

			psSelect = conn.prepareStatement(selectQuery);
			psSelect.setInt(1, searchOrderId);
			psSelect.setString(2, identifier);
			psSelect.setString(3, identifier);
			rs = psSelect.executeQuery();

			if (rs.next()) {
				int parkId = rs.getInt("parkId");
				int orderId = rs.getInt("orderId");
				int visitorCount = rs.getInt("visitorCount");

				String actualVisitorId = rs.getString("visitorId");
				int paid = rs.getInt("paid");
				String orderType = rs.getString("orderType");

				psInsert = conn.prepareStatement(insertQuery);
				psInsert.setInt(1, parkId);
				psInsert.setInt(2, orderId);
				psInsert.setString(3, actualVisitorId);
				psInsert.setInt(4, visitorCount);
				psInsert.setInt(5, visitorCount);

				if ("OrganizedGroup".equals(orderType)) {
					psInsert.setString(6, "OrganizedGroup");
				} else {
					psInsert.setString(6, "RegularGroup");
				}

				int rows = psInsert.executeUpdate();

				if (rows > 0) {
					psUpdateOrder = conn.prepareStatement(updateOrderQuery);
					psUpdateOrder.setInt(1, orderId);
					psUpdateOrder.executeUpdate();

					updateVisitorCount(parkId, visitorCount, conn);

					if (paid == 0) {
						String visitorType = getVisitorTypeById(actualVisitorId);
						String visitType = "REGULAR_PREORDER";
						boolean subscriber = false;

						if ("Guide".equals(visitorType)) {
							visitType = "GUIDE_PREORDER";
						} else if ("Subscriber".equals(visitorType)) {
							visitType = "REGULAR_PREORDER";
							subscriber = true;
						}

						PricingService pricingService = new PricingService();
						double price = pricingService.calculatePrice(visitType, visitorCount, false, subscriber, parkId,
								LocalDate.now(), EchoServer.instance);

						return "Success_Pay_" + price + "_" + orderId;
					}
					return "Success";
				}
			}

			return "Order not found or invalid time window.";

		} catch (SQLException e) {
			e.printStackTrace();
			return "Database error.";
		}
	}

	// =========================================================
	// EXIT VISITOR (Supports multiple active visits for same ID)
	// =========================================================

	/**
	 * Processes the exit of a visitor or a group of visitors from a specific park.
	 * This method supports partial exits and gracefully handles scenarios where
	 * multiple active visits are associated with the same identifier (e.g., a guide
	 * managing several groups). It utilizes database transactions to guarantee data
	 * consistency, distributing the exiting amount across active visits (oldest
	 * first). Fully exited visits are timestamped, and their associated orders are
	 * marked as 'Fulfilled'.
	 *
	 * @param identifier    A {@link String} representing the visitor's
	 *                      identification (Order ID, Visitor ID, or QR Code).
	 * @param parkId        The unique identifier of the park from which the
	 *                      visitors are exiting.
	 * @param exitingAmount The number of visitors attempting to exit.
	 * @return A {@link String} indicating the result of the exit transaction: -
	 *         "Success: All associated groups exited completely.": If all visitors
	 *         associated with the ID have exited. - "Success: {amount} exited.
	 *         {remaining} remaining...": If a partial exit was successfully
	 *         processed. - "Error: No active visit found...": If no active entries
	 *         match the identifier. - "Error: Cannot exit {amount} people...": If
	 *         the exiting amount exceeds the number of people currently inside. -
	 *         "Error: Failed to safely update park counts." / "Database
	 *         exception...": If the transaction fails and rolls back.
	 */
	public String exitVisitor(String identifier, int parkId, int exitingAmount) {
		// Attempt to parse the input as an integer for orderId
		int searchOrderId = -1;
		try {
			searchOrderId = Integer.parseInt(identifier);
		} catch (NumberFormatException e) {
			// Not a valid integer, meaning it's likely a Visitor ID or QR code
		}

		// SELECT query: Fetch ALL active visits for this identifier, ORDER BY visitId
		// ASC (Oldest first). No LIMIT 1.
		String selectQuery = "SELECT V.visitId, V.currentlyIn, V.orderId, V.visitorId "
				+ "FROM Visits V LEFT JOIN Orders O ON V.orderId = O.orderId "
				+ "WHERE V.parkId = ? AND V.currentlyIn > 0 AND V.exitTime IS NULL "
				+ "AND (V.orderId = ? OR V.visitorId = ? OR O.QRCode = ?) " + "ORDER BY V.visitId ASC";

		Connection conn = null;
		PreparedStatement psSelect = null;

		// Pre-compile update statements for reuse in the loop
		PreparedStatement psUpdateVisitFull = null;
		PreparedStatement psUpdateVisitPartial = null;
		PreparedStatement psUpdateOrder = null;
		ResultSet rs = null;

		// A local class to hold the fetched visit data
		class ActiveVisit {
			int visitId;
			int currentlyIn;
			int orderId;
			boolean isCasual;

			ActiveVisit(int visitId, int currentlyIn, int orderId, boolean isCasual) {
				this.visitId = visitId;
				this.currentlyIn = currentlyIn;
				this.orderId = orderId;
				this.isCasual = isCasual;
			}
		}

		try {
			conn = pool.getConnection();
			conn.setAutoCommit(false); // Start transaction

			psSelect = conn.prepareStatement(selectQuery);
			psSelect.setInt(1, parkId);
			psSelect.setInt(2, searchOrderId);
			psSelect.setString(3, identifier);
			psSelect.setString(4, identifier);
			rs = psSelect.executeQuery();

			List<ActiveVisit> activeVisits = new ArrayList<>();
			int totalCurrentlyIn = 0;

			// 1. Gather all active visits and calculate total available capacity
			while (rs.next()) {
				int visitId = rs.getInt("visitId");
				int currentlyIn = rs.getInt("currentlyIn");
				int orderId = rs.getInt("orderId");
				boolean isCasual = rs.wasNull();

				activeVisits.add(new ActiveVisit(visitId, currentlyIn, orderId, isCasual));
				totalCurrentlyIn += currentlyIn;
			}

			// 2. Validate we have visits and enough people to exit
			if (activeVisits.isEmpty()) {
				return "Error: No active visit found for this identifier in this park.";
			}

			if (exitingAmount > totalCurrentlyIn) {
				return "Error: Cannot exit " + exitingAmount + " people. Only " + totalCurrentlyIn + " are inside.";
			}

			// 3. Prepare the update statements
			psUpdateVisitFull = conn
					.prepareStatement("UPDATE Visits SET currentlyIn = 0, exitTime = NOW() WHERE visitId = ?");
			psUpdateVisitPartial = conn.prepareStatement("UPDATE Visits SET currentlyIn = ? WHERE visitId = ?");
			psUpdateOrder = conn.prepareStatement("UPDATE Orders SET status = 'Fulfilled' WHERE orderId = ?");

			// 4. Loop through the visits (oldest first) and distribute the exit amount
			int remainingToExit = exitingAmount;

			for (ActiveVisit visit : activeVisits) {
				if (remainingToExit <= 0) {
					break; // We've successfully exited the required amount of people
				}

				// Determine how many to exit from THIS specific visit row
				int exitingFromThisVisit = Math.min(remainingToExit, visit.currentlyIn);
				int remainingInThisVisit = visit.currentlyIn - exitingFromThisVisit;
				boolean isFullExit = (remainingInThisVisit == 0);

				if (isFullExit) {
					// Fully exit this specific group
					psUpdateVisitFull.setInt(1, visit.visitId);
					psUpdateVisitFull.executeUpdate();

					// Fulfill the order if it wasn't casual
					if (!visit.isCasual) {
						psUpdateOrder.setInt(1, visit.orderId);
						psUpdateOrder.executeUpdate();
					}
				} else {
					// Partially exit this group
					psUpdateVisitPartial.setInt(1, remainingInThisVisit);
					psUpdateVisitPartial.setInt(2, visit.visitId);
					psUpdateVisitPartial.executeUpdate();
				}

				// Deduct what we just processed from the total remaining amount
				remainingToExit -= exitingFromThisVisit;
			}

			// 5. Update overall park capacity once at the end
			boolean isCountUpdated = updateVisitorCount(parkId, -exitingAmount, conn);

			// 6. Commit or Rollback transaction
			if (isCountUpdated) {
				conn.commit();
				int newTotalInPark = totalCurrentlyIn - exitingAmount;
				if (newTotalInPark == 0) {
					return "Success: All associated groups exited completely.";
				} else {
					return "Success: " + exitingAmount + " exited. " + newTotalInPark
							+ " remaining associated with this ID.";
				}
			} else {
				conn.rollback();
				return "Error: Failed to safely update park counts.";
			}

		} catch (SQLException e) {
			System.err.println("[DB ERROR] Exception during visitor exit process.");
			e.printStackTrace();
			if (conn != null) {
				try {
					conn.rollback();
				} catch (SQLException ex) {
					ex.printStackTrace();
				}
			}
			return "Error: Database exception occurred.";
		} finally {
			// Clean up all resources
			try {
				if (rs != null)
					rs.close();
				if (psSelect != null)
					psSelect.close();
				if (psUpdateVisitFull != null)
					psUpdateVisitFull.close();
				if (psUpdateVisitPartial != null)
					psUpdateVisitPartial.close();
				if (psUpdateOrder != null)
					psUpdateOrder.close();

				if (conn != null) {
					conn.setAutoCommit(true);
					pool.releaseConnection(conn);
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	// =========================================================
	// REGISTER FAMILY SUBSCRIBER
	// =========================================================

	/**
	 * Registers a new family subscriber or upgrades an existing casual visitor to a
	 * subscriber status. This method first determines the next available
	 * subscription number. It then performs an "upsert" (Insert or Update)
	 * operation: if the visitor ID already exists in the database, their details
	 * are updated, and their type is changed to 'Subscriber' while retaining their
	 * existing subscription number if they have one. If the visitor is new, a
	 * completely new record is created.
	 *
	 * @param data An {@link ArrayList} of {@link String} containing the
	 *             subscriber's details in the following order: [0] visitorId, [1]
	 *             firstName, [2] lastName, [3] phone, [4] email, [5] familyMembers
	 *             (numeric string), [6] creditCard (can be null or empty).
	 * @return {@code true} if the registration or update was successful (rows
	 *         affected > 0), {@code false} if the operation failed or a database
	 *         error occurred.
	 */
	public synchronized boolean registerFamilySubscriber(ArrayList<String> data) {

		String getNextSubQuery = "SELECT IFNULL(MAX(subscriptionNumber), 10000) + 1 AS nextSub FROM Visitors";

		// Use ON DUPLICATE KEY UPDATE to insert or update if visitorId already exists
		String insertQuery = "INSERT INTO Visitors "
				+ "(visitorId, firstName, lastName, phone, email, visitorType, subscriptionNumber, familyMembers, creditCard) "
				+ "VALUES (?, ?, ?, ?, ?, 'Subscriber', ?, ?, ?) " + "ON DUPLICATE KEY UPDATE "
				+ "firstName = VALUES(firstName), " + "lastName = VALUES(lastName), " + "phone = VALUES(phone), "
				+ "email = VALUES(email), " + "visitorType = 'Subscriber', "
				+ "subscriptionNumber = IFNULL(subscriptionNumber, VALUES(subscriptionNumber)), " // Keep existing sub
																									// number if they
																									// already have one
				+ "familyMembers = VALUES(familyMembers), " + "creditCard = VALUES(creditCard)";

		Connection conn = null;

		try {
			conn = pool.getConnection();

			int nextSubscriptionNumber = 10001;

			PreparedStatement ps1 = conn.prepareStatement(getNextSubQuery);
			ResultSet rs = ps1.executeQuery();

			if (rs.next()) {
				nextSubscriptionNumber = rs.getInt("nextSub");
			}

			rs.close();
			ps1.close();

			PreparedStatement ps2 = conn.prepareStatement(insertQuery);

			ps2.setString(1, data.get(0)); // visitorId
			ps2.setString(2, data.get(1)); // firstName
			ps2.setString(3, data.get(2)); // lastName
			ps2.setString(4, data.get(3)); // phone
			ps2.setString(5, data.get(4)); // email
			ps2.setInt(6, nextSubscriptionNumber); // subscriptionNumber
			ps2.setInt(7, Integer.parseInt(data.get(5))); // familyMembers

			if (data.get(6) == null || data.get(6).isEmpty()) {
				ps2.setNull(8, java.sql.Types.VARCHAR);
			} else {
				ps2.setString(8, data.get(6)); // creditCard
			}

			int rows = ps2.executeUpdate();
			ps2.close();

			// row count can be 1 for insert, 2 for update in MySQL
			return rows > 0;

		} catch (SQLException e) {
			e.printStackTrace();
			return false;

		} finally {
			pool.releaseConnection(conn);
		}
	}

	// =========================================================
	// REGISTER GROUP GUIDE
	// =========================================================

	/**
	 * Registers a new group guide or updates an existing visitor to a guide status.
	 * This method utilizes an "upsert" (Insert or Update) operation. If the visitor
	 * ID already exists in the system, their personal details are updated, their
	 * type is forced to 'Guide', and any previous subscription data is reset
	 * (subscriptionNumber and creditCard are set to NULL, and familyMembers is
	 * reset to 1). If the visitor is new, a fresh guide record is created.
	 *
	 * @param data An {@link ArrayList} of {@link String} containing the guide's
	 *             details in the following order: [0] visitorId, [1] firstName, [2]
	 *             lastName, [3] phone, [4] email.
	 * @return {@code true} if the registration or update was successful (rows
	 *         affected > 0), {@code false} if the operation failed or a database
	 *         error occurred.
	 */
	public boolean registerGroupGuide(ArrayList<String> data) {

		// Use ON DUPLICATE KEY UPDATE to insert or update if visitorId already exists
		String insertQuery = "INSERT INTO Visitors "
				+ "(visitorId, firstName, lastName, phone, email, visitorType, subscriptionNumber, familyMembers, creditCard) "
				+ "VALUES (?, ?, ?, ?, ?, 'Guide', NULL, 1, NULL) " + "ON DUPLICATE KEY UPDATE "
				+ "firstName = VALUES(firstName), " + "lastName = VALUES(lastName), " + "phone = VALUES(phone), "
				+ "email = VALUES(email), " + "visitorType = 'Guide', " + "subscriptionNumber = NULL, "
				+ "familyMembers = 1, " + "creditCard = NULL";

		Connection conn = null;

		try {
			conn = pool.getConnection();

			PreparedStatement ps = conn.prepareStatement(insertQuery);

			ps.setString(1, data.get(0)); // visitorId
			ps.setString(2, data.get(1)); // firstName
			ps.setString(3, data.get(2)); // lastName
			ps.setString(4, data.get(3)); // phone
			ps.setString(5, data.get(4)); // email

			int rows = ps.executeUpdate();
			ps.close();

			// row count can be 1 for insert, 2 for update in MySQL
			return rows > 0;

		} catch (SQLException e) {
			e.printStackTrace();
			return false;

		} finally {
			pool.releaseConnection(conn);
		}
	}

	// =========================================================
	// SUBMIT PARK REQUEST
	// =========================================================

	/**
	 * Submits a new configuration or promotion request for a specific park. The
	 * method first resolves the park's ID based on the provided park name. It then
	 * records the request in the Requests table with a default 'Pending' status. If
	 * the request type is identified as a "Promotion", it also parses and stores
	 * the promotion's active date range.
	 *
	 * @param data An {@link ArrayList} of {@link String} containing the request
	 *             details in the following order: [0] parkName, [1] requestType,
	 *             [2] oldValue, [3] newValue. If the requestType is "Promotion",
	 *             the list must also contain: [4] startDate, [5] endDate (in
	 *             ISO-8601 format).
	 * @return {@code true} if the request was successfully submitted (rows affected
	 *         > 0), {@code false} if the park name was not found, if parsing
	 *         failed, or if a database error occurred.
	 */
	public boolean submitParkRequest(ArrayList<String> data) {

		String parkName = data.get(0);
		String requestType = data.get(1);
		String oldValue = data.get(2);
		String newValue = data.get(3);

		LocalDate startDate = null;
		LocalDate endDate = null;

		Connection conn = null;
		PreparedStatement findPark = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		boolean isSuccess = false;

		try {
			// Moved date parsing inside the try block to safely catch
			// DateTimeParseException
			if ("Promotion".equals(requestType)) {
				startDate = LocalDate.parse(data.get(4));
				endDate = LocalDate.parse(data.get(5));
			}

			conn = pool.getConnection();

			findPark = conn.prepareStatement("SELECT parkId FROM Parks WHERE parkName = ?");
			findPark.setString(1, parkName);

			rs = findPark.executeQuery();

			int parkId = -1;
			if (rs.next()) {
				parkId = rs.getInt("parkId");
			} else {
				return false; // Exit early if park is not found (finally block will still execute!)
			}

			String insertQuery = "INSERT INTO Requests (parkId, requestType, oldValue, newValue, status, startDate, endDate) "
					+ "VALUES (?, ?, ?, ?, 'Pending', ?, ?)";

			ps = conn.prepareStatement(insertQuery);
			ps.setInt(1, parkId);
			ps.setString(2, requestType);
			ps.setString(3, oldValue);
			ps.setString(4, newValue);

			if (startDate != null) {
				ps.setDate(5, java.sql.Date.valueOf(startDate));
			} else {
				ps.setNull(5, java.sql.Types.DATE);
			}

			if (endDate != null) {
				ps.setDate(6, java.sql.Date.valueOf(endDate));
			} else {
				ps.setNull(6, java.sql.Types.DATE);
			}

			int rows = ps.executeUpdate();
			isSuccess = rows > 0;

		} catch (Exception e) {
			// Catches both SQLException and general Exceptions (like
			// DateTimeParseException)
			e.printStackTrace();
			return false;

		} finally {
			// Safe cleanup of all resources
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (findPark != null) {
				try {
					findPark.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (ps != null) {
				try {
					ps.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (conn != null) {
				pool.releaseConnection(conn);
			}
		}

		return isSuccess;
	}

	// =========================================================
	// GET PARK CURRENT VALUE
	// =========================================================
	/**
	 * Retrieves a specific current value for a park based on the requested property
	 * type. * @param parkName The name of the park to query.
	 * 
	 * @param requestType The type of data to retrieve (e.g., "MaxCapacity",
	 *                    "CasualGap", "AvgStayDuration", "CurrentVisitorCount",
	 *                    "OpenCasualSpots").
	 * @return The requested value as a {@link String}, or {@code null} if the park
	 *         is not found, the request type is invalid, or a database error
	 *         occurs.
	 */
	public String getParkCurrentValue(String parkName, String requestType) {

		Connection conn = null;

		// 1. Fix hidden spaces issues!
		parkName = parkName.trim();
		requestType = requestType.trim();

		System.out.println(
				"[DEBUG] getParkCurrentValue called! Park: [" + parkName + "], Request: [" + requestType + "]");

		try {
			conn = pool.getConnection();

			String columnName;

			switch (requestType) {
			case "MaxCapacity":
				columnName = "maxCapacity";
				break;
			case "CasualGap":
				columnName = "casualGap";
				break;
			case "AvgStayDuration":
				columnName = "avgStayDuration";
				break;
			case "CurrentVisitorCount":
				columnName = "CurrentVisitorCount";
				break;
			case "OpenCasualSpots":
				// The correct formula!
				columnName = "GREATEST((maxCapacity - CurrentVisitorCount), 0)";
				break;
			case "Promotion":
				return "Promotion request";
			default:
				System.out.println("[DEBUG] ERROR: requestType fell into DEFAULT block!");
				return null;
			}

			String query = "SELECT " + columnName + " FROM Parks WHERE parkName = ?";

			PreparedStatement ps = conn.prepareStatement(query);
			ps.setString(1, parkName);

			ResultSet rs = ps.executeQuery();

			if (rs.next()) {
				String value = rs.getString(1);
				rs.close();
				ps.close();
				return value;
			} else {
				System.out.println("[DEBUG] ERROR: rs.next() is false! Could not find park: [" + parkName + "] in DB.");
			}

			rs.close();
			ps.close();

		} catch (SQLException e) {
			e.printStackTrace();

		} finally {
			if (conn != null) {
				pool.releaseConnection(conn);
			}
		}

		return null;
	}

	// =========================================================
	// GET PARK ID BY NAME
	// =========================================================
	/**
	 * Retrieves the unique park ID for a given park name from the database. The
	 * search is performed in a case-insensitive manner using trimmed input strings.
	 *
	 * @param parkName The name of the park to search for.
	 * @return The unique park ID as an {@code int}, or {@code -1} if the park is
	 *         not found or the input is null.
	 */
	public int getParkIdByName(String parkName) {
		if (parkName == null)
			return -1;

		String query = "SELECT parkId FROM Parks WHERE LOWER(TRIM(parkName)) = LOWER(TRIM(?))";

		Connection conn = null;
		try {
			conn = pool.getConnection();

			PreparedStatement ps = conn.prepareStatement(query);
			ps.setString(1, parkName);

			ResultSet rs = ps.executeQuery();

			if (rs.next()) {
				return rs.getInt("parkId");
			}

			rs.close();
			ps.close();

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (conn != null) {
				pool.releaseConnection(conn);
			}
		}

		return -1;
	}

	// =========================================================
	// USAGE REPORT
	// =========================================================
	/**
	 * Generates a usage report for a specific park, month, and year. It calculates
	 * the peak visitor count and whether the park reached its maximum capacity for
	 * each day of the month by simulating minute-by-minute visitor activity.
	 *
	 * @param parkName The name of the park.
	 * @param month    The month for which to generate the report.
	 * @param year     The year for which to generate the report.
	 * @return An {@link ArrayList} of {@link UsageReportData} objects, each
	 *         containing the day of the month, the peak visitor count for that day,
	 *         and a flag indicating if maximum capacity was reached. Returns an
	 *         empty list if the park is not found or an error occurs.
	 */
	public ArrayList<UsageReportData> getUsageReport(String parkName, int month, int year) {

		ArrayList<UsageReportData> result = new ArrayList<>();
		Connection conn = null;

		try {
			conn = pool.getConnection();

			int parkId = getParkIdByName(parkName);
			if (parkId == -1) {
				return result;
			}

			String sql = "SELECT v.entryTime, v.exitTime, v.actualVisitorCount, p.maxCapacity " + "FROM Visits v "
					+ "JOIN Parks p ON v.parkId = p.parkId " + "WHERE v.parkId = ? " + "AND YEAR(v.entryTime) = ? "
					+ "AND MONTH(v.entryTime) = ? " + "ORDER BY v.entryTime";

			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setInt(1, parkId);
			ps.setInt(2, year);
			ps.setInt(3, month);

			ResultSet rs = ps.executeQuery();

			List<VisitRecord> visits = new ArrayList<>();
			int maxCapacity = 0;

			while (rs.next()) {

				LocalDateTime entry = rs.getTimestamp("entryTime").toLocalDateTime();
				LocalDateTime exit = rs.getTimestamp("exitTime").toLocalDateTime();
				int count = rs.getInt("actualVisitorCount");

				maxCapacity = rs.getInt("maxCapacity");

				visits.add(new VisitRecord(entry, exit, count));
			}

			rs.close();
			ps.close();

			Map<Integer, Integer> dayToPeak = new HashMap<>();
			Map<Integer, Boolean> dayToFull = new HashMap<>();

			YearMonth ym = YearMonth.of(year, month);
			int daysInMonth = ym.lengthOfMonth();

			if (visits.isEmpty()) {
				for (int day = 1; day <= daysInMonth; day++) {
					result.add(new UsageReportData(day, 0, false));
				}
				return result;
			}

			for (int day = 1; day <= daysInMonth; day++) {

				int peak = 0;
				boolean full = false;

				LocalDateTime base = LocalDateTime.of(year, month, day, 0, 0);

				int startMinute = 9 * 60;
				int endMinute = 17 * 60;

				for (int minute = startMinute; minute < endMinute; minute++) {

					LocalDateTime t = base.plusMinutes(minute);

					int current = 0;

					for (VisitRecord v : visits) {

						if (!v.entry.isAfter(t) && v.exit.isAfter(t)) {
							current += v.count;
						}
					}

					peak = Math.max(peak, current);

					if (current >= maxCapacity) {
						full = true;
					}
				}

				dayToPeak.put(day, peak);
				dayToFull.put(day, full);
			}

			for (int day = 1; day <= daysInMonth; day++) {

				result.add(
						new UsageReportData(day, dayToPeak.getOrDefault(day, 0), dayToFull.getOrDefault(day, false)));
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (conn != null) {
				pool.releaseConnection(conn);
			}
		}

		return result;
	}

	// =========================================================
	// VISIT DURATION
	// =========================================================
	/**
	 * Retrieves all completed visit records for a specific park, month, and year to
	 * support duration reporting. Only visits with an exit time are included.
	 *
	 * @param parkId The unique identifier of the park.
	 * @param month  The month for which to retrieve data.
	 * @param year   The year for which to retrieve data.
	 * @return An {@link ArrayList} of {@link Visit} objects representing the
	 *         completed visits, or an empty list if none are found or a database
	 *         error occurs.
	 */
	public ArrayList<Visit> getVisitDurationReport(int parkId, int month, int year) {

		ArrayList<Visit> result = new ArrayList<>();
		Connection conn = null;

		String query = "SELECT * " + "FROM Visits v " + "WHERE v.parkId = ? " + "AND YEAR(v.entryTime) = ? "
				+ "AND MONTH(v.entryTime) = ? " + "AND v.exitTime IS NOT NULL";

		try {
			conn = pool.getConnection();

			PreparedStatement ps = conn.prepareStatement(query);
			ps.setInt(1, parkId);
			ps.setInt(2, year);
			ps.setInt(3, month);

			ResultSet rs = ps.executeQuery();

			while (rs.next()) {

				Visit visit = new Visit(rs.getInt("visitId"), rs.getInt("parkId"), rs.getInt("orderId"),
						rs.getString("visitorId"), rs.getInt("actualVisitorCount"), rs.getTimestamp("entryTime"),
						rs.getTimestamp("exitTime"), rs.getString("visitType"));

				result.add(visit);
			}

			rs.close();
			ps.close();

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (conn != null) {
				pool.releaseConnection(conn);
			}
		}

		return result;
	}

	// =========================================================
	// REPORTS - CANCELLATION
	// =========================================================
	/**
	 * Generates a cancellation report for a specific park, month, and year,
	 * aggregating the total number of cancellations per day.
	 *
	 * @param parkId The unique identifier of the park.
	 * @param month  The month for which to generate the report.
	 * @param year   The year for which to generate the report.
	 * @return An {@link ArrayList} of {@link CancellationReportData} objects, each
	 *         containing the day of the month and the number of cancellations, or
	 *         an empty list if no data is found or an error occurs.
	 */
	public ArrayList<CancellationReportData> getCancellationReport(int parkId, int month, int year) {

		ArrayList<CancellationReportData> result = new ArrayList<>();
		Connection conn = null;

		String query = "SELECT DAY(visitDate) AS dayOfMonth, COUNT(*) AS cancellations " + "FROM Orders "
				+ "WHERE parkId = ? " + "AND status = 'Canceled' " + "AND YEAR(visitDate) = ? "
				+ "AND MONTH(visitDate) = ? " + "GROUP BY DAY(visitDate) " + "ORDER BY dayOfMonth";

		try {
			conn = pool.getConnection();

			PreparedStatement ps = conn.prepareStatement(query);
			ps.setInt(1, parkId);
			ps.setInt(2, year);
			ps.setInt(3, month);

			ResultSet rs = ps.executeQuery();

			while (rs.next()) {

				int day = rs.getInt("dayOfMonth");
				double cancellations = rs.getDouble("cancellations");

				result.add(new CancellationReportData(day, cancellations));
			}

			rs.close();
			ps.close();

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (conn != null) {
				pool.releaseConnection(conn);
			}
		}

		return result;
	}

	// =========================================================
	// GET PARK NAMES
	// =========================================================
	/**
	 * Retrieves a list of all park names from the database, sorted alphabetically.
	 *
	 * @return An {@link ArrayList} of {@link String} containing the names of all
	 *         parks, or an empty list if no parks are found or a database error
	 *         occurs.
	 */
	public ArrayList<String> getAllParkNames() {

		ArrayList<String> parks = new ArrayList<>();

		String query = "SELECT parkName FROM Parks ORDER BY parkName";

		Connection conn = null;

		try {
			conn = pool.getConnection();

			PreparedStatement ps = conn.prepareStatement(query);
			ResultSet rs = ps.executeQuery();

			while (rs.next()) {
				parks.add(rs.getString("parkName"));
			}

			rs.close();
			ps.close();

		} catch (SQLException e) {
			e.printStackTrace();

		} finally {
			if (conn != null) {
				pool.releaseConnection(conn);
			}
		}

		return parks;
	}

	// =========================================================
	// CREATE NEW CASUAL VISIT
	// =========================================================
	/**
	 * Registers a new casual visit in the system. Checks for available capacity
	 * before inserting the visit record and updating the current visitor count for
	 * the park.
	 *
	 * @param parkName     The name of the park.
	 * @param visitorId    The unique identifier of the visitor.
	 * @param visitorCount The number of visitors for the casual visit.
	 * @return {@code true} if the visit was successfully registered and the visitor
	 *         count updated, {@code false} otherwise.
	 */
	public boolean createCasualVisit(String parkName, String visitorId, int visitorCount) {

		// Fetch the OpenCasualSpots using the helper method
		String openSpotsStr = getParkCurrentValue(parkName, "OpenCasualSpots");

		if (openSpotsStr == null) {
			System.err.println("[DB ERROR] Could not fetch OpenCasualSpots for park: " + parkName);
			return false;
		}

		int openCasualSpots;
		try {
			openCasualSpots = Integer.parseInt(openSpotsStr);
		} catch (NumberFormatException e) {
			System.err.println("[ERROR] Failed to parse OpenCasualSpots.");
			e.printStackTrace();
			return false;
		}

		// Check if the requested visitor count exceeds the available casual spots
		if (visitorCount > openCasualSpots) {
			System.out.println("[INFO] Casual visit denied: Not enough open casual spots. Requested: " + visitorCount
					+ ", Available: " + openCasualSpots);
			return false;
		}

		int parkId = getParkIdByName(parkName);

		if (parkId == -1) {
			System.err.println("[DB ERROR] Could not register casual visit: Park name '" + parkName + "' not found.");
			return false;
		}

		String query = "INSERT INTO Visits (parkId, orderId, visitorId, actualVisitorCount, entryTime, exitTime, currentlyIn) "
				+ "VALUES (?, NULL, ?, ?, NOW(), NULL , ?)";

		Connection conn = null;
		PreparedStatement ps = null;

		try {
			conn = pool.getConnection();
			ps = conn.prepareStatement(query);

			ps.setInt(1, parkId);
			ps.setString(2, visitorId);
			ps.setInt(3, visitorCount);
			ps.setInt(4, visitorCount);
			int rowsAffected = ps.executeUpdate();

			if (rowsAffected > 0) {
				// The visit was inserted successfully, update the current visitor count
				// NOTE: updateCurrentVisitorCount should also DECREASE OpenCasualSpots
				boolean isCountUpdated = updateVisitorCount(parkId, visitorCount, conn);

				if (!isCountUpdated) {
					System.err.println(
							"[DB ERROR] Visit registered, but failed to update current visitor count in Parks table.");
				}

				return isCountUpdated;
			}

			return false;

		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		} finally {

			if (ps != null) {
				try {
					ps.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (conn != null) {
				pool.releaseConnection(conn);
			}
		}
	}

	// =========================================================
	// GET OCCUPIED VISITOR COUNT FOR SLOT (Updated to include Pending)
	// =========================================================
	/**
	 * Calculates the total number of visitors currently booked for a specific park
	 * and time slot, optionally excluding a specific order from the total count.
	 *
	 * @param parkId         The unique identifier of the park.
	 * @param visitDate      The date of the visit.
	 * @param visitTime      The time slot of the visit.
	 * @param excludeOrderId An order ID to exclude from the total count (use -1 to
	 *                       include all orders).
	 * @return The total number of visitors booked for the slot as an {@code int},
	 *         or {@code 0} if no visitors are found or an error occurs.
	 */
	private int getOccupiedVisitorCountForSlot(int parkId, String visitDate, String visitTime, int excludeOrderId) {
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			conn = pool.getConnection();

			String query = "SELECT SUM(visitorCount) AS total FROM Orders "
					+ "WHERE parkId = ? AND visitDate = ? AND visitTime = ? "
					+ "AND status IN ('Approved', 'PendingConfirmation', 'PendingVisitReminder', 'Entered')";

			if (excludeOrderId != -1) {
				query += " AND orderId != ?";
			}

			ps = conn.prepareStatement(query);
			ps.setInt(1, parkId);
			ps.setString(2, visitDate);
			ps.setString(3, visitTime);

			if (excludeOrderId != -1) {
				ps.setInt(4, excludeOrderId);
			}

			rs = ps.executeQuery();
			if (rs.next()) {
				return rs.getInt("total");
			}

		} catch (SQLException e) {
			System.out.println("Error fetching occupied visitors.");
			e.printStackTrace();
		} finally {
			try {
				if (rs != null)
					rs.close();
				if (ps != null)
					ps.close();
			} catch (SQLException e) {
			}
			if (conn != null)
				pool.releaseConnection(conn);
		}
		return 0;
	}

	// =========================================================
	// GET PARK MAX CAPACITY
	// =========================================================
	/**
	 * Retrieves the maximum capacity for a specific park from the database.
	 *
	 * @param parkId The unique identifier of the park.
	 * @return The maximum capacity as an {@code int}, or {@code -1} if the park is
	 *         not found or a database error occurs.
	 */
	private int getParkMaxCapacity(int parkId) {
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			conn = pool.getConnection();
			// Ensure it matches your column name perfectly
			ps = conn.prepareStatement("SELECT maxCapacity FROM Parks WHERE parkId = ?");
			ps.setInt(1, parkId);
			rs = ps.executeQuery();
			if (rs.next()) {
				return rs.getInt("maxCapacity");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null)
					rs.close();
				if (ps != null)
					ps.close();
			} catch (SQLException e) {
			}
			if (conn != null)
				pool.releaseConnection(conn);
		}
		return -1;
	}

	// =========================================================
	// GET PARK CASUAL GAP
	// =========================================================
	/**
	 * Retrieves the casual gap (the buffer of reserved spots for non-booked
	 * visitors) for a specific park from the database.
	 *
	 * @param parkId The unique identifier of the park.
	 * @return The casual gap value as an {@code int}, or {@code -1} if the park is
	 *         not found or a database error occurs.
	 */
	private int getParkCasualGap(int parkId) {
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			conn = pool.getConnection();
			// Ensure it matches your column name perfectly
			ps = conn.prepareStatement("SELECT casualGap FROM Parks WHERE parkId = ?");
			ps.setInt(1, parkId);
			rs = ps.executeQuery();
			if (rs.next()) {
				return rs.getInt("casualGap");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null)
					rs.close();
				if (ps != null)
					ps.close();
			} catch (SQLException e) {
			}
			if (conn != null)
				pool.releaseConnection(conn);
		}
		return -1;
	}

	// =========================================================
	// CHECK IF THERE IS ROOM IN SLOT (Supports Exclusions)
	// =========================================================
	/**
	 * Determines if a park has enough remaining capacity for a requested number of
	 * visitors in a specific time slot, optionally excluding a specific order from
	 * the calculation.
	 *
	 * @param parkId            The unique identifier of the park.
	 * @param visitDate         The date of the visit.
	 * @param visitTime         The time slot of the visit.
	 * @param requestedVisitors The number of visitors to check capacity for.
	 * @param excludeOrderId    An order ID to ignore in the occupied visitor count
	 *                          (use -1 to include all orders).
	 * @return {@code true} if there is sufficient room, {@code false} otherwise.
	 */
	public boolean hasRoomInSlot(int parkId, String visitDate, String visitTime, int requestedVisitors,
			int excludeOrderId) {
		int maxCapacity = getParkMaxCapacity(parkId);
		int approvedVisitors = getOccupiedVisitorCountForSlot(parkId, visitDate, visitTime, excludeOrderId);

		if (maxCapacity == -1) {
			return false;
		}

		int gap = getParkCasualGap(parkId);

		if (gap == -1) {
			return false;
		}

		return approvedVisitors + requestedVisitors <= maxCapacity - gap;
	}

	/**
	 * Overloaded method to check for room in a slot for new orders, without
	 * excluding any existing orders.
	 *
	 * @param parkId            The unique identifier of the park.
	 * @param visitDate         The date of the visit.
	 * @param visitTime         The time slot of the visit.
	 * @param requestedVisitors The number of visitors to check capacity for.
	 * @return {@code true} if there is sufficient room, {@code false} otherwise.
	 */
	public boolean hasRoomInSlot(int parkId, String visitDate, String visitTime, int requestedVisitors) {
		return hasRoomInSlot(parkId, visitDate, visitTime, requestedVisitors, -1);
	}

	// =========================================================
	// TRY TO PROMOTE FIRST WAITING ORDER
	// =========================================================
	/**
	 * Checks the waiting list for a specific park and time slot, and if sufficient
	 * capacity becomes available, promotes the first eligible order in the queue to
	 * "PendingConfirmation" status and notifies the visitor.
	 *
	 * @param parkId    The unique identifier of the park.
	 * @param visitDate The date of the visit.
	 * @param visitTime The time slot of the visit.
	 * @return {@code true} if an order was successfully promoted, {@code false}
	 *         otherwise.
	 */
	public boolean promoteWaitingOrderIfPossible(int parkId, String visitDate, String visitTime) {

		// Selects the FIRST order in the waiting list for this specific time slot
		String query = "SELECT * FROM Orders "
				+ "WHERE parkId = ? AND visitDate = ? AND visitTime = ? AND status = 'WaitingList' "
				+ "ORDER BY orderId ASC";

		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			conn = pool.getConnection();

			ps = conn.prepareStatement(query);
			ps.setInt(1, parkId);
			ps.setString(2, visitDate);
			ps.setString(3, visitTime);

			rs = ps.executeQuery();

			while (rs.next()) {
				int orderId = rs.getInt("orderId");
				int visitorCount = rs.getInt("visitorCount");
				String email = rs.getString("email");

				// Check if this specific waiting order fits in the newly opened space
				if (hasRoomInSlot(parkId, visitDate, visitTime, visitorCount)) {

					// Change status to PendingConfirmation and start the 1-hour clock!
					PreparedStatement updatePs = conn.prepareStatement(
							"UPDATE Orders SET status = 'PendingConfirmation', holdUntil = DATE_ADD(NOW(), INTERVAL 1 HOUR) WHERE orderId = ?");
					updatePs.setInt(1, orderId);

					int rows = updatePs.executeUpdate();
					updatePs.close();

					if (rows > 0) {

						// Insert Email Notification with DYNAMIC 1-HOUR TIME
						PreparedStatement notifEmailPs = conn.prepareStatement(
								"INSERT INTO Notifications (orderId, notificationType, contactMethod, destinationAddress, messageContent, scheduledTime, isSent) "
										+ "VALUES (?, 'WaitingListTurn', 'Email', ?, CONCAT('A place has become available from the waiting list! Please confirm before ', DATE_FORMAT(DATE_ADD(NOW(), INTERVAL 1 HOUR), '%H:%i on %d/%m/%Y'), ' or your spot will be given to the next person.'), NOW(), false)");
						notifEmailPs.setInt(1, orderId);
						notifEmailPs.setString(2, email);
						notifEmailPs.executeUpdate();
						notifEmailPs.close();

						// Insert SMS Notification with DYNAMIC 1-HOUR TIME
						PreparedStatement notifSmsPs = conn.prepareStatement(
								"INSERT INTO Notifications (orderId, notificationType, contactMethod, destinationAddress, messageContent, scheduledTime, isSent) "
										+ "VALUES (?, 'WaitingListTurn', 'SMS', ?, CONCAT('A place has become available from the waiting list! Please confirm before ', DATE_FORMAT(DATE_ADD(NOW(), INTERVAL 1 HOUR), '%H:%i on %d/%m/%Y'), ' or your spot will be given to the next person.'), NOW(), false)");
						notifSmsPs.setInt(1, orderId);
						notifSmsPs.setString(2, email);
						notifSmsPs.executeUpdate();
						notifSmsPs.close();

						return true; // Successfully promoted someone!
					}
				}
			}

		} catch (SQLException e) {
			e.printStackTrace();

		} finally {
			try {
				if (rs != null)
					rs.close();
				if (ps != null)
					ps.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			if (conn != null)
				pool.releaseConnection(conn);
		}

		return false; // No one in the waiting list fit the spot
	}

	// =========================================================
	// CHECK IF WAITING LIST IS ACTIVE FOR SLOT
	// =========================================================
	/**
	 * Checks if there are any pending orders currently on the waiting list for a
	 * specific park, date, and time slot.
	 *
	 * @param parkId    The unique identifier of the park.
	 * @param visitDate The date to check.
	 * @param visitTime The time slot to check.
	 * @return {@code true} if there is at least one order with 'WaitingList' status
	 *         for the given parameters, {@code false} otherwise.
	 */
	public boolean isWaitingListActive(int parkId, String visitDate, String visitTime) {
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			conn = pool.getConnection();

			// Check if anyone is currently waiting for this exact slot
			String query = "SELECT COUNT(*) AS waitlistCount FROM Orders "
					+ "WHERE parkId = ? AND visitDate = ? AND visitTime = ? AND status = 'WaitingList'";

			ps = conn.prepareStatement(query);
			ps.setInt(1, parkId);
			ps.setString(2, visitDate);
			ps.setString(3, visitTime);

			rs = ps.executeQuery();
			if (rs.next()) {
				// If the count is greater than 0, there is an active waiting list
				return rs.getInt("waitlistCount") > 0;
			}
		} catch (SQLException e) {
			System.out.println("Error checking if waiting list is active.");
			e.printStackTrace();
		} finally {
			// This safely closes everything and prevents memory leaks!
			try {
				if (rs != null)
					rs.close();
				if (ps != null)
					ps.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			if (conn != null) {
				pool.releaseConnection(conn);
			}
		}
		return false;
	}

	// =========================================================
	// CANCEL ORDER
	// =========================================================
	/**
	 * Cancels a visitor's order if the visit time has not yet arrived or passed. If
	 * the cancellation is successful, it subsequently attempts to promote orders
	 * from the waiting list for the associated park and time slot.
	 *
	 * @param orderId The unique identifier of the order to cancel.
	 * @return {@code true} if the order was successfully canceled, {@code false} if
	 *         the order could not be found, was already canceled, or if the visit
	 *         time has already arrived.
	 */
	public boolean cancelOrder(int orderId) {

		Connection conn = null;
		PreparedStatement selectPs = null;
		PreparedStatement updatePs = null;
		ResultSet rs = null;

		try {
			conn = pool.getConnection();

			String selectQuery = "SELECT parkId, visitDate, visitTime, status FROM Orders WHERE orderId = ?";
			selectPs = conn.prepareStatement(selectQuery);
			selectPs.setInt(1, orderId);

			rs = selectPs.executeQuery();

			if (!rs.next()) {
				return false;
			}

			int parkId = rs.getInt("parkId");
			Date visitDate = rs.getDate("visitDate");
			Time visitTime = rs.getTime("visitTime");
			String status = rs.getString("status");

			if ("Canceled".equalsIgnoreCase(status)) {
				System.out.println("[CANCEL ORDER] Order " + orderId + " is already canceled.");
				return false;
			}

			// Combine the visit date and visit time into one date-time object
			LocalDateTime visitDateTime = LocalDateTime.of(visitDate.toLocalDate(), visitTime.toLocalTime());

			// If the visit time already arrived or passed, cancellation is not allowed
			if (!visitDateTime.isAfter(LocalDateTime.now())) {
				System.out.println(
						"[CANCEL ORDER] Cannot cancel order " + orderId + " because visit time already arrived.");
				return false;
			}

			String updateQuery = "UPDATE Orders SET status = 'Canceled' WHERE orderId = ?";
			updatePs = conn.prepareStatement(updateQuery);
			updatePs.setInt(1, orderId);

			int rows = updatePs.executeUpdate();

			if (rows > 0) {
				promoteWaitingOrderIfPossible(parkId, visitDate.toString(), visitTime.toString());
				return true;
			}

			return false;

		} catch (SQLException e) {
			e.printStackTrace();
			return false;

		} finally {
			try {
				if (rs != null)
					rs.close();
				if (selectPs != null)
					selectPs.close();
				if (updatePs != null)
					updatePs.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}

			if (conn != null) {
				pool.releaseConnection(conn);
			}
		}
	}

	// =========================================================
	// UPDATE COUNT FOR CASUAL VISITORS ONLY
	// =========================================================
	/**
	 * Updates the current visitor count for a specified park in the database. *
	 * Note: This method is designed to be part of a larger transaction; it accepts
	 * a shared {@link Connection} and does not manage transaction commits or
	 * connection closure to ensure atomicity.
	 *
	 * @param parkId            The unique identifier of the park.
	 * @param visitorCountToAdd The number of visitors to add to the current count
	 *                          (can be negative to subtract).
	 * @param conn              The active database {@link Connection} to use.
	 * @return {@code true} if the update was successful, {@code false} otherwise.
	 */
	public boolean updateVisitorCount(int parkId, int visitorCountToAdd, Connection conn) {
		PreparedStatement ps = null;
		String query = "UPDATE Parks SET CurrentVisitorCount = CurrentVisitorCount + ? WHERE parkId = ?";

		try {
			ps = conn.prepareStatement(query);
			ps.setInt(1, visitorCountToAdd);
			ps.setInt(2, parkId);

			int rowsAffected = ps.executeUpdate();
			return rowsAffected > 0;

		} catch (SQLException e) {
			System.err.println("[DB ERROR] Failed to update visitor count.");
			e.printStackTrace();
			return false;
		} finally {
			// ONLY close the PreparedStatement here.
			// DO NOT close the Connection, because exitVisitor() still needs it to commit!
			try {
				if (ps != null)
					ps.close();
			} catch (SQLException ex) {
				ex.printStackTrace();
			}
		}
	}

	// =========================================================
	// GET ALTERNATIVE SLOTS
	// =========================================================
	/**
	 * Identifies available time slots for a given park and date by checking each
	 * predefined hourly slot for sufficient capacity.
	 *
	 * @param parkId            The unique identifier of the park.
	 * @param visitDate         The date for which to check availability.
	 * @param requestedVisitors The number of visitors for which to check capacity.
	 * @return An {@link ArrayList} of {@link String} containing all available time
	 *         slots, or an empty list if no slots are available.
	 */
	public ArrayList<String> getAlternativeSlots(int parkId, String visitDate, int requestedVisitors) {

		ArrayList<String> alternativeSlots = new ArrayList<>();

		String[] possibleTimes = { "09:00:00", "10:00:00", "11:00:00", "12:00:00", "13:00:00", "14:00:00", "15:00:00",
				"16:00:00" };

		for (String time : possibleTimes) {
			if (hasRoomInSlot(parkId, visitDate, time, requestedVisitors)) {
				alternativeSlots.add(time);
			}
		}

		return alternativeSlots;
	}

	// =========================================================
	// ADD ORDER TO WAITING LIST
	// =========================================================
	/**
	 * Adds a new order request to the waiting list for a specific park. This method
	 * retrieves the park ID based on the provided park name, generates a unique QR
	 * code for the order, and saves the order entry with 'WaitingList' status.
	 *
	 * @param orderData An {@link ArrayList} of {@link String} containing the order
	 *                  details in the following order: Visitor ID, Park Name, Visit
	 *                  Date, Visit Time, Visitor Count, Order Type, and Email.
	 * @return {@code true} if the order was successfully added to the waiting list,
	 *         {@code false} otherwise.
	 */
	public boolean addOrderToWaitingList(ArrayList<String> orderData) {

		String visitorId = orderData.get(0);
		String parkName = orderData.get(1);
		String visitDate = orderData.get(2);
		String visitTime = orderData.get(3);
		int visitorCount = Integer.parseInt(orderData.get(4));
		String orderType = orderData.get(5);
		String email = orderData.get(6);
		String QR = UUID.randomUUID().toString().replace("-", "").substring(0, 8).toUpperCase();

		Connection conn = null;
		PreparedStatement find = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			conn = pool.getConnection();

			int parkId = -1;

			find = conn.prepareStatement("SELECT parkId FROM Parks WHERE parkName = ?");
			find.setString(1, parkName);

			rs = find.executeQuery();

			if (rs.next()) {
				parkId = rs.getInt("parkId");
			} else {
				return false;
			}

			String insert = "INSERT INTO Orders "
					+ "(parkId, visitorId, visitDate, visitTime, visitorCount, email, orderType, status, QRCode, paid)"
					+ "VALUES (?, ?, ?, ?, ?, ?, ?, 'WaitingList', ?, ?)";

			ps = conn.prepareStatement(insert);

			ps.setInt(1, parkId);
			ps.setString(2, visitorId);
			ps.setString(3, visitDate);
			ps.setString(4, visitTime);
			ps.setInt(5, visitorCount);
			ps.setString(6, email);
			ps.setString(7, orderType);
			ps.setString(8, QR);
			ps.setBoolean(9, false);

			int rows = ps.executeUpdate();

			return rows > 0;

		} catch (SQLException e) {
			e.printStackTrace();
			return false;

		} finally {
			try {
				if (rs != null)
					rs.close();
				if (find != null)
					find.close();
				if (ps != null)
					ps.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}

			if (conn != null) {
				pool.releaseConnection(conn);
			}
		}
	}

	// =========================================================
	// APPROVE REQUEST
	// =========================================================
	/**
	 * Processes and approves a pending request by updating the corresponding park
	 * parameters or inserting a new promotion into the database. The operation is
	 * performed within a transaction to ensure data integrity.
	 *
	 * @param requestId The unique identifier of the request to be approved.
	 * @return {@code true} if the request was successfully approved and database
	 *         records updated, {@code false} otherwise.
	 */
	public boolean approveRequest(int requestId) {

		Connection conn = null;

		try {
			conn = pool.getConnection();
			conn.setAutoCommit(false);

			String selectQuery = "SELECT parkId, requestType, newValue, startDate, endDate FROM Requests WHERE requestId = ?";
			PreparedStatement selectPs = conn.prepareStatement(selectQuery);
			selectPs.setInt(1, requestId);

			ResultSet rs = selectPs.executeQuery();

			if (!rs.next()) {
				rs.close();
				selectPs.close();
				conn.rollback();
				return false;
			}

			int parkId = rs.getInt("parkId");
			String requestType = rs.getString("requestType");
			String newValue = rs.getString("newValue");
			java.sql.Date startDate = rs.getDate("startDate");
			java.sql.Date endDate = rs.getDate("endDate");

			rs.close();
			selectPs.close();

			// =========================================================
			// CASE 1: Request is for updating MaxCapacity
			// =========================================================
			if ("MaxCapacity".equals(requestType)) {
				PreparedStatement gapPs = conn.prepareStatement("SELECT casualGap FROM Parks WHERE parkId = ?");
				gapPs.setInt(1, parkId);

				ResultSet gapRs = gapPs.executeQuery();

				if (!gapRs.next()) {
					gapRs.close();
					gapPs.close();
					conn.rollback();
					return false;
				}

				int casualGap = gapRs.getInt("casualGap");
				int requestedCapacity = Integer.parseInt(newValue);

				gapRs.close();
				gapPs.close();

				// Business Rule: maxCapacity cannot be less than or equal to the casual gap
				if (requestedCapacity <= casualGap) {
					conn.rollback();
					return false;
				}

				PreparedStatement updateParkPs = conn
						.prepareStatement("UPDATE Parks SET maxCapacity = ? WHERE parkId = ?");
				updateParkPs.setInt(1, requestedCapacity);
				updateParkPs.setInt(2, parkId);
				updateParkPs.executeUpdate();
				updateParkPs.close();
			}

			// =========================================================
			// CASE 2: Request is for updating CasualGap
			// =========================================================
			else if ("CasualGap".equals(requestType)) {
				// Fetch current casualGap and maxCapacity to validate and calculate the
				// difference
				PreparedStatement parkPs = conn
						.prepareStatement("SELECT maxCapacity, casualGap FROM Parks WHERE parkId = ?");
				parkPs.setInt(1, parkId);
				ResultSet parkRs = parkPs.executeQuery();

				if (!parkRs.next()) {
					parkRs.close();
					parkPs.close();
					conn.rollback();
					return false;
				}

				int maxCapacity = parkRs.getInt("maxCapacity");
				int requestedCasualGap = Integer.parseInt(newValue);

				parkRs.close();
				parkPs.close();

				// Business Rule Validation: New gap cannot exceed or equal total max capacity
				if (requestedCasualGap >= maxCapacity) {
					conn.rollback();
					return false;
				}

				// Update the casualGap inside the Parks table
				PreparedStatement updateGapPs = conn
						.prepareStatement("UPDATE Parks SET casualGap = ? WHERE parkId = ?");
				updateGapPs.setInt(1, requestedCasualGap);
				updateGapPs.setInt(2, parkId);
				updateGapPs.executeUpdate();
				updateGapPs.close();

			}

			// =========================================================
			// CASE 3: Request is for AvgStayDuration
			// =========================================================
			else if ("AvgStayDuration".equals(requestType)) {
				int requestedDuration = Integer.parseInt(newValue);

				PreparedStatement updateDurationPs = conn
						.prepareStatement("UPDATE Parks SET avgStayDuration = ? WHERE parkId = ?");
				updateDurationPs.setInt(1, requestedDuration);
				updateDurationPs.setInt(2, parkId);
				updateDurationPs.executeUpdate();
				updateDurationPs.close();
			}

			// =========================================================
			// CASE 4: Request is for Promotion
			// =========================================================
			else if ("Promotion".equals(requestType)) {
				double discountPercentage = Double.parseDouble(newValue);

				String promotionName = "Manager Discount " + discountPercentage + "%";

				String insertPromoQuery = "INSERT INTO Promotions (parkId, promotionName, discountPercentage, startDate, endDate, status) "
						+ "VALUES (?, ?, ?, ?, ?, 'Approved')";

				PreparedStatement insertPromoPs = conn.prepareStatement(insertPromoQuery);
				insertPromoPs.setInt(1, parkId);
				insertPromoPs.setString(2, promotionName);
				insertPromoPs.setDouble(3, discountPercentage);
				insertPromoPs.setDate(4, startDate);
				insertPromoPs.setDate(5, endDate);

				insertPromoPs.executeUpdate();
				insertPromoPs.close();
			}

			// =========================================================
			// Final Step: Update the request status to 'Approved'
			// =========================================================
			PreparedStatement updateRequestPs = conn
					.prepareStatement("UPDATE Requests SET status = 'Approved' WHERE requestId = ?");
			updateRequestPs.setInt(1, requestId);
			int rows = updateRequestPs.executeUpdate();
			updateRequestPs.close();

			conn.commit();
			return rows > 0;

		} catch (SQLException e) {
			e.printStackTrace();
			try {
				if (conn != null)
					conn.rollback();
			} catch (SQLException ex) {
				ex.printStackTrace();
			}
			return false;

		} finally {
			try {
				if (conn != null)
					conn.setAutoCommit(true);
			} catch (SQLException e) {
				e.printStackTrace();
			}
			pool.releaseConnection(conn);
		}
	}

	// =========================================================
	// GET PENDING REQUESTS
	// =========================================================
	/**
	 * Retrieves all requests currently pending approval from the database.
	 *
	 * @return An {@link ArrayList} of {@link ArrayList} of {@link String}, where
	 *         each inner list represents a request record (ID, Park Name, Type, Old
	 *         Value, New Value, Status), or an empty list if no pending requests
	 *         are found or an error occurs.
	 */
	public ArrayList<ArrayList<String>> getPendingRequests() {

		ArrayList<ArrayList<String>> result = new ArrayList<>();
		String query = "SELECT requestId, parkId, requestType, oldValue, newValue, status "
				+ "FROM Requests WHERE status = 'Pending'";

		Connection conn = null;

		try {
			conn = pool.getConnection();

			PreparedStatement ps = conn.prepareStatement(query);
			ResultSet rs = ps.executeQuery();

			while (rs.next()) {
				ArrayList<String> row = new ArrayList<>();
				row.add(String.valueOf(rs.getInt("requestId")));
				row.add(getParkNameById(Integer.valueOf(rs.getInt("parkId"))));
				row.add(rs.getString("requestType"));
				row.add(rs.getString("oldValue"));
				row.add(rs.getString("newValue"));
				row.add(rs.getString("status"));
				result.add(row);

			}

			rs.close();
			ps.close();

		} catch (SQLException e) {
			e.printStackTrace();

		} finally {
			pool.releaseConnection(conn);
		}

		return result;
	}

	// =========================================================
	// REJECT REQUEST
	// =========================================================
	/**
	 * Updates the status of a specific request in the database to "Rejected".
	 *
	 * @param requestId The unique identifier of the request to be rejected.
	 * @return {@code true} if the update was successful (at least one row was
	 *         affected), {@code false} otherwise.
	 */
	public boolean rejectRequest(int requestId) {

		String query = "UPDATE Requests SET status = 'Rejected' WHERE requestId = ?";

		Connection conn = null;

		try {
			conn = pool.getConnection();

			PreparedStatement ps = conn.prepareStatement(query);
			ps.setInt(1, requestId);

			int rows = ps.executeUpdate();
			ps.close();

			return rows > 0;

		} catch (SQLException e) {
			e.printStackTrace();
			return false;

		} finally {
			pool.releaseConnection(conn);
		}
	}

	// =========================================================
	// GET PARK ORDERS
	// =========================================================
	/**
	 * Retrieves a list of all orders associated with a specific park, ordered
	 * chronologically by visit date and time.
	 *
	 * @param parkName The name of the park for which to retrieve orders.
	 * @return An {@link ArrayList} of {@link Order} objects for the specified park,
	 *         or an empty list if no orders are found or an error occurs.
	 */
	public ArrayList<Order> getOrdersByParkName(String parkName) {

		ArrayList<Order> result = new ArrayList<>();

		String query = "SELECT o.* FROM Orders o " + "JOIN Parks p ON o.parkId = p.parkId " + "WHERE p.parkName = ? "
				+ "ORDER BY o.visitDate, o.visitTime, o.orderId";

		Connection conn = null;

		try {
			conn = pool.getConnection();

			PreparedStatement ps = conn.prepareStatement(query);
			ps.setString(1, parkName);

			ResultSet rs = ps.executeQuery();

			while (rs.next()) {
				result.add(new Order(rs.getInt("orderId"), rs.getInt("parkId"), rs.getString("visitorId"),
						rs.getDate("visitDate"), rs.getTime("visitTime"), rs.getInt("visitorCount"),
						rs.getString("email"), rs.getString("orderType"), rs.getString("status"),
						rs.getTimestamp("holdUntil")));
			}

			rs.close();
			ps.close();

		} catch (SQLException e) {
			e.printStackTrace();

		} finally {
			pool.releaseConnection(conn);
		}

		return result;
	}

	// =========================================================
	// GET VISITOR TYPE BY ID
	// =========================================================
	/**
	 * Retrieves the visitor type (e.g., "Subscriber", "Casual") for a specific
	 * visitor from the database based on their unique visitor ID.
	 *
	 * @param visitorId The unique identifier of the visitor.
	 * @return The visitor type as a {@link String}, or {@code null} if the visitor
	 *         is not found or a database error occurs.
	 */
	public String getVisitorTypeById(String visitorId) {

		String query = "SELECT visitorType FROM Visitors WHERE visitorId = ?";

		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			conn = pool.getConnection();

			ps = conn.prepareStatement(query);
			ps.setString(1, visitorId);

			rs = ps.executeQuery();

			if (rs.next()) {
				return rs.getString("visitorType");
			}

			return null;

		} catch (SQLException e) {
			e.printStackTrace();
			return null;

		} finally {

			try {
				if (rs != null)
					rs.close();

				if (ps != null)
					ps.close();

				if (conn != null)
					pool.releaseConnection(conn);

			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	// =========================================================
	// GET PARK DATA FOR DASHBOARD
	// =========================================================
	/**
	 * Retrieves statistical data for a specific park to populate the manager's
	 * dashboard, including capacity and current visitor metrics.
	 *
	 * @param parkName The name of the park to query.
	 * @return An {@link ArrayList} of {@link String} containing the park's data
	 *         (Name, Max Capacity, Casual Gap, Average Stay Duration, Current
	 *         Visitor Count), or an empty list if the park is not found or a
	 *         database error occurs.
	 */
	public ArrayList<String> getParkDashboardData(String parkName) {
		ArrayList<String> parkData = new ArrayList<>();
		String query = "SELECT parkName, maxCapacity, casualGap, avgStayDuration, CurrentVisitorCount "
				+ "FROM Parks WHERE parkName = ?";

		try {
			Connection conn = pool.getConnection();
			PreparedStatement stmt = conn.prepareStatement(query);
			stmt.setString(1, parkName);
			ResultSet rs = stmt.executeQuery();

			if (rs.next()) {
				// Add data to the list in the exact order we queried it
				parkData.add(rs.getString("parkName"));
				parkData.add(String.valueOf(rs.getInt("maxCapacity")));
				parkData.add(String.valueOf(rs.getInt("casualGap")));
				parkData.add(String.valueOf(rs.getInt("avgStayDuration")));
				parkData.add(String.valueOf(rs.getInt("CurrentVisitorCount")));
			}

			rs.close();
			stmt.close();
			pool.releaseConnection(conn);

		} catch (SQLException e) {
			System.out.println("Error fetching park dashboard data:");
			e.printStackTrace();
		}

		return parkData;
	}

	// =========================================================
	// GET TOTAL ACTIVE PROMOTIONS DISCOUNT
	// =========================================================
	/**
	 * Calculates the total discount percentage for all approved active promotions
	 * applicable to a specific park on a given date.
	 *
	 * @param parkId The unique identifier of the park.
	 * @param date   The date for which to calculate the active discounts.
	 * @return The total discount percentage as a {@code double}, or {@code 0} if no
	 *         active promotions exist or an error occurs.
	 */
	public double getActivePromotionsDiscount(int parkId, LocalDate date) {

		String query = "SELECT IFNULL(SUM(discountPercentage), 0) AS totalDiscount " + "FROM promotions "
				+ "WHERE parkId = ? " + "AND status = 'Approved' " + "AND startDate <= ? " + "AND endDate >= ?";

		Connection conn = null;

		try {
			conn = pool.getConnection();

			PreparedStatement ps = conn.prepareStatement(query);

			ps.setInt(1, parkId);
			ps.setDate(2, java.sql.Date.valueOf(date));
			ps.setDate(3, java.sql.Date.valueOf(date));

			ResultSet rs = ps.executeQuery();

			if (rs.next()) {
				double discount = rs.getDouble("totalDiscount");
				rs.close();
				ps.close();
				return discount;
			}

			rs.close();
			ps.close();

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return 0;
	}

	// =========================================================
	// GET QUICK SEARCH RESULT
	// =========================================================
	/**
	 * Performs a quick search for orders in the database matching either the
	 * specific order ID or the visitor ID provided.
	 *
	 * @param searchInput The identifier (Order ID or Visitor ID) to search for.
	 * @return An {@link ArrayList} of {@link Order} objects matching the search
	 *         criteria, or an empty list if no matches are found.
	 */
	public ArrayList<Order> quickSearchOrders(String searchInput) {
		ArrayList<Order> ordersList = new ArrayList<>();

		// We use OR to check both columns
		String query = "SELECT * FROM Orders WHERE orderId = ? OR visitorId = ?";

		Connection conn = null;

		try {
			conn = pool.getConnection();
			PreparedStatement ps = conn.prepareStatement(query);

			ps.setString(1, searchInput);
			ps.setString(2, searchInput);

			ResultSet rs = ps.executeQuery();

			while (rs.next()) {
				// Create the Order object using the exact requested structure
				ordersList.add(new Order(rs.getInt("orderId"), rs.getInt("parkId"), rs.getString("visitorId"),
						rs.getDate("visitDate"), rs.getTime("visitTime"), rs.getInt("visitorCount"),
						rs.getString("email"), rs.getString("orderType"), rs.getString("status"),
						rs.getTimestamp("holdUntil")));
			}

			rs.close();
			ps.close();

		} catch (SQLException e) {
			System.out.println("Error fetching orders for quick search:");
			e.printStackTrace();

		} finally {
			if (conn != null) {
				pool.releaseConnection(conn);
			}
		}
		return ordersList;
	}

	// =========================================================
	// CONFIRM ORDER
	// =========================================================
	/**
	 * Confirms a visitor's order by updating its status to "Approved" if the
	 * confirmation is received within the allowed time frame (hold or reminder
	 * duration).
	 *
	 * @param orderId The unique identifier of the order to be confirmed.
	 * @return {@code true} if the order status was successfully updated,
	 *         {@code false} if the order does not exist or the time limit has
	 *         expired.
	 */
	public boolean confirmOrder(int orderId) {

		Connection conn = null;
		PreparedStatement ps = null;

		try {
			conn = pool.getConnection();

			// Cleanly formatted SQL query with your excellent timestamp validation!
			String query = "UPDATE Orders " + "SET status = 'Approved', holdUntil = NULL, reminderUntil = NULL "
					+ "WHERE orderId = ? " + "AND ( " + "  (status = 'PendingConfirmation' AND holdUntil >= NOW()) OR "
					+ "  (status = 'PendingVisitReminder' AND reminderUntil >= NOW()) " + ")";

			ps = conn.prepareStatement(query);
			ps.setInt(1, orderId);

			int rows = ps.executeUpdate();

			// If rows > 0, the order was successfully confirmed in time!
			// If rows == 0, either the order doesn't exist, or their time expired.
			return rows > 0;

		} catch (SQLException e) {
			e.printStackTrace();
			return false;

		} finally {
			if (ps != null) {
				try {
					ps.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}

			if (conn != null) {
				pool.releaseConnection(conn);
			}
		}
	}

	// =========================================================
	// RELEASE EXPIRED PENDING CONFIRMATIONS
	// =========================================================
	/**
	 * Identifies orders in 'PendingConfirmation' status that have exceeded their
	 * hold duration, cancels them, and subsequently attempts to promote orders from
	 * the waiting list for the associated park and time slot.
	 */
	public void releaseExpiredPendingConfirmations() {

		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			conn = pool.getConnection();

			String query = "SELECT orderId, parkId, visitDate, visitTime " + "FROM Orders "
					+ "WHERE status = 'PendingConfirmation' AND holdUntil < NOW()";

			ps = conn.prepareStatement(query);
			rs = ps.executeQuery();

			ArrayList<Integer> expiredOrderIds = new ArrayList<>();
			ArrayList<Integer> parkIds = new ArrayList<>();
			ArrayList<String> visitDates = new ArrayList<>();
			ArrayList<String> visitTimes = new ArrayList<>();

			while (rs.next()) {
				expiredOrderIds.add(rs.getInt("orderId"));
				parkIds.add(rs.getInt("parkId"));
				visitDates.add(rs.getDate("visitDate").toString());
				visitTimes.add(rs.getTime("visitTime").toString());
			}

			rs.close();
			ps.close();

			for (int i = 0; i < expiredOrderIds.size(); i++) {
				PreparedStatement updatePs = conn
						.prepareStatement("UPDATE Orders SET status = 'Canceled', holdUntil = NULL WHERE orderId = ?");
				updatePs.setInt(1, expiredOrderIds.get(i));
				updatePs.executeUpdate();
				updatePs.close();

				promoteWaitingOrderIfPossible(parkIds.get(i), visitDates.get(i), visitTimes.get(i));
			}

		} catch (SQLException e) {
			e.printStackTrace();

		} finally {
			try {
				if (rs != null)
					rs.close();
				if (ps != null)
					ps.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}

			if (conn != null) {
				pool.releaseConnection(conn);
			}
		}
	}

	// =========================================================
	// SEND VISIT REMINDERS FOR TOMORROW
	// =========================================================
	/**
	 * Identifies "Approved" orders scheduled for the following day that have not
	 * yet received a visit reminder. Updates these orders to "PendingVisitReminder"
	 * status, sets a 2-hour confirmation deadline, and creates both Email and SMS
	 * notification records.
	 */
	public void sendVisitRemindersForTomorrow() {

		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			conn = pool.getConnection();

			// Check for orders that need a reminder tomorrow
			String query = "SELECT orderId, email FROM Orders " + "WHERE status = 'Approved' "
					+ "AND visitDate = DATE_ADD(CURDATE(), INTERVAL 1 DAY) "
					+ "AND orderId NOT IN (SELECT orderId FROM Notifications WHERE notificationType = 'Reminder')";

			ps = conn.prepareStatement(query);
			rs = ps.executeQuery();

			ArrayList<Integer> orderIds = new ArrayList<>();
			ArrayList<String> emails = new ArrayList<>();

			while (rs.next()) {
				orderIds.add(rs.getInt("orderId"));
				emails.add(rs.getString("email"));
			}

			rs.close();
			ps.close();

			for (int i = 0; i < orderIds.size(); i++) {
				// 1. Update the order status and set the 2-hour timer
				PreparedStatement updatePs = conn.prepareStatement(
						"UPDATE Orders SET status = 'PendingVisitReminder', reminderUntil = DATE_ADD(NOW(), INTERVAL 2 HOUR) "
								+ "WHERE orderId = ?");
				updatePs.setInt(1, orderIds.get(i));
				updatePs.executeUpdate();
				updatePs.close();

				// 2. Insert Email Notification with DYNAMIC TIME using SQL CONCAT and
				// DATE_FORMAT
				PreparedStatement notifEmailPs = conn.prepareStatement(
						"INSERT INTO Notifications (orderId, notificationType, contactMethod, destinationAddress, messageContent, scheduledTime, isSent) "
								+ "VALUES (?, 'Reminder', 'Email', ?, CONCAT('Reminder: your visit is tomorrow. Please confirm before ', DATE_FORMAT(DATE_ADD(NOW(), INTERVAL 2 HOUR), '%H:%i on %d/%m/%Y'), '.'), NOW(), false)");
				notifEmailPs.setInt(1, orderIds.get(i));
				notifEmailPs.setString(2, emails.get(i));
				notifEmailPs.executeUpdate();
				notifEmailPs.close();

				// 3. Insert SMS Notification with DYNAMIC TIME using SQL CONCAT and DATE_FORMAT
				PreparedStatement notifSmsPs = conn.prepareStatement(
						"INSERT INTO Notifications (orderId, notificationType, contactMethod, destinationAddress, messageContent, scheduledTime, isSent) "
								+ "VALUES (?, 'Reminder', 'SMS', ?, CONCAT('Reminder: your visit is tomorrow. Please confirm before ', DATE_FORMAT(DATE_ADD(NOW(), INTERVAL 2 HOUR), '%H:%i on %d/%m/%Y'), '.'), NOW(), false)");
				notifSmsPs.setInt(1, orderIds.get(i));
				notifSmsPs.setString(2, emails.get(i));
				notifSmsPs.executeUpdate();
				notifSmsPs.close();
			}

		} catch (SQLException e) {
			e.printStackTrace();

		} finally {
			try {
				if (rs != null)
					rs.close();
				if (ps != null)
					ps.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}

			if (conn != null) {
				pool.releaseConnection(conn);
			}
		}
	}

	// =========================================================
	// RELEASE EXPIRED VISIT REMINDERS
	// =========================================================
	/**
	 * Identifies orders that have exceeded their visit reminder deadline, cancels
	 * them, and generates corresponding notification records for both Email and SMS
	 * channels.
	 */
	public void releaseExpiredVisitReminders() {

		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			conn = pool.getConnection();

			String query = "SELECT orderId, email FROM Orders "
					+ "WHERE status = 'PendingVisitReminder' AND reminderUntil < NOW()";

			ps = conn.prepareStatement(query);
			rs = ps.executeQuery();

			ArrayList<Integer> expiredOrderIds = new ArrayList<>();
			ArrayList<String> emails = new ArrayList<>();

			while (rs.next()) {
				expiredOrderIds.add(rs.getInt("orderId"));
				emails.add(rs.getString("email"));
			}

			rs.close();
			ps.close();

			for (int i = 0; i < expiredOrderIds.size(); i++) {
				PreparedStatement updatePs = conn.prepareStatement(
						"UPDATE Orders SET status = 'Canceled', reminderUntil = NULL WHERE orderId = ?");
				updatePs.setInt(1, expiredOrderIds.get(i));
				updatePs.executeUpdate();
				updatePs.close();

				PreparedStatement notifEmailPs = conn.prepareStatement(
						"INSERT INTO Notifications (orderId, notificationType, contactMethod, destinationAddress, messageContent, scheduledTime, isSent) "
								+ "VALUES (?, 'VisitReminderExpired', 'Email', ?, ?, NOW(), false)");
				notifEmailPs.setInt(1, expiredOrderIds.get(i));
				notifEmailPs.setString(2, emails.get(i));
				notifEmailPs.setString(3,
						"Your order was canceled automatically because you did not confirm within 2 hours.");
				notifEmailPs.executeUpdate();
				notifEmailPs.close();

				PreparedStatement notifSmsPs = conn.prepareStatement(
						"INSERT INTO Notifications (orderId, notificationType, contactMethod, destinationAddress, messageContent, scheduledTime, isSent) "
								+ "VALUES (?, 'VisitReminderExpired', 'SMS', ?, ?, NOW(), false)");
				notifSmsPs.setInt(1, expiredOrderIds.get(i));
				notifSmsPs.setString(2, emails.get(i));
				notifSmsPs.setString(3,
						"Your order was canceled automatically because you did not confirm within 2 hours.");
				notifSmsPs.executeUpdate();
				notifSmsPs.close();
			}

		} catch (SQLException e) {
			e.printStackTrace();

		} finally {
			try {
				if (rs != null)
					rs.close();
				if (ps != null)
					ps.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}

			if (conn != null) {
				pool.releaseConnection(conn);
			}
		}
	}

	// =========================================================
	// GET VISITOR EMAIL BY ID
	// =========================================================
	/**
	 * Retrieves the email address of a visitor from the database based on their
	 * unique visitor ID.
	 *
	 * @param visitorId The unique identifier of the visitor.
	 * @return The email address of the visitor as a {@link String}, or {@code null}
	 *         if the visitor is not found or a database error occurs.
	 */
	public String getVisitorEmailById(String visitorId) {

		String query = "SELECT email FROM Visitors WHERE visitorId = ?";

		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			conn = pool.getConnection();

			ps = conn.prepareStatement(query);
			ps.setString(1, visitorId);

			rs = ps.executeQuery();

			if (rs.next()) {
				return rs.getString("email");
			}

			return null;

		} catch (SQLException e) {
			e.printStackTrace();
			return null;

		} finally {

			try {
				if (rs != null)
					rs.close();
				if (ps != null)
					ps.close();
				if (conn != null)
					pool.releaseConnection(conn);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	// =========================================================
	// UPDATE ORDER PAID STATUS
	// =========================================================
	/**
	 * Updates the payment status of a specific order in the database to "paid".
	 *
	 * @param orderId The unique identifier of the order to update.
	 * @return {@code true} if the update was successful (at least one row was
	 *         affected), {@code false} otherwise.
	 */
	public boolean updateOrderPaidStatus(int orderId) {
		String query = "UPDATE Orders SET paid = 1 WHERE orderId = ?";
		Connection conn = null;
		PreparedStatement ps = null;

		try {
			conn = pool.getConnection();
			ps = conn.prepareStatement(query);
			ps.setInt(1, orderId);

			int rows = ps.executeUpdate();
			return rows > 0;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		} finally {
			if (ps != null)
				try {
					ps.close();
				} catch (SQLException e) {
				}
			if (conn != null)
				pool.releaseConnection(conn);
		}
	}

	// =========================================================
	// SAVE REPORT
	// =========================================================
	/**
	 * Persists a report record into the database, including its associated image
	 * data.
	 *
	 * @param report The {@link ReportImage} object containing the report details
	 *               (type, park name, month, year, and image byte array).
	 * @return {@code true} if the insertion was successful (at least one row was
	 *         affected), {@code false} otherwise.
	 */
	public boolean saveReport(ReportImage report) {

		String sql = """
				    INSERT INTO reports
				    (reportType, parkName, month, year, createdAt, image)
				    VALUES (?, ?, ?, ?, NOW(), ?)
				""";

		try (Connection conn = pool.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

			stmt.setString(1, report.getReportType());
			stmt.setString(2, report.getParkName());
			stmt.setInt(3, report.getMonth());
			stmt.setInt(4, report.getYear());
			stmt.setBytes(5, report.getImage());

			return stmt.executeUpdate() > 0;

		} catch (Exception e) {
			e.printStackTrace();
		}

		return false;
	}

	// =========================================================
	// GET ALL REPORTS
	// =========================================================
	/**
	 * Retrieves a list of all report records from the database, including their
	 * associated image data.
	 *
	 * @return A {@link List} of {@link ReportImage} objects containing all report
	 *         data, or an empty list if no reports are found or an error occurs.
	 */
	public List<ReportImage> getAllReports() {

		List<ReportImage> reports = new ArrayList<>();

		String sql = "SELECT reportId, reportType, parkName, month, year, createdAt, image FROM reports";

		try (Connection conn = pool.getConnection();
				PreparedStatement stmt = conn.prepareStatement(sql);
				ResultSet rs = stmt.executeQuery()) {

			while (rs.next()) {

				ReportImage report = new ReportImage(rs.getInt("reportId"), rs.getString("reportType"),
						rs.getString("parkName"), rs.getInt("month"), rs.getInt("year"), rs.getString("createdAt"),
						rs.getBytes("image"));

				reports.add(report);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return reports;
	}

	// =========================================================
	// GET UNREAD NOTIFICATIONS
	// =========================================================
	/**
	 * Retrieves a list of unread notifications for a specific user based on their
	 * email address. * Each notification string is formatted as
	 * "contactMethod|messageContent".
	 *
	 * @param email The destination email address of the user.
	 * @return An {@link ArrayList} of {@link String} representing the unread
	 *         notifications, or an empty list if no notifications are found or an
	 *         error occurs.
	 */
	public ArrayList<String> getUnreadNotifications(String email) {
		ArrayList<String> notifications = new ArrayList<>();
		Connection conn = null;

		try {
			conn = pool.getConnection();
			// We select BOTH contactMethod and messageContent
			PreparedStatement ps = conn.prepareStatement(
					"SELECT contactMethod, messageContent FROM Notifications WHERE destinationAddress = ? AND isSent = false");
			ps.setString(1, email);

			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				// We glue them together with a "|" symbol (e.g. "SMS|Reminder: your visit is
				// tomorrow")
				String combined = rs.getString("contactMethod") + "|" + rs.getString("messageContent");
				notifications.add(combined);
			}

			rs.close();
			ps.close();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			pool.releaseConnection(conn);
		}
		return notifications;
	}

	// =========================================================
	// MARK NOTIFICATIONS AS READ
	// =========================================================
	/**
	 * Updates the status of all unread notifications for a specific user to "sent"
	 * (read status) in the database.
	 *
	 * @param email The destination email address of the user whose notifications
	 *              are to be marked as read.
	 */
	public void markNotificationsAsRead(String email) {
		Connection conn = null;
		try {
			conn = pool.getConnection();
			PreparedStatement ps = conn.prepareStatement(
					"UPDATE Notifications SET isSent = true WHERE destinationAddress = ? AND isSent = false");
			ps.setString(1, email);
			ps.executeUpdate();
			ps.close();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			pool.releaseConnection(conn);
		}
	}

	// =========================================================
	// GET PARK NAME BY ID
	// =========================================================
	/**
	 * Retrieves the name of a park from the database based on its unique park ID.
	 *
	 * @param parkId The unique identifier of the park.
	 * @return The name of the park as a {@link String}, or {@code null} if the park
	 *         is not found or a database error occurs.
	 */
	public String getParkNameById(int parkId) {

		String query = "SELECT parkName FROM parks WHERE parkId = ?";

		Connection conn = null;

		try {
			conn = pool.getConnection();

			PreparedStatement stmt = conn.prepareStatement(query);
			stmt.setInt(1, parkId);

			ResultSet rs = stmt.executeQuery();

			if (rs.next()) {
				return rs.getString("parkName");
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			pool.releaseConnection(conn);
		}

		return null;
	}

	// =========================================================
	// UPDATE VISITOR DETAILS
	// =========================================================
	/**
	 * Updates the personal and payment information of a specific visitor in the
	 * database.
	 *
	 * @param visitorId  The unique identifier of the visitor.
	 * @param firstName  The new first name of the visitor.
	 * @param lastName   The new last name of the visitor.
	 * @param phone      The new phone number of the visitor.
	 * @param email      The new email address of the visitor.
	 * @param creditCard The new credit card information for the visitor.
	 * @return {@code true} if the update was successful (at least one row was
	 *         affected), {@code false} otherwise.
	 */
	public boolean updateVisitorDetails(String visitorId, String firstName, String lastName, String phone, String email,
			String creditCard) {

		String query = "UPDATE Visitors SET firstName = ?, lastName = ?, phone = ?, email = ?, creditCard = ? WHERE visitorId = ?";

		Connection conn = null;

		try {
			conn = pool.getConnection();

			PreparedStatement ps = conn.prepareStatement(query);
			ps.setString(1, firstName);
			ps.setString(2, lastName);
			ps.setString(3, phone);
			ps.setString(4, email);
			ps.setString(5, creditCard);
			ps.setString(6, visitorId);

			int rows = ps.executeUpdate();
			ps.close();

			return rows > 0;

		} catch (SQLException e) {
			e.printStackTrace();
			return false;

		} finally {
			pool.releaseConnection(conn);
		}
	}

	// =========================================================
	// FETCH EMPLOYEE BY ID
	// =========================================================
	/**
	 * Retrieves the details of a specific employee from the database by their
	 * unique ID.
	 *
	 * @param employeeId The unique identifier of the employee to search for.
	 * @return An {@link ArrayList} of {@link String} containing the employee's
	 *         details (ID, First Name, Last Name, Email, Role, Affiliation), or
	 *         {@code null} if the employee was not found or a database error
	 *         occurred.
	 */
	public ArrayList<String> fetchEmployeeById(String employeeId) {

		String query = "SELECT * FROM Employees WHERE employeeId = ?";

		Connection conn = null;

		try {
			conn = pool.getConnection();

			PreparedStatement ps = conn.prepareStatement(query);
			ps.setString(1, employeeId);

			ResultSet rs = ps.executeQuery();

			if (rs.next()) {
				ArrayList<String> employeeInfo = new ArrayList<>();

				employeeInfo.add(String.valueOf(rs.getInt("employeeId")));
				employeeInfo.add(rs.getString("firstName"));
				employeeInfo.add(rs.getString("lastName"));
				employeeInfo.add(rs.getString("email"));
				employeeInfo.add(rs.getString("role"));
				employeeInfo.add(rs.getString("affiliation"));

				rs.close();
				ps.close();

				return employeeInfo;
			}

			rs.close();
			ps.close();

		} catch (SQLException e) {
			e.printStackTrace();

		} finally {
			pool.releaseConnection(conn);
		}

		return null;
	}

	// =========================================================
	// FETCH SUBSCRIBER BY ID
	// =========================================================
	/**
	 * Retrieves comprehensive details for a specific subscriber from the database.
	 *
	 * @param subscriberId The unique identifier of the subscriber to retrieve.
	 * @return An {@link ArrayList} of {@link String} containing subscriber
	 *         information (ID, First Name, Last Name, Phone, Email, Type,
	 *         Subscription Number, Family Members, Credit Card), or {@code null} if
	 *         no subscriber is found or an error occurs.
	 */
	public ArrayList<String> fetchSubscriberById(String subscriberId) {

		String query = "SELECT * FROM Visitors WHERE visitorId = ? AND visitorType = 'Subscriber'";

		Connection conn = null;

		try {
			conn = pool.getConnection();

			PreparedStatement ps = conn.prepareStatement(query);
			ps.setString(1, subscriberId);

			ResultSet rs = ps.executeQuery();

			if (rs.next()) {
				ArrayList<String> subscriberInfo = new ArrayList<>();

				subscriberInfo.add(rs.getString("visitorId"));
				subscriberInfo.add(rs.getString("firstName"));
				subscriberInfo.add(rs.getString("lastName"));
				subscriberInfo.add(rs.getString("phone"));
				subscriberInfo.add(rs.getString("email"));
				subscriberInfo.add(rs.getString("visitorType"));
				subscriberInfo.add(String.valueOf(rs.getInt("subscriptionNumber")));
				subscriberInfo.add(String.valueOf(rs.getInt("familyMembers")));
				subscriberInfo.add(rs.getString("creditCard"));

				rs.close();
				ps.close();

				return subscriberInfo;
			}

			rs.close();
			ps.close();

		} catch (SQLException e) {
			e.printStackTrace();

		} finally {
			pool.releaseConnection(conn);
		}

		return null;
	}
}