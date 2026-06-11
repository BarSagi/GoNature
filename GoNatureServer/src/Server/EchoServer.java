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

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class EchoServer extends AbstractServer {

	private DBController database;

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

			if (message.getCommand().equals("DISCONNECT")) {

				String compName = (String) client.getInfo("hostName");
				if (compName == null)
					compName = "Unknown";

				log("[CLIENT DISCONNECTED] Host: " + compName + " | IP: " + client.getInetAddress().getHostAddress());
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
		database = new DBController(this);
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
}