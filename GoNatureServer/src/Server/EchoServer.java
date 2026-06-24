package Server;

import Common.Message;
import Database.DBController;
import GUI.ServerConsoleController;
import OCSFUtils.AbstractServer;
import OCSFUtils.ConnectionToClient;
import Strategy.MessageStrategy;
import Strategy.StrategyFactory;
import javafx.application.Platform;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Server class responsible for handling client connections and messages. The
 * server receives messages from clients, sends them to the correct strategy,
 * manages connected users, and writes logs to the server console.
 */
public class EchoServer extends AbstractServer {

	public static EchoServer instance;
	private DBController database;

	private final Map<ConnectionToClient, Long> lastActivityMap = new ConcurrentHashMap<>();

	// ADDED: Map to keep track of logged in users to prevent double logins
	private final Map<String, ConnectionToClient> loggedInUsers = new ConcurrentHashMap<>();

	private static final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

	/**
	 * Constructs a new EchoServer with the given port.
	 *
	 * @param port the port number used by the server
	 */
	public EchoServer(int port) {
		super(port);
		instance = this;
	}

	/**
	 * Logs in a user if the user is not already connected.
	 *
	 * @param userId the ID of the user trying to log in
	 * @param client the client connection of the user
	 * @return true if the login succeeded, otherwise false
	 */
	public boolean loginUser(String userId, ConnectionToClient client) {
		if (loggedInUsers.containsKey(userId)) {
			return false; // User is already logged in!
		}
		loggedInUsers.put(userId, client);
		log("[USER LOGIN] User ID: " + userId + " logged in.");
		return true;
	}

	/**
	 * Logs out a user by removing the matching client connection from the logged-in
	 * users map.
	 *
	 * @param client the client connection to log out
	 */
	public void logoutUser(ConnectionToClient client) {
		loggedInUsers.entrySet().removeIf(entry -> {
			if (entry.getValue().equals(client)) {
				log("[USER LOGOUT] User ID: " + entry.getKey() + " has disconnected.");
				return true;
			}
			return false;
		});
	}

	/**
	 * Handles messages received from a client. The method updates client activity,
	 * handles connect and disconnect commands, and sends other commands to the
	 * matching strategy.
	 *
	 * @param msg    the message received from the client
	 * @param client the client connection that sent the message
	 */
	@Override
	public void handleMessageFromClient(Object msg, ConnectionToClient client) {

		if (database == null) {
			log("[ERROR] DB not initialized yet!");
			return;
		}

		try {
			Message message = (Message) msg;
			lastActivityMap.put(client, System.currentTimeMillis()); // map the last activity

			if (message.getCommand().equals("DISCONNECT")) {

				String compName = (String) client.getInfo("hostName");
				if (compName == null)
					compName = "Unknown";

				log("[CLIENT DISCONNECTED] Host: " + compName + " | IP: " + client.getInetAddress().getHostAddress());

				logoutUser(client); // ADDED: Remove user from logged-in map
				lastActivityMap.remove(client); // clean this user's activity map
				return;
			}

			else if (message.getCommand().equals("CONNECT")) {

				client.setInfo("hostName", message.getData());

				log("[CLIENT CONNECTED] Host: " + message.getData() + " | IP: "
						+ client.getInetAddress().getHostAddress());
				return;
			}

			log("[MESSAGE RECEIVED] Command: " + message.getCommand() + " | From: "
					+ client.getInetAddress().getHostAddress());

			MessageStrategy strategy = StrategyFactory.getStrategy(message.getCommand());

			if (strategy != null) {

				strategy.execute(message, client, this);

			} else {

				log("[WARNING] Unknown command received: " + message.getCommand());
			}

		} catch (Exception e) {

			log("[ERROR] Exception while handling client message: " + e.getMessage());
			e.printStackTrace();
		}
	}

	/**
	 * Called when the server starts listening for client connections. Initializes
	 * the idle checker and the database controller.
	 */
	protected void serverStarted() {
		log("[SYSTEM] Server listening for connections on port " + getPort());
		startIdleChecker();
		database = new DBController(this);
		startAutoCancelNoShowTask();
	}
	
