package Database;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import Common.Order;
import Server.EchoServer;

//Handle all database operations of the server
public class DBController {
	private EchoServer server;
	private Connection conn;

	public DBController(EchoServer server) {
		this.server = server;
		connectToDB();
	}

	public void connectToDB() { // connection to database
		try {
			conn = DriverManager.getConnection(
					"jdbc:mysql://localhost:3306/GoNature?allowLoadLocalInfile=true&serverTimezone=Asia/Jerusalem&useSSL=false",
					"root", "2066");
			/*
			 * CHANGE THE PASSWORD HERE!!!! ALSO MAKE SURE MYSQL IS OPEN AND YOU CHANGED THE
			 * mysql-connetor JAR PATH!!! (BUILD PATH-> CONFIGURE BUILD PATH-> CLASSPATH->
			 * CLICK THE JAR-> EDIT ON THE RIGHT-> CHOOSE THE JAR'S LOCATION)
			 */
			if (server != null) {
				server.log("Connected to MySQL");
			}
		} catch (SQLException e) {

			e.printStackTrace();
		}
	}

	public Connection getConnection() {
		return conn;
	}

	public boolean isConnected() {
		try {
			return conn != null && !conn.isClosed();
		} catch (SQLException e) {
			return false;
		}

	}

	// this method will create a 2 dimensional array that will contain all orders
	public ArrayList<Order> getAllOrders() throws SQLException {
		String query = "SELECT * FROM `orders`";
		PreparedStatement prepareStatement = conn.prepareStatement(query);
		ResultSet resultSet = prepareStatement.executeQuery();
		ArrayList<Order> result = new ArrayList<>();
		while (resultSet.next()) {
			Order order = new Order(resultSet.getInt("orderId"), resultSet.getInt("parkId"),
					resultSet.getString("visitorId"), resultSet.getDate("visitDate"), resultSet.getTime("visitTime"),
					resultSet.getInt("visitorCount"), resultSet.getString("email"), resultSet.getString("orderType"),
					resultSet.getString("status"));
			result.add(order);
		}
		return result;
	}

	/**
	 * Registers a new Casual visitor in the database.
	 * 
	 * @param visitorData ArrayList containing: [0] ID, [1] First Name, [2] Last
	 *                    Name, [3] Phone, [4] Email
	 * @return true if insertion was successful, false otherwise
	 */
	public boolean registerNewVisitor(ArrayList<String> visitorData) {
		// 1. Extract data from the ArrayList based on the indices we set in the GUI
		// Controller
		String id = visitorData.get(0);
		String firstName = visitorData.get(1);
		String lastName = visitorData.get(2);
		String phone = visitorData.get(3);
		String email = visitorData.get(4);

		// 2. Prepare the SQL INSERT query
		// Note: We explicitly set visitorType to 'Casual'.
		// subscriptionNumber remains NULL, and familyMembers will auto-set to your
		// DEFAULT 1.
		String insertQuery = "INSERT INTO Visitors (visitorId, firstName, lastName, phone, email, visitorType) "
				+ "VALUES (?, ?, ?, ?, ?, 'Casual')";

		try (PreparedStatement insertStmt = conn.prepareStatement(insertQuery)) {
			insertStmt.setString(1, id);
			insertStmt.setString(2, firstName);
			insertStmt.setString(3, lastName);
			insertStmt.setString(4, phone);
			insertStmt.setString(5, email);

			// 4. Execute the update
			int rowsAffected = insertStmt.executeUpdate();

			// If rowsAffected > 0, the new visitor was successfully saved to the DB!
			if (rowsAffected > 0) {
				System.out.println("DBController: Successfully registered new Casual visitor with ID: " + id);
				return true;
			} else {
				return false;
			}

		} catch (SQLException e) {
			System.out.println("DB Error: Failed to register new visitor.");
			e.printStackTrace();
			// If a Duplicate Key error occurs (user already exists), it will be caught
			// here!
			return false;
		}
	}

	public boolean createNewOrder(ArrayList<String> orderData) {
		// orderData index mapping based on our Client setup:
		// 0: visitorId, 1: parkName, 2: visitDate, 3: visitTime, 4: visitorsAmount, 5:
		// email, 6: orderType

		String visitorId = orderData.get(0);
		String parkName = orderData.get(1);
		String visitDate = orderData.get(2);
		String visitTime = orderData.get(3);
		int visitorCount = Integer.parseInt(orderData.get(4));
		String email = orderData.get(5);
		String orderType = orderData.get(6);

		int parkId = -1;

		// --- STEP 1: Translate Park Name to Park ID ---
		String findParkQuery = "SELECT parkId FROM Parks WHERE parkName = ?";
		try (PreparedStatement parkStmt = conn.prepareStatement(findParkQuery)) {
			parkStmt.setString(1, parkName);
			ResultSet rs = parkStmt.executeQuery();

			if (rs.next()) {
				parkId = rs.getInt("parkId");
			} else {
				System.out.println("DB Error: Park name '" + parkName + "' not found in the Parks table.");
				return false; // Cannot create an order without a valid parkId
			}
		} catch (SQLException e) {
			System.out.println("DB Error: Failed to fetch park ID.");
			e.printStackTrace();
			return false;
		}

		// --- STEP 2: Insert the Order into the Orders Table ---
		// Note: 'status' is an ENUM. We default it to 'Approved' here,
		// but later you can add logic to set it to 'WaitingList' if the park is full.
		String insertQuery = "INSERT INTO Orders (parkId, visitorId, visitDate, visitTime, visitorCount, email, orderType, status) "
				+ "VALUES (?, ?, ?, ?, ?, ?, ?, 'Approved')";

		try (PreparedStatement insertStmt = conn.prepareStatement(insertQuery)) {
			insertStmt.setInt(1, parkId);
			insertStmt.setString(2, visitorId);
			insertStmt.setString(3, visitDate);
			insertStmt.setString(4, visitTime);
			insertStmt.setInt(5, visitorCount);
			insertStmt.setString(6, email);
			insertStmt.setString(7, orderType);

			// executeUpdate() returns the number of rows affected.
			// If it's greater than 0, the insertion was successful!
			int rowsAffected = insertStmt.executeUpdate();
			return rowsAffected > 0;

		} catch (SQLException e) {
			System.out.println("DB Error: Failed to insert new order.");
			e.printStackTrace();
			return false;
		}
	}

