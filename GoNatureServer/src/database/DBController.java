package database;

import common.Order;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DBController {
	
	private Connection conn;
	
	public void connect() throws SQLException{
		conn = DriverManager.getConnection(
			    "jdbc:mysql://localhost:3306/gonature?allowLoadLocalInfile=true&allowPublicKeyRetrieval=true&serverTimezone=Asia/Jerusalem&useSSL=false",
			    "root",
			    "YOUR PASSWORD HERE" //אין לי מושג איך לעשות את זה
			);
	}
	
	public boolean isConnected() {
        try {
            return conn != null && !conn.isClosed();
        } catch (SQLException e) {
            return false;
        }
    }
	
	public Order getOrderByNumber(int orderNumber) throws SQLException {
        String sql = "SELECT * FROM `Order` WHERE order_number = ?";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt(1, orderNumber);

        ResultSet rs = ps.executeQuery();

        if (rs.next()) {
            return new Order(
                rs.getInt("order_number"),
                rs.getDate("order_date"),
                rs.getInt("number_of_visitors"),
                rs.getInt("confirmation_code"),
                rs.getInt("subscriber_id"),
                rs.getDate("date_of_placing_order")
            );
        }

        return null;
    }

    public boolean updateOrder(int orderNumber, Date orderDate, int numberOfVisitors) throws SQLException {
        String sql = "UPDATE `Order` SET order_date = ?, number_of_visitors = ? WHERE order_number = ?";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setDate(1, orderDate);
        ps.setInt(2, numberOfVisitors);
        ps.setInt(3, orderNumber);

        return ps.executeUpdate() > 0;
    }
}

	