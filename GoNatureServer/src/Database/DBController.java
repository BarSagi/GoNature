package Database;


import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

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
			if (server != null) {
				server.log("Connected to MySQL");
			}
        }
        catch (SQLException e) {

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
	public ArrayList<ArrayList<String>> getAllOrders() throws SQLException {
		String query = "SELECT * FROM `orders`";
		PreparedStatement prepareStatement = conn.prepareStatement(query);
		ResultSet resultSet = prepareStatement.executeQuery();
		ArrayList<ArrayList<String>> result = new ArrayList<>();
		while (resultSet.next()) {
	        ArrayList<String> row = new ArrayList<>();
			row.add(String.valueOf(resultSet.getInt("order_number")));
			row.add(String.valueOf(resultSet.getDate("order_date")));
			row.add(String.valueOf(resultSet.getInt("number_of_visitors")));
			row.add(String.valueOf(resultSet.getInt("confirmation_code")));
			row.add(String.valueOf(resultSet.getInt("subscriber_id")));
			row.add(String.valueOf(resultSet.getDate("date_of_placing_order")));
			result.add(row);

		}
		return result;
	}

	// this method will update order date and number of visitors
	public boolean updateOrder(int orderNumber, String orderDate, int numberOfVisitors) throws SQLException {
		String sql = "UPDATE `orders` SET order_date = ?, number_of_visitors = ? WHERE order_number = ?";
		PreparedStatement ps = conn.prepareStatement(sql);
		ps.setDate(1, Date.valueOf(orderDate));
		ps.setInt(2, numberOfVisitors);
		ps.setInt(3, orderNumber);
		return ps.executeUpdate() > 0;
	}

}
