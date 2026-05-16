package Server;

import Common.Message;
import Database.DBController;
import GUI.ServerPortFrameController;
import OCSFUtils.AbstractServer;
import OCSFUtils.ConnectionToClient;
import Strategy.MessageStrategy;
import Strategy.StrategyFactory;
import javafx.application.Platform;

public class EchoServer extends AbstractServer {

	private DBController database;

	public EchoServer(int port) {
		super(port);
	}

	@Override
	public void handleMessageFromClient(Object msg, ConnectionToClient client) {

		if (database == null) {
			log("DB not initialized yet!");
			return;
		}

		try {

			Message message = (Message) msg;
<<<<<<< HEAD

			if (message.getCommand().equals("DISCONNECT")) {

				String compName = (String) client.getInfo("hostName");
				if (compName == null)
					compName = "Unknown";

				log("--------------------");
				log("CLIENT DISCONNECTED");
				log("Host name: " + compName);
				log("IP address: " + client.getInetAddress().getHostAddress());
				log("Status: DISCONNECTED");
				log("--------------------");

				return;
			}

			else if (message.getCommand().equals("CONNECT")) {

				client.setInfo("hostName", message.getData());

				log("--------------------");
				log("CLIENT CONNECTED");
				log("Host name: " + message.getData());
				log("IP address: " + client.getInetAddress().getHostAddress());
				log("Status: CONNECTED");
				log("--------------------");

				return;
			}

			log("Message received: " + message.getCommand());

			MessageStrategy strategy = StrategyFactory.getStrategy(message.getCommand());

			if (strategy != null) {

				strategy.execute(message, client, this);

			} else {

				log("Unknown command: " + message.getCommand());
			}

=======
			log("Message received: " + message.getCommand());

			MessageStrategy strategy = StrategyFactory.getStrategy(message.getCommand());

			if (strategy != null) {

				strategy.execute(message, client, this);

			} else {

				log("Unknown command: " + message.getCommand());
			}

>>>>>>> 4bba51f1bf110a2c47e64329925ab7f56ec490e4
		} catch (Exception e) {

			e.printStackTrace();

			log("Error while handling client message");
		}
	}

	protected void serverStarted() {
		log("Server listening for connections on port " + getPort());
		database = new DBController(this);
	}

	protected void serverStopped() {
		log("Server has stopped listening for connections.");
	}

	// this method will handle prints in side the GUI
	public void log(String msg) {
		System.out.println(msg);

		if (ServerPortFrameController.instance != null) {
			Platform.runLater(new Runnable() {
				@Override
				public void run() {
					ServerPortFrameController.instance.log(msg);
				}
			});
		}
	}

<<<<<<< HEAD
	/*@Override we tried using this but it works without it, therefore we put it as a comment.
=======
	@Override
>>>>>>> 4bba51f1bf110a2c47e64329925ab7f56ec490e4
	protected void clientConnected(ConnectionToClient client) {

		log("--------------------");
		log("CLIENT CONNECTED");
		log("IP address: " + client.getInetAddress().getHostAddress());
		log("Host name: " + client.getInetAddress().getHostName());
		log("Status: CONNECTED");
		log("--------------------");
	}

	@Override
	protected void clientDisconnected(ConnectionToClient client) {
		log("--------------------");
		log("CLIENT DISCONNECTED");
		log("IP address: " + client.getInetAddress().getHostAddress());
<<<<<<< HEAD
		log("Host name: " + client.getInetAddress().getHostName());
		log("Status: DISCONNECTED");
		log("--------------------");
	}*/

	public String getConnectedClientInfo() {
		StringBuilder sb = new StringBuilder();

		Thread[] clients = getClientConnections();

		if (clients.length == 0)
			return "NO CONNECTED CLIENTS!\n";

		sb.append("Connected clients:\n");

		for (Thread t : clients) {
			ConnectionToClient client = (ConnectionToClient) t;

			// Fetch the host name we saved during CONNECT
			String compName = (String) client.getInfo("hostName");
			if (compName == null)
				compName = "Unknown";

			sb.append("--------------------\n");

			sb.append("Host name: ").append(compName).append("\n");

			sb.append("IP address: ").append(client.getInetAddress().getHostAddress()).append("\n");

			sb.append("Status: CONNECTED\n");

			sb.append("--------------------\n");
		}

		return sb.toString();
=======
		log("Status: DISCONNECTED");
		log("--------------------");
	}

	public String getConnectedClientInfo() {
	    StringBuilder sb = new StringBuilder();

	    sb.append("Connected clients:\n");

	    Thread[] clients = getClientConnections();

	    for (Thread t : clients) {

	        ConnectionToClient client = (ConnectionToClient) t;

	        sb.append("--------------------\n");
	        sb.append("IP address: ")
	          .append(client.getInetAddress().getHostAddress())
	          .append("\n");

	        sb.append("Host name: ")
	          .append(client.getInetAddress().getHostName())
	          .append("\n");

	        sb.append("Status: CONNECTED\n");
	        sb.append("--------------------\n");
	    }

	    return sb.toString();
>>>>>>> 4bba51f1bf110a2c47e64329925ab7f56ec490e4
	}

	public DBController getDatabase() {
		return database;
	}
}