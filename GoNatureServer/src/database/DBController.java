package database;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Properties;

/*Handle all database operations of the server*/
public class DBController {
	
	private Connection conn;
	
	public void connect() throws SQLException, IOException{
		Properties props = new Properties();
        FileInputStream fis = new FileInputStream("db.properties");
        props.load(fis);

        String url = props.getProperty("db.url");
        String user = props.getProperty("db.user");
        String password = props.getProperty("db.password");

        conn = DriverManager.getConnection(url, user, password);
	}
	
	public boolean isConnected() {
        try {
            return conn != null && !conn.isClosed();
        } catch (SQLException e) {
            return false;
        }
    }
	
	//reads an order from the database by order number
	public ArrayList<String> getOrderByNumber(int orderNumber) throws SQLException {
        String sql = "SELECT * FROM `Order` WHERE order_number = ?";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt(1, orderNumber);

        ResultSet rs = ps.executeQuery();

        if (rs.next()) {
            ArrayList<String> result = new ArrayList<>();
            result.add("ORDER_RESULT");
            result.add(String.valueOf(rs.getInt("order_number")));
            result.add(String.valueOf(rs.getDate("order_date")));
            result.add(String.valueOf(rs.getInt("number_of_visitors")));
            result.add(String.valueOf(rs.getInt("confirmation_code")));
            result.add(String.valueOf(rs.getInt("subscriber_id")));
            result.add(String.valueOf(rs.getDate("date_of_placing_order")));
            return result;
        }
        return null;
    }
	
	//update order date and number of visitors
	public boolean updateOrder(int orderNumber, String orderDate, int numberOfVisitors) throws SQLException {
        String sql = "UPDATE `Order` SET order_date = ?, number_of_visitors = ? WHERE order_number = ?";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setDate(1, Date.valueOf(orderDate));
        ps.setInt(2, numberOfVisitors);
        ps.setInt(3, orderNumber);

        return ps.executeUpdate() > 0;
    }
}

	