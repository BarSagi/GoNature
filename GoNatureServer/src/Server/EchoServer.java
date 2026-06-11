package Server;

import Common.Message;
import Database.DBController;
// FIXED: Imported the new Console Controller instead of the Port Controller
import GUI.ServerConsoleController;
import OCSFUtils.AbstractServer;
import OCSFUtils.ConnectionToClient;
import Strategy.MessageStrategy;
import Strategy.StrategyFactory;
import javafx.application.Platform;
import Reports.ReportService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class EchoServer extends AbstractServer {

	private DBController database;
	private ReportService reportService;
	private final Map<ConnectionToClient, Long> lastActivityMap = new ConcurrentHashMap<>();

	private static final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

	public EchoServer(int port) {
		super(port);
	}

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

	protected void serverStarted() {
		log("[SYSTEM] Server listening for connections on port " + getPort());
		startIdleChecker();
		database = new DBController(this);
		reportService = new ReportService(database);
	}

	protected void serverStopped() {
		log("[SYSTEM] Server has stopped listening for connections.");
	}

	// this method will handle prints inside the GUI and Console with Timestamps
	public void log(String msg) {

		String timeStampedMsg = "[" + dtf.format(LocalDateTime.now()) + "] " + msg;

		System.out.println(timeStampedMsg);

		// FIXED: Pointed the GUI logging to the new ServerConsoleController
		if (ServerConsoleController.instance != null) {
			Platform.runLater(new Runnable() {
				@Override
				public void run() {
					ServerConsoleController.instance.log(timeStampedMsg);
				}
			});
		}
	}

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

	public DBController getDatabase() {
		return database;
	}
	
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