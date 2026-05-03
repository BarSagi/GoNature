package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;

public class DBController {
	
	
	private Connection connectToDB() {
		try {
			Connection conn = DriverManager.getConnection(
					"jdbc:mysql://localhost:3306/gonature?allowLoadLocalInfile=true&serverTimezone=Asia/Jerusalem&useSSL=false",
					"root", "RDac2027");
			return conn;
		} catch (SQLException ex) {/* handle any errors */
			System.out.println("SQLException: " + ex.getMessage());
			System.out.println("SQLState: " + ex.getSQLState());
			System.out.println("VendorError: " + ex.getErrorCode());
		}

		return null;
	}
	
	private void saveOrderToDB(Connection conn, ArrayList<String> msg) {
		try {
			PreparedStatement ps = conn
					.prepareStatement("INSERT INTO orders (order_number, order_date, number_of_visitors, confirmation_code, subscriber_id, date_of_placing_order) VALUES(?,?,?,?,?,?)");

			ps.setString(1, msg.get(0));
			ps.setString(2, msg.get(1));
			ps.setString(3, msg.get(2));
			ps.setString(4, msg.get(3));
			ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	private void ReadFromDB(Connection conn, ArrayList<String> msg) {
		try {
			PreparedStatement ps = conn
					.prepareStatement("SELECT * FROM Orders;");

			
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}

