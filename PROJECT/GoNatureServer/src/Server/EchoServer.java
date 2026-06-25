package Server;

import Common.Message;
import Database.DBController;
import GUI.ServerConsoleController;
import OCSFUtils.AbstractServer;
import OCSFUtils.ConnectionToClient;
import Strategy.MessageStrategy;
import Strategy.StrategyFactory;
import javafx.application.Platform;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Server class responsible for handling client connections and messages. The
 * server receives messages from clients, dispatches them to the appropriate
 * processing strategy, manages connected users to prevent duplicate logins, and
 * handles server logging.
 */
public class EchoServer extends AbstractServer {

	/**
	 * Static instance of this server for access from other parts of the system.
	 */
	public static EchoServer instance;

	/**
	 * Controller for database interactions.
	 */
	private DBController database;

	/**
	 * Map to store the timestamp of the last activity for each connected client.
	 */
	private final Map<ConnectionToClient, Long> lastActivityMap = new ConcurrentHashMap<>();

	/**
	 * Map to keep track of logged-in users to prevent duplicate logins.
	 */
	private final Map<String, ConnectionToClient> loggedInUsers = new ConcurrentHashMap<>();

	/**
	 * Formatter for log timestamps.
	 */
	private static final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

	/**
	 * Scheduled executor for running background tasks.
	 */
	private ScheduledExecutorService backgroundTaskTimer;

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
	 * Attempts to log in a user. If the user is already logged in, the method
	 * returns false.
	 *
	 * @param userId the ID of the user trying to log in
	 * @param client the client connection associated with the user
	 * @return true if the login succeeded, otherwise false
	 */
	public boolean loginUser(String userId, ConnectionToClient client) {
		if (loggedInUsers.containsKey(userId)) {
			return false;
		}
		loggedInUsers.put(userId, client);
		log("[USER LOGIN] User ID: " + userId + " logged in.");
		return true;
	}

	/**
	 * Logs out a user by removing the associated client connection from the
	 * logged-in users map.
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
	 * Handles messages received from a client. Updates client activity, processes
	 * connect/disconnect commands, and routes other messages to the appropriate
	 * strategy.
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
			lastActivityMap.put(client, System.currentTimeMillis());

			if (message.getCommand().equals("DISCONNECT")) {

				String compName = (String) client.getInfo("hostName");
				if (compName == null)
					compName = "Unknown";

				log("[CLIENT DISCONNECTED] Host: " + compName + " | IP: " + client.getInetAddress().getHostAddress());

				logoutUser(client);
				lastActivityMap.remove(client);
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
	 * Initializes the server, starts the idle connection checker, the background
	 * system maintenance task, and initializes the database controller.
	 */
	@Override
	protected void serverStarted() {
		log("[SYSTEM] Server listening for connections on port " + getPort());
		startIdleChecker();
		database = new DBController(this);
		startSystemMaintenanceTask();
	}

	/**
	 * Schedules a background task to automatically perform system maintenance every
	 * minute. This includes canceling late orders, releasing expired pending
	 * confirmations, sending visit reminders, and releasing expired visit
	 * reminders.
	 */
	private void startSystemMaintenanceTask() {
		backgroundTaskTimer = Executors.newScheduledThreadPool(1);

		backgroundTaskTimer.scheduleAtFixedRate(() -> {
			try {
				// 1. Cancel orders that are 30+ minutes past their scheduled arrival time
				int canceledCount = database.cancelExpiredOrders();
				if (canceledCount > 0) {
					log("[SYSTEM] Auto-canceled " + canceledCount + " late orders.");
				}

				// 2. Release slots from visitors who failed to confirm their waiting list spot
				database.releaseExpiredPendingConfirmations();

				// 3. Send automated visit reminders for tomorrow's orders
				database.sendVisitRemindersForTomorrow();

				// 4. Cancel orders for visitors who did not respond to the reminder in time
				database.releaseExpiredVisitReminders();

			} catch (Exception e) {
				log("[ERROR] Error in system maintenance thread.");
				e.printStackTrace();
			}
		}, 1, 1, TimeUnit.MINUTES);
	}

	/**
	 * Called when the server stops listening for client connections. Shuts down the
	 * background task timer.
	 */
	@Override
	protected void serverStopped() {
		log("[SYSTEM] Server has stopped listening for connections.");
		if (backgroundTaskTimer != null) {
			backgroundTaskTimer.shutdown();
		}
	}

	/**
	 * Handles a graceful client disconnection. Removes the client from the tracked
	 * logged-in users and activity maps.
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
	 * Handles an abrupt client disconnection due to an exception (or standard
	 * socket closure). Cleans up tracking maps for the disconnected client and logs
	 * the event gracefully.
	 *
	 * @param client    the disconnected client
	 * @param exception the exception that caused the disconnection
	 */
	@Override
	synchronized protected void clientException(ConnectionToClient client, Throwable exception) {
		logoutUser(client);
		lastActivityMap.remove(client);

		// Pull the saved name from OCSF memory instead of the dead socket!
		String clientIp = (String) client.getInfo("hostName");
		if (clientIp == null) {
			clientIp = "Unknown Client";
		}

		String reason = exception.getMessage();

		// EOFException or null message usually means a standard app closure/disconnect
		if (reason == null || exception instanceof java.io.EOFException || reason.equals("Connection reset")) {
			log("[SYSTEM] Client disconnected: " + clientIp);
		} else {
			// Print the actual error if it was a real crash
			log("[SYSTEM] Client disconnected abruptly (" + clientIp + "): " + reason);
		}
	}

	/**
	 * Logs a timestamped message to the standard console and the server GUI.
	 *
	 * @param msg the message to log
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
	 * Provides a summary of all currently connected clients for display.
	 *
	 * @return a string containing connection details for all active clients.
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
	 * Retrieves the database controller.
	 *
	 * @return the database controller instance
	 */
	public DBController getDatabase() {
		return database;
	}

	/**
	 * Starts a background thread that monitors client activity and disconnects
	 * clients that remain idle for longer than the defined timeout period.
	 */
	private void startIdleChecker() {
		Thread t = new Thread(() -> {
			while (true) {
				try {
					// Check every 5 seconds
					Thread.sleep(5000);
					long now = System.currentTimeMillis();

					for (ConnectionToClient client : lastActivityMap.keySet()) {
						long last = lastActivityMap.get(client);

						long idleTime = now - last;

						// 1. Send warning at 13 minutes (780,000 ms)
						if (idleTime > 780_000 && idleTime < 785_000) {
							client.sendToClient(new Message("IDLE_WARNING", "Warning"));
						}

						// 900,000 milliseconds = 15 minutes
						if (idleTime > 900_000) {
							String clientIp = "Unknown";
							if (client != null && client.getInetAddress() != null) {
								clientIp = client.getInetAddress().getHostAddress();
							}

							log("[IDLE TIMEOUT] Disconnecting client: " + clientIp);

							try {
								client.sendToClient(new Message("FORCE_LOGOUT", "Idle Timeout"));

								Thread.sleep(100);
							} catch (Exception ex) {
								// Ignore if the socket is already completely dead
							}

							logoutUser(client);
							client.close();
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