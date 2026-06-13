package Database;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Common.Order;
import Common.UsageReportData;
import Common.Visit;
import Server.EchoServer;

public class DBController {

	private EchoServer server;
	private DBConnectionPool pool;

	public DBController(EchoServer server) {
		this.server = server;
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
	// CHECK AVAILABILITY (CAPACITY BASED)
	// =========================================================
	public boolean isTimeSlotAvailable(String parkName, String visitDate, String visitTime, int requestedVisitors) {
		Connection conn = null;
		try {
			conn = pool.getConnection();

			// 1. Get the parkId AND the specific park's maximum capacity!
			int parkId = -1;
			int maxCapacity = 0;

			PreparedStatement findPark = conn
					.prepareStatement("SELECT parkId, maxCapacity FROM Parks WHERE parkName = ?");
			findPark.setString(1, parkName);
			ResultSet rsPark = findPark.executeQuery();

			if (rsPark.next()) {
				parkId = rsPark.getInt("parkId");
				maxCapacity = rsPark.getInt("maxCapacity"); // We pull the limit from the DB!
			} else {
				return false; // Park not found
			}
			rsPark.close();
			findPark.close();

			// 2. Sum up all the visitors who ALREADY have orders for this exact date and
			// time.
			// PRO TIP: We add "AND status != 'Cancelled'" because cancelled orders don't
			// take up space!
			String checkQuery = "SELECT SUM(visitorCount) AS totalVisitors FROM Orders "
					+ "WHERE parkId = ? AND visitDate = ? AND visitTime = ? AND status != 'Cancelled'";
			PreparedStatement psCheck = conn.prepareStatement(checkQuery);
			psCheck.setInt(1, parkId);
			psCheck.setString(2, visitDate);
			psCheck.setString(3, visitTime);

			ResultSet rsCheck = psCheck.executeQuery();
			int currentBookedVisitors = 0;

			if (rsCheck.next()) {
				currentBookedVisitors = rsCheck.getInt("totalVisitors");
			}

			rsCheck.close();
			psCheck.close();

			// 3. The Ultimate Capacity Decision
			System.out.println("[DB] Park: " + parkName + " | Max Capacity: " + maxCapacity + " | Currently Booked: "
					+ currentBookedVisitors + " | Requesting: " + requestedVisitors);

			if ((currentBookedVisitors + requestedVisitors) > maxCapacity) {
				return false; // NOT AVAILABLE! Letting them in would exceed the park's limit.
			}

			return true; // AVAILABLE! There is enough room.

		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		} finally {
			if (conn != null) {
				pool.releaseConnection(conn);
			}
		}
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
	// CREATE ORDER
	// =========================================================
	public boolean createNewOrder(ArrayList<String> orderData) {

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

			// 1. find parkId
			int parkId = -1;

			PreparedStatement find = conn.prepareStatement("SELECT parkId FROM Parks WHERE parkName = ?");
			find.setString(1, parkName);

			ResultSet rs = find.executeQuery();

			if (rs.next()) {
				parkId = rs.getInt("parkId");
			} else {
				return false;
			}

			rs.close();
			find.close();

			// 2. insert order
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

			return rows > 0;

		} catch (SQLException e) {
			e.printStackTrace();
			return false;

		} finally {
			pool.releaseConnection(conn);
		}
	}

	// =========================================================
	// UPDATE ORDER
	// =========================================================
	public boolean updateOrder(int orderNumber, String orderDate, int numberOfVisitors) {

		String query = "UPDATE Orders SET visitDate = ?, visitorCount = ? WHERE orderId = ?";

		Connection conn = null;

		try {
			conn = pool.getConnection();

			PreparedStatement ps = conn.prepareStatement(query);

			ps.setDate(1, Date.valueOf(orderDate));
			ps.setInt(2, numberOfVisitors);
			ps.setInt(3, orderNumber);

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
	// REPORTS - CANCELLATION
	// =========================================================
	public ArrayList<Order> getCancellationReport(int parkId, int month, int year) {

		ArrayList<Order> result = new ArrayList<>();

		String query = "SELECT * FROM Orders " + "WHERE parkId = ? AND status = 'CANCELLED' "
				+ "AND YEAR(visitDate) = ? AND MONTH(visitDate) = ?";

		Connection conn = null;

		try {
			conn = pool.getConnection();

			PreparedStatement ps = conn.prepareStatement(query);
			ps.setInt(1, parkId);
			ps.setInt(2, year);
			ps.setInt(3, month);

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

	// =========================================================
	// ENTER VISITOR
	// =========================================================
	public boolean enterVisitor(String visitorId) {

		String query = "INSERT INTO Visits (parkId, orderId, visitorId, actualVisitorCount, entryTime) "
				+ "SELECT parkId, orderId, visitorId, visitorCount, NOW() " + "FROM Orders "
				+ "WHERE visitorId = ? AND status = 'Approved' " + "ORDER BY orderId DESC LIMIT 1";

		Connection conn = null;

		try {
			conn = pool.getConnection();

			PreparedStatement ps = conn.prepareStatement(query);
			ps.setString(1, visitorId);

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
	// EXIT VISITOR
	// =========================================================
	public boolean exitVisitor(String visitorId) {

		String query = "UPDATE Visits SET exitTime = NOW() " + "WHERE visitorId = ? AND exitTime IS NULL "
				+ "ORDER BY visitId DESC LIMIT 1";

		Connection conn = null;

		try {
			conn = pool.getConnection();

			PreparedStatement ps = conn.prepareStatement(query);
			ps.setString(1, visitorId);

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

	public int getParkIdByName(String parkName) {
	    if (parkName == null) return -1;

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

	public ArrayList<UsageReportData> getUsageReport(String parkName, int year) {

		ArrayList<UsageReportData> result = new ArrayList<>();
		Connection conn = null;

		try {
			conn = pool.getConnection();

			int parkId = getParkIdByName(parkName);

			if (parkId == -1) {
				return result;
			}

			String sql = "SELECT " + "MONTH(v.entryTime) AS month, " + "DAY(v.entryTime) AS day, "
					+ "SUM(v.actualVisitorCount) AS dailyVisitors, " + "p.maxCapacity " + "FROM Visits v "
					+ "JOIN Parks p ON v.parkId = p.parkId " + "WHERE v.parkId = ? " + "AND YEAR(v.entryTime) = ? "
					+ "GROUP BY MONTH(v.entryTime), DAY(v.entryTime), p.maxCapacity " + "ORDER BY month, day";

			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setInt(1, parkId);
			ps.setInt(2, year);

			ResultSet rs = ps.executeQuery();

			// month -> list of daily percentages
			Map<Integer, List<Double>> monthlyUsage = new HashMap<>();

			while (rs.next()) {

				int month = rs.getInt("month");
				int dailyVisitors = rs.getInt("dailyVisitors");
				int maxCapacity = rs.getInt("maxCapacity");

				double dailyPercent = maxCapacity == 0 ? 0 : ((double) dailyVisitors / maxCapacity) * 100;

				monthlyUsage.computeIfAbsent(month, k -> new ArrayList<>()).add(dailyPercent);
			}

			rs.close();
			ps.close();

			for (Map.Entry<Integer, List<Double>> entry : monthlyUsage.entrySet()) {

				int month = entry.getKey();
				List<Double> values = entry.getValue();

				double avgMonthlyPercent = values.stream().mapToDouble(Double::doubleValue).average().orElse(0);

				result.add(new UsageReportData(month, avgMonthlyPercent));
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
}