package Database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.Queue;

import Server.EchoServer;

public class DBConnectionPool {

	private static DBConnectionPool instance;
	private EchoServer server;

	private final Queue<Connection> pool = new LinkedList<>();
	private final int MAX_POOL_SIZE = 10;

	private final String url = "jdbc:mysql://localhost:3306/gonature?serverTimezone=Asia/Jerusalem&useSSL=false";
	private final String user = "root";
	private final String password = "RDac2027"; // CHANGE PASSWORD HERE

	private DBConnectionPool(EchoServer server) {
		this.server = server;
		initializePool();

		server.log("MySQL Connection Pool initialized with " + MAX_POOL_SIZE + " connections");
	}

	public static synchronized DBConnectionPool getInstance(EchoServer server) {
		if (instance == null) {
			instance = new DBConnectionPool(server);
		}
		return instance;
	}

	private void initializePool() {
		try {
			for (int i = 0; i < MAX_POOL_SIZE; i++) {
				pool.add(createNewConnection());
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private Connection createNewConnection() throws SQLException {
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

		return DriverManager.getConnection(url, user, password);
	}

	public synchronized Connection getConnection() throws SQLException {
		if (!pool.isEmpty()) {
			return pool.poll();
		}
		return createNewConnection(); // fallback
	}

	public synchronized void releaseConnection(Connection conn) {
		if (conn == null)
			return;

		try {
			if (conn.isClosed()) {
				pool.add(createNewConnection());
			} else {
				pool.add(conn);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public synchronized void closeAll() {
		for (Connection conn : pool) {
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		pool.clear();
	}
}