	/**
	 * Starts a background task that automatically cancels approved orders
	 * if the visitor did not arrive within 30 minutes after the scheduled visit time.
	 */
	private void startAutoCancelNoShowTask() {
		Thread autoCancelThread = new Thread(() -> {
			while (true) {
				try {
					if (database != null) {
						int canceledOrders = database.autoCancelNoShowOrders();

						if (canceledOrders > 0) {
							log("[AUTO CANCEL] No-show orders canceled: " + canceledOrders);
						}
					}

					// Run the check every minute
					Thread.sleep(60 * 1000);

				} catch (Exception e) {
					log("[AUTO CANCEL ERROR] " + e.getMessage());
					e.printStackTrace();
				}
			}
		});

		autoCancelThread.setDaemon(true);
		autoCancelThread.start();
	}

	/**
	 * Called when the server stops listening for client connections.
	 */
	protected void serverStopped() {
		log("[SYSTEM] Server has stopped listening for connections.");
	}

	/**
	 * Handles a graceful client disconnection. Removes the client from the
	 * logged-in users map and activity map.
	 *
	 * @param client the disconnected client
	 */
	@Override
	synchronized protected void clientDisconnected(ConnectionToClient client) {
		logoutUser(client);
		lastActivityMap.remove(client);
		log("[SYSTEM] Client disconnected gracefully.");
	}

	/**
	 * Handles an abrupt client disconnection caused by an exception. Removes the
	 * client from the logged-in users map and activity map.
	 *
	 * @param client    the disconnected client
	 * @param exception the exception that caused the disconnection
	 */
	@Override
	synchronized protected void clientException(ConnectionToClient client, Throwable exception) {
		logoutUser(client);
		lastActivityMap.remove(client);
		log("[SYSTEM] Client disconnected abruptly: " + exception.getMessage());
	}

	/**
	 * Writes a timestamped message to the console and to the server GUI.
	 *
	 * @param msg the message to write
	 */
	public void log(String msg) {

		String timeStampedMsg = "[" + dtf.format(LocalDateTime.now()) + "] " + msg;

		System.out.println(timeStampedMsg);

		if (ServerConsoleController.instance != null) {
			Platform.runLater(new Runnable() {
				@Override
				public void run() {
					ServerConsoleController.instance.log(timeStampedMsg);
				}
			});
		}
	}

	/**
	 * Builds a text summary of all currently connected clients.
	 *
	 * @return a string containing information about connected clients
	 */
	public String getConnectedClientInfo() {
		StringBuilder sb = new StringBuilder();

		Thread[] clients = getClientConnections();

		if (clients.length == 0)
			return "No clients are currently connected.\n";

		sb.append("--- Connected Clients (Total: ").append(clients.length).append(") ---\n");

		for (Thread t : clients) {
			ConnectionToClient client = (ConnectionToClient) t;

			String compName = (String) client.getInfo("hostName");
			if (compName == null)
				compName = "Unknown";

			sb.append(" • Host: ").append(compName).append(" | IP: ").append(client.getInetAddress().getHostAddress())
					.append("\n");
		}

		sb.append("----------------------------------\n");

		return sb.toString();
	}

	/**
	 * Returns the database controller used by the server.
	 *
	 * @return the database controller
	 */
	public DBController getDatabase() {
		return database;
	}

	/**
	 * Starts a background thread that checks for inactive clients. If a client is
	 * idle for too long, the server disconnects it.
	 */
	private void startIdleChecker() {
		Thread t = new Thread(() -> {
			while (true) {
				try {
					Thread.sleep(5000); // check every 5 seconds
					long now = System.currentTimeMillis();

					for (ConnectionToClient client : lastActivityMap.keySet()) {
						long last = lastActivityMap.get(client);

						if (now - last > 200000_000) { // if the client is idle for more than 20 seconds
							String clientIp = "Unknown";
							if (client != null && client.getInetAddress() != null) {
								clientIp = client.getInetAddress().getHostAddress();
							}

							log("[IDLE TIMEOUT] Disconnecting client: " + clientIp);

							logoutUser(client); // ADDED: Remove user from logged-in map
							client.close(); // close the connection
							lastActivityMap.remove(client);
						}
					}

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

		t.setDaemon(true);
		t.start();
	}
}