package Database;

import java.sql.*;
import java.util.ArrayList;

import Common.Order;
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

		String query = "SELECT * FROM Orders WHERE visitorId = ?";

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
	// EMPLOYEE ROLE
	// =========================================================
	public String getEmployeeRole(ArrayList<String> empData) {

		String query = "SELECT role FROM Employees WHERE username = ? AND password = ?";

		Connection conn = null;

		try {
			conn = pool.getConnection();

			PreparedStatement ps = conn.prepareStatement(query);
			ps.setString(1, empData.get(0));
			ps.setString(2, empData.get(1));

			ResultSet rs = ps.executeQuery();

			if (rs.next()) {
				String role = rs.getString("role");

				rs.close();
				ps.close();

				return role;
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
	public ArrayList<Order> getVisitReport(int parkId, int month, int year) {

		ArrayList<Order> result = new ArrayList<>();

		String query = "SELECT * FROM Orders " + "WHERE parkId = ? AND YEAR(visitDate) = ? AND MONTH(visitDate) = ?";

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
}