package Database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.Queue;

import Server.EchoServer;

/**
 * Manages a pool of database connections for the GoNature system. This class
 * creates, stores, provides, releases, and closes MySQL connections. It uses
 * the Singleton pattern so only one connection pool instance exists.
 */
public class DBConnectionPool {

	private static DBConnectionPool instance;

	private final Queue<Connection> pool = new LinkedList<>();
	private final int MAX_POOL_SIZE = 10;

	private final String url = "jdbc:mysql://localhost:3306/gonature?serverTimezone=Asia/Jerusalem&useSSL=false";
	private final String user = "root";
	private final String password = "Shirpot111!"; // CHANGE PASSWORD HERE

	/**
	 * Creates a new database connection pool and initializes it with connections.
	 *
	 * @param server the server instance used for logging pool initialization
	 */
	private DBConnectionPool(EchoServer server) {
		initializePool();

		server.log("MySQL Connection Pool initialized with " + MAX_POOL_SIZE + " connections");
	}

	/**
	 * Returns the single instance of the database connection pool. If the pool does
	 * not exist yet, it creates a new one.
	 *
	 * @param server the server instance used when creating the pool
	 * @return the single database connection pool instance
	 */
	public static synchronized DBConnectionPool getInstance(EchoServer server) {
		if (instance == null) {
			instance = new DBConnectionPool(server);
		}
		return instance;
	}

	/**
	 * Initializes the connection pool with a fixed number of database connections.
	 */
	private void initializePool() {
		try {
			for (int i = 0; i < MAX_POOL_SIZE; i++) {
				pool.add(createNewConnection());
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Creates a new connection to the MySQL database.
	 *
	 * @return a new database connection
	 * @throws SQLException if a database access error occurs
	 */
	private Connection createNewConnection() throws SQLException {
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

		return DriverManager.getConnection(url, user, password);
	}

	/**
	 * Gets an available connection from the pool. If the pool is empty, a new
	 * connection is created as a fallback.
	 *
	 * @return an available database connection
	 * @throws SQLException if a database access error occurs
	 */
	public synchronized Connection getConnection() throws SQLException {
		if (!pool.isEmpty()) {
			return pool.poll();
		}
		return createNewConnection(); // fallback
	}

	/**
	 * Releases a database connection back into the pool. If the connection is
	 * closed, a new connection is created and added instead.
	 *
	 * @param conn the connection to release back to the pool
	 */
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

	/**
	 * Closes all database connections in the pool and clears the pool.
	 */
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