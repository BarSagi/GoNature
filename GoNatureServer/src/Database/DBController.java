package Database;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Common.CancellationReportData;
import Common.Order;
import Common.UsageReportData;
import Common.Visit;
import Common.VisitRecord;
import Server.EchoServer;

public class DBController {

	private DBConnectionPool pool;

	public DBController(EchoServer server) {
		this.pool = DBConnectionPool.getInstance(server);
	}

	// =========================================================
	// ORDERS - GET ALL
	// =========================================================
	public ArrayList<Order> getAllOrders() throws SQLException {

		ArrayList<Order> result = new ArrayList<>();

		String query = "SELECT * FROM Orders";

		Connection conn = null;

		try {
			conn = pool.getConnection();

			PreparedStatement ps = conn.prepareStatement(query);
			ResultSet rs = ps.executeQuery();

			while (rs.next()) {
				result.add(new Order(rs.getInt("orderId"), rs.getInt("parkId"), rs.getString("visitorId"),
						rs.getDate("visitDate"), rs.getTime("visitTime"), rs.getInt("visitorCount"),
						rs.getString("email"), rs.getString("orderType"), rs.getString("status")));
			}

			rs.close();
			ps.close();

		} finally {
			pool.releaseConnection(conn);
		}

		return result;
	}

	// =========================================================
	// ORDERS - GET ALL ORDERS OF SPECIFIC PARK
	// =========================================================
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
						rs.getString("email"), rs.getString("orderType"), rs.getString("status")));
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

				visitor.add(String.valueOf(rs.getInt("visitorId")));
				visitor.add(rs.getString("firstName"));
				visitor.add(rs.getString("lastName"));
				visitor.add(rs.getString("phone"));
				visitor.add(rs.getString("email"));
				visitor.add(rs.getString("visitorType"));
				visitor.add(String.valueOf(rs.getInt("subscriptionNumber")));
				visitor.add(String.valueOf(rs.getInt("familyMembers")));

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
	// CREATE ORDER
	// =========================================================
	public String createNewOrder(ArrayList<String> orderData) {

		String visitorId = orderData.get(0);
		String parkName = orderData.get(1);
		String visitDate = orderData.get(2);
		String visitTime = orderData.get(3);
		int visitorCount = Integer.parseInt(orderData.get(4));
		String email = orderData.get(5);
		String orderType = orderData.get(6);

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

			if (hasRoomInSlot(parkId, visitDate, visitTime, visitorCount)) {

				String insert = "INSERT INTO Orders "
						+ "(parkId, visitorId, visitDate, visitTime, visitorCount, email, orderType, status) "
						+ "VALUES (?, ?, ?, ?, ?, ?, ?, 'Approved')";

				PreparedStatement ps = conn.prepareStatement(insert);

				ps.setInt(1, parkId);
				ps.setString(2, visitorId);
				ps.setString(3, visitDate);
				ps.setString(4, visitTime);
				ps.setInt(5, visitorCount);
				ps.setString(6, email);
				ps.setString(7, orderType);

				int rows = ps.executeUpdate();
				ps.close();

				if (rows > 0) {
					return "Approved";
				}

				return "Failed";
			}

			// אם אין מקום - לא שומרים עדיין, רק מחזירים שעות חלופיות
			ArrayList<String> alternatives = getAlternativeSlots(parkId, visitDate, visitorCount);
			String joined = String.join(", ", alternatives);

			return "Full|" + joined;

		} catch (SQLException e) {
			e.printStackTrace();
			return "Failed";

		} finally {
			pool.releaseConnection(conn);
		}
	}

	// =========================================================
	// UPDATE ORDER
	// =========================================================
	public boolean updateOrder(Order order) {
		// The SQL UPDATE statement
		String query = "UPDATE Orders SET visitDate = ?, visitTime = ?, visitorCount = ? WHERE orderId = ?";

		Connection conn = null;

		try {
			conn = pool.getConnection();

			PreparedStatement pstmt = conn.prepareStatement(query);

			// Inject the values from the Order object into the ? placeholders
			pstmt.setDate(1, order.getVisitDate());
			pstmt.setTime(2, order.getVisitTime());
			pstmt.setInt(3, order.getVisitorCount());
			pstmt.setInt(4, order.getOrderId());

			// executeUpdate() returns the number of rows changed in the DB
			int rowsAffected = pstmt.executeUpdate();

			// If it changed 1 or more rows, the update was successful!
			return rowsAffected > 0;

		} catch (SQLException e) {
			System.out.println("Error updating order in database.");
			e.printStackTrace();
			return false;
		}
	}

	// =========================================================
	// VISITOR ORDERS
	// =========================================================
	public ArrayList<Order> getVisitorOrders(String visitorId) {

		ArrayList<Order> list = new ArrayList<>();

		String query = "SELECT * FROM Orders WHERE visitorId = ? ORDER BY visitDate DESC";

		Connection conn = null;

		try {
			conn = pool.getConnection();

			PreparedStatement ps = conn.prepareStatement(query);
			ps.setString(1, visitorId);

			ResultSet rs = ps.executeQuery();

			while (rs.next()) {
				list.add(new Order(rs.getInt("orderId"), rs.getInt("parkId"), rs.getString("visitorId"),
						rs.getDate("visitDate"), rs.getTime("visitTime"), rs.getInt("visitorCount"),
						rs.getString("email"), rs.getString("orderType"), rs.getString("status")));
			}

			rs.close();
			ps.close();

		} catch (SQLException e) {
			e.printStackTrace();

		} finally {
			pool.releaseConnection(conn);
		}

		return list;
	}

	// =========================================================
	// EMPLOYEE INFO
	// =========================================================
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
	public ArrayList<Visit> getVisitReport(int parkId, int month, int year) {

		ArrayList<Visit> result = new ArrayList<>();

		String query = "SELECT v.* " + "FROM Visits v " + "WHERE v.parkId = ? " + "AND YEAR(v.entryTime) = ? "
				+ "AND MONTH(v.entryTime) = ?";

		Connection conn = null;

		try {
			conn = pool.getConnection();

			PreparedStatement ps = conn.prepareStatement(query);
			ps.setInt(1, parkId);
			ps.setInt(2, year);
			ps.setInt(3, month);

			ResultSet rs = ps.executeQuery();

			while (rs.next()) {

				result.add(new Visit(rs.getInt("visitId"), rs.getInt("parkId"), rs.getInt("orderId"),
						rs.getString("visitorId"), rs.getInt("actualVisitorCount"), rs.getTimestamp("entryTime"),
						rs.getTimestamp("exitTime"), rs.getString("orderType")));
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
	// ENTER VISITOR (RESERVED)
	// =========================================================
	public boolean enterVisitor(String visitorId) {
		String selectQuery = "SELECT parkId, orderId, visitorCount FROM Orders "
				+ "WHERE visitorId = ? AND status = 'Approved' " + "AND visitDate = CURDATE() "
				+ "AND visitTime BETWEEN SUBTIME(CURTIME(), '00:30:00') AND ADDTIME(CURTIME(), '00:30:00') "
				+ "ORDER BY orderId DESC LIMIT 1";

		String insertQuery = "INSERT INTO Visits (parkId, orderId, visitorId, actualVisitorCount, entryTime, exitTime) "
				+ "VALUES (?, ?, ?, ?, NOW(), NULL)";

		Connection conn = null;
		PreparedStatement psSelect = null;
		PreparedStatement psInsert = null;
		ResultSet rs = null;

		try {
			conn = pool.getConnection();

			psSelect = conn.prepareStatement(selectQuery);
			psSelect.setString(1, visitorId);
			rs = psSelect.executeQuery();

			if (rs.next()) {
				int parkId = rs.getInt("parkId");
				int orderId = rs.getInt("orderId");
				int visitorCount = rs.getInt("visitorCount");

				psInsert = conn.prepareStatement(insertQuery);
				psInsert.setInt(1, parkId);
				psInsert.setInt(2, orderId);
				psInsert.setString(3, visitorId);
				psInsert.setInt(4, visitorCount);

				int rows = psInsert.executeUpdate();

				if (rows > 0) {
					boolean isCountUpdated = updateReservedVisitorCount(parkId, visitorCount);
					return isCountUpdated;
				}
			}

			return false;

		} catch (SQLException e) {
			e.printStackTrace();
			return false;

		} finally {
			try {
				if (rs != null)
					rs.close();
				if (psSelect != null)
					psSelect.close();
				if (psInsert != null)
					psInsert.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}

			if (conn != null) {
				pool.releaseConnection(conn);
			}
		}
	}

	// =========================================================
	// EXIT VISITOR
	// =========================================================
	public boolean exitVisitor(String visitorId) {

		// 1. SELECT query to fetch the parkId, visitor count, and orderId before
		// updating
		String selectQuery = "SELECT visitId, parkId, actualVisitorCount, orderId FROM Visits "
				+ "WHERE visitorId = ? AND entryTime IS NOT NULL AND exitTime IS NULL "
				+ "ORDER BY visitId DESC LIMIT 1";

		// 2. UPDATE query to set the exit time for the specific visit we just found
		String updateQuery = "UPDATE Visits SET exitTime = NOW() WHERE visitId = ?";

		Connection conn = null;
		PreparedStatement psSelect = null;
		PreparedStatement psUpdate = null;
		ResultSet rs = null;

		try {
			conn = pool.getConnection();

			// Execute SELECT to get visit details
			psSelect = conn.prepareStatement(selectQuery);
			psSelect.setString(1, visitorId);
			rs = psSelect.executeQuery();

			if (rs.next()) {
				int visitId = rs.getInt("visitId");
				int parkId = rs.getInt("parkId");
				int actualVisitorCount = rs.getInt("actualVisitorCount");

				// Read orderId to determine if this was a casual or reserved visit
				rs.getInt("orderId");
				// rs.wasNull() returns true if the last read column (orderId) was NULL in the
				// DB
				boolean isCasual = rs.wasNull();

				// Update the exitTime for this specific visitId
				psUpdate = conn.prepareStatement(updateQuery);
				psUpdate.setInt(1, visitId);

				int rows = psUpdate.executeUpdate();

				// If the UPDATE was successful, decrease the relevant visitor counts
				if (rows > 0) {
					boolean isCountUpdated = false;

					// Route to the correct update method based on visitor type
					if (isCasual) {
						// Casual visitor: Decrease total count AND increase OpenCasualSpots
						// We pass a negative value to subtract from the visitors and add to the open
						// spots
						isCountUpdated = updateCasualVisitorCount(parkId, -actualVisitorCount);
					} else {
						// Reserved visitor: Decrease total count ONLY
						isCountUpdated = updateReservedVisitorCount(parkId, -actualVisitorCount);
					}

					if (!isCountUpdated) {
						System.err.println(
								"[DB ERROR] Visitor exit recorded, but failed to decrease current visitor count.");
					}

					// Return true only if both operations succeeded
					return isCountUpdated;
				}
			}

			// If we reach here, either no active visit was found, or the UPDATE failed
			return false;

		} catch (SQLException e) {
			e.printStackTrace();
			return false;

		} finally {
			// Safely close all resources to prevent memory leaks
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (psSelect != null) {
				try {
					psSelect.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (psUpdate != null) {
				try {
					psUpdate.close();
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
	// REGISTER FAMILY SUBSCRIBER
	// =========================================================
	public boolean registerFamilySubscriber(ArrayList<String> data) {

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
	public boolean submitParkRequest(ArrayList<String> data) {

		String parkName = data.get(0);
		String requestType = data.get(1);
		String oldValue = data.get(2);
		String newValue = data.get(3);

		Connection conn = null;

		try {
			conn = pool.getConnection();

			int parkId = -1;

			PreparedStatement findPark = conn.prepareStatement("SELECT parkId FROM Parks WHERE parkName = ?");
			findPark.setString(1, parkName);

			ResultSet rs = findPark.executeQuery();

			if (rs.next()) {
				parkId = rs.getInt("parkId");
			} else {
				rs.close();
				findPark.close();
				return false;
			}

			rs.close();
			findPark.close();

			String insertQuery = "INSERT INTO Requests (parkId, requestType, oldValue, newValue, status) "
					+ "VALUES (?, ?, ?, ?, 'Pending')";

			PreparedStatement ps = conn.prepareStatement(insertQuery);
			ps.setInt(1, parkId);
			ps.setString(2, requestType);
			ps.setString(3, oldValue);
			ps.setString(4, newValue);

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
	// GET PARK CURRENT VALUE
	// =========================================================
	public String getParkCurrentValue(String parkName, String requestType) {

		Connection conn = null;

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
				columnName = "OpenCasualSpots";
				break;
			case "Promotion":
				return "Promotion request";
			default:
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
	// GET PARK ID BY NAME
	// =========================================================
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
						rs.getTimestamp("exitTime"), rs.getString("orderType"));

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

		String query = "INSERT INTO Visits (parkId, orderId, visitorId, actualVisitorCount, entryTime, exitTime) "
				+ "VALUES (?, NULL, ?, ?, NOW(), NULL)";

		Connection conn = null;
		PreparedStatement ps = null;

		try {
			conn = pool.getConnection();
			ps = conn.prepareStatement(query);

			ps.setInt(1, parkId);
			ps.setString(2, visitorId);
			ps.setInt(3, visitorCount);

			int rowsAffected = ps.executeUpdate();

			if (rowsAffected > 0) {
				// The visit was inserted successfully, update the current visitor count
				// NOTE: updateCurrentVisitorCount should also DECREASE OpenCasualSpots
				boolean isCountUpdated = updateCasualVisitorCount(parkId, visitorCount);

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
	// GET PARK MAX CAPACITY
	// =========================================================
	public int getParkMaxCapacity(int parkId) {

		String query = "SELECT maxCapacity FROM Parks WHERE parkId = ?";
		Connection conn = null;

		try {
			conn = pool.getConnection();

			PreparedStatement ps = conn.prepareStatement(query);
			ps.setInt(1, parkId);

			ResultSet rs = ps.executeQuery();

			if (rs.next()) {
				int capacity = rs.getInt("maxCapacity");
				rs.close();
				ps.close();
				return capacity;
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
	// GET APPROVED VISITOR COUNT FOR SLOT (Supports Exclusions)
	// =========================================================
	public int getApprovedVisitorCountForSlot(int parkId, String visitDate, String visitTime, int excludeOrderId) {
		String query = "SELECT IFNULL(SUM(visitorCount), 0) AS totalVisitors FROM Orders "
				+ "WHERE parkId = ? AND visitDate = ? AND visitTime = ? AND status = 'Approved'";

		// THE FIX: If an excludeOrderId is provided, ignore it so we don't double-count
		// during updates!
		if (excludeOrderId > 0) {
			query += " AND orderId != ?";
		}

		Connection conn = null;
		try {
			conn = pool.getConnection();
			PreparedStatement ps = conn.prepareStatement(query);
			ps.setInt(1, parkId);
			ps.setString(2, visitDate);
			ps.setString(3, visitTime);

			if (excludeOrderId > 0) {
				ps.setInt(4, excludeOrderId);
			}

			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				int total = rs.getInt("totalVisitors");
				rs.close();
				ps.close();
				return total;
			}
			rs.close();
			ps.close();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (conn != null)
				pool.releaseConnection(conn);
		}
		return 0;
	}

	// OVERLOAD: For new orders (Doesn't exclude any IDs)
	public int getApprovedVisitorCountForSlot(int parkId, String visitDate, String visitTime) {
		return getApprovedVisitorCountForSlot(parkId, visitDate, visitTime, -1);
	}

	// =========================================================
	// CHECK IF THERE IS ROOM IN SLOT (Supports Exclusions)
	// =========================================================
	public boolean hasRoomInSlot(int parkId, String visitDate, String visitTime, int requestedVisitors,
			int excludeOrderId) {
		int maxCapacity = getParkMaxCapacity(parkId);
		int approvedVisitors = getApprovedVisitorCountForSlot(parkId, visitDate, visitTime, excludeOrderId);

		if (maxCapacity == -1) {
			return false;
		}

		return approvedVisitors + requestedVisitors <= maxCapacity;
	}

	// OVERLOAD: For new orders (Doesn't exclude any IDs)
	public boolean hasRoomInSlot(int parkId, String visitDate, String visitTime, int requestedVisitors) {
		return hasRoomInSlot(parkId, visitDate, visitTime, requestedVisitors, -1);
	}

	// =========================================================
	// TRY TO PROMOTE FIRST WAITING ORDER
	// =========================================================
	public boolean promoteWaitingOrderIfPossible(int parkId, String visitDate, String visitTime) {

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

				if (hasRoomInSlot(parkId, visitDate, visitTime, visitorCount)) {
					PreparedStatement updatePs = conn
							.prepareStatement("UPDATE Orders SET status = 'Approved' WHERE orderId = ?");
					updatePs.setInt(1, orderId);

					int rows = updatePs.executeUpdate();
					updatePs.close();

					return rows > 0;
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

			if (conn != null) {
				pool.releaseConnection(conn);
			}
		}

		return false;
	}

	// =========================================================
	// CANCEL ORDER
	// =========================================================
	public boolean cancelOrder(int orderId) {

		Connection conn = null;
		PreparedStatement selectPs = null;
		PreparedStatement updatePs = null;
		ResultSet rs = null;

		try {
			conn = pool.getConnection();

			String selectQuery = "SELECT parkId, visitDate, visitTime FROM Orders WHERE orderId = ?";
			selectPs = conn.prepareStatement(selectQuery);
			selectPs.setInt(1, orderId);

			rs = selectPs.executeQuery();

			if (!rs.next()) {
				return false;
			}

			int parkId = rs.getInt("parkId");
			String visitDate = rs.getString("visitDate");
			String visitTime = rs.getString("visitTime");

			String updateQuery = "UPDATE Orders SET status = 'Canceled' WHERE orderId = ?";
			updatePs = conn.prepareStatement(updateQuery);
			updatePs.setInt(1, orderId);

			int rows = updatePs.executeUpdate();

			if (rows > 0) {
				promoteWaitingOrderIfPossible(parkId, visitDate, visitTime);
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
	public boolean updateCasualVisitorCount(int parkId, int countChange) {
		String updateQuery = "UPDATE Parks SET CurrentVisitorCount = CurrentVisitorCount + ?, "
				+ "OpenCasualSpots = OpenCasualSpots - ? WHERE parkId = ?";

		Connection conn = null;
		PreparedStatement ps = null;

		try {
			conn = pool.getConnection();
			ps = conn.prepareStatement(updateQuery);

			ps.setInt(1, countChange);
			ps.setInt(2, countChange);
			ps.setInt(3, parkId);

			return ps.executeUpdate() > 0;

		} catch (SQLException e) {
			e.printStackTrace();
			return false;

		} finally {
			try {
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
	// GET ALTERNATIVE SLOTS
	// =========================================================
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
	public boolean addOrderToWaitingList(ArrayList<String> orderData) {

		String visitorId = orderData.get(0);
		String parkName = orderData.get(1);
		String visitDate = orderData.get(2);
		String visitTime = orderData.get(3);
		int visitorCount = Integer.parseInt(orderData.get(4));
		String email = orderData.get(5);
		String orderType = orderData.get(6);

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
					+ "(parkId, visitorId, visitDate, visitTime, visitorCount, email, orderType, status) "
					+ "VALUES (?, ?, ?, ?, ?, ?, ?, 'WaitingList')";

			ps = conn.prepareStatement(insert);

			ps.setInt(1, parkId);
			ps.setString(2, visitorId);
			ps.setString(3, visitDate);
			ps.setString(4, visitTime);
			ps.setInt(5, visitorCount);
			ps.setString(6, email);
			ps.setString(7, orderType);

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
	// UPDATE COUNT FOR RESERVED VISITORS ONLY
	// =========================================================
	public boolean updateReservedVisitorCount(int parkId, int countChange) {
		String updateQuery = "UPDATE Parks SET CurrentVisitorCount = CurrentVisitorCount + ? " + "WHERE parkId = ?";

		Connection conn = null;
		PreparedStatement ps = null;

		try {
			conn = pool.getConnection();
			ps = conn.prepareStatement(updateQuery);

			ps.setInt(1, countChange);
			ps.setInt(2, parkId);

			return ps.executeUpdate() > 0;

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
	// APPROVE REQUEST
	// =========================================================
	public boolean approveRequest(int requestId) {

		Connection conn = null;

		try {
			conn = pool.getConnection();
			conn.setAutoCommit(false);

			// 1. Fetch request details
			String selectQuery = "SELECT parkId, requestType, newValue FROM Requests WHERE requestId = ?";
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
			// CASE 2: Request is for updating CasualGap (New Logic Added)
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
				int currentCasualGap = parkRs.getInt("casualGap");
				int requestedCasualGap = Integer.parseInt(newValue);

				parkRs.close();
				parkPs.close();

				// Business Rule Validation: New gap cannot exceed or equal total max capacity
				if (requestedCasualGap >= maxCapacity) {
					conn.rollback();
					return false;
				}

				// Calculate the difference between the new gap and the old gap
				int gapDifference = requestedCasualGap - currentCasualGap;

				// Update the casualGap inside the Parks table
				PreparedStatement updateGapPs = conn
						.prepareStatement("UPDATE Parks SET casualGap = ? WHERE parkId = ?");
				updateGapPs.setInt(1, requestedCasualGap);
				updateGapPs.setInt(2, parkId);
				updateGapPs.executeUpdate();
				updateGapPs.close();

				// Dynamic adjustment: Add the gap difference directly to the current open
				// casual spots
				String updateSpotsQuery = "UPDATE Parks SET OpenCasualSpots = OpenCasualSpots + ? WHERE parkId = ?";
				PreparedStatement updateSpotsPs = conn.prepareStatement(updateSpotsQuery);
				updateSpotsPs.setInt(1, gapDifference);
				updateSpotsPs.setInt(2, parkId);
				updateSpotsPs.executeUpdate();
				updateSpotsPs.close();
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
				row.add(String.valueOf(rs.getInt("parkId")));
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
						rs.getString("email"), rs.getString("orderType"), rs.getString("status")));
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
	public ArrayList<String> getParkDashboardData(String parkName) {
		ArrayList<String> parkData = new ArrayList<>();
		String query = "SELECT parkName, maxCapacity, casualGap, avgStayDuration, CurrentVisitorCount, OpenCasualSpots "
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
				parkData.add(String.valueOf(rs.getInt("OpenCasualSpots")));
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
}