	// this method will update order date and number of visitors
	public boolean updateOrder(int orderNumber, String orderDate, int numberOfVisitors) throws SQLException {
		String query = "UPDATE `orders` SET order_date = ?, number_of_visitors = ? WHERE order_number = ?";
		PreparedStatement prepareStatement = conn.prepareStatement(query);
		prepareStatement.setDate(1, Date.valueOf(orderDate));
		prepareStatement.setInt(2, numberOfVisitors);
		prepareStatement.setInt(3, orderNumber);
		return prepareStatement.executeUpdate() > 0; // returns true if 1 or more rows affected
	}

//=======================================================================================================================
//================================ GET VISITOR ORDERS ===================================================================
//=======================================================================================================================
	public ArrayList<Order> getVisitorOrders(String visitorId) {
		ArrayList<Order> ordersList = new ArrayList<>();

		// Updated query to match the exact table name and column name from your schema
		String query = "SELECT * FROM Orders WHERE visitorId = ?";

		try {
			PreparedStatement pstmt = conn.prepareStatement(query); // assuming 'conn' is your Connection
			pstmt.setString(1, visitorId);

			ResultSet rs = pstmt.executeQuery();

			while (rs.next()) {
				Order order = new Order(rs.getInt("orderId"), rs.getInt("parkId"), rs.getString("visitorId"),
						rs.getDate("visitDate"), rs.getTime("visitTime"), rs.getInt("visitorCount"),
						rs.getString("email"), rs.getString("orderType"), rs.getString("status"));
				ordersList.add(order);
			}

			rs.close();
			pstmt.close();

		} catch (SQLException e) {
			System.out.println("Error fetching orders for visitor: " + visitorId);
			e.printStackTrace();
		}

		return ordersList;
	}

	public String getEmployeeRole(ArrayList<String> empData) {
		String query = "SELECT role FROM Employees WHERE username = ? AND password = ?";

		try {
			PreparedStatement ps = conn.prepareStatement(query);
			ps.setString(1, empData.get(0)); // username
			ps.setString(2, empData.get(1)); // password

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
			System.out.println("Error fetching employee role: " + empData.get(0));
			e.printStackTrace();
		}

		return null; // employee not found
	}

	// =======================================================================================================================
	// ================================ GET REPORTS DATA
	// ====================================================================
	// =======================================================================================================================
	public ArrayList<Order> getVisitReport(int parkId, int month, int year) {
		ArrayList<Order> result = new ArrayList<>();

		String query = "SELECT * FROM Orders " + "WHERE parkId = ? AND YEAR(visitDate) = ? AND MONTH(visitDate) = ?";

		try (PreparedStatement stmt = conn.prepareStatement(query)) {

			stmt.setInt(1, parkId);
			stmt.setInt(2, year);
			stmt.setInt(3, month);

			ResultSet rs = stmt.executeQuery();

			while (rs.next()) {
				result.add(new Order(rs.getInt("orderId"), rs.getInt("parkId"), rs.getString("visitorId"),
						rs.getDate("visitDate"), rs.getTime("visitTime"), rs.getInt("visitorCount"),
						rs.getString("email"), rs.getString("orderType"), rs.getString("status")));
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return result;
	}

	public ArrayList<Order> getCancellationReport(int parkId, int month, int year) {
		ArrayList<Order> result = new ArrayList<>();

		String query = "SELECT * FROM Orders " + "WHERE parkId = ? " + "AND orderStatus = 'CANCELLED' "
				+ "AND YEAR(visitDate) = ? AND MONTH(visitDate) = ?";

		try (PreparedStatement stmt = conn.prepareStatement(query)) {

			stmt.setInt(1, parkId);
			stmt.setInt(2, year);
			stmt.setInt(3, month);

			ResultSet rs = stmt.executeQuery();

			while (rs.next()) {
				result.add(new Order(rs.getInt("orderId"), rs.getInt("parkId"), rs.getString("visitorId"),
						rs.getDate("visitDate"), rs.getTime("visitTime"), rs.getInt("visitorCount"),
						rs.getString("email"), rs.getString("orderType"), rs.getString("status")));
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return result;
	}
}
