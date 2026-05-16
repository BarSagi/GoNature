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

	        if (message.getCommand().equals("DISCONNECT")) {

	            log("--------------------");
	            log("CLIENT DISCONNECTED");
	            log("Computer name: " + message.getData());
	            log("IP address: " + client.getInetAddress().getHostAddress());
	            log("Status: DISCONNECTED");
	            log("--------------------");

	            return;
	        }
	        
	        if (message.getCommand().equals("CONNECT")) {


	            log("--------------------");
	            log("CLIENT CONNECTED");
	            log("Computer name: " + message.getData());
	            log("IP address: " + client.getInetAddress().getHostAddress());
	            log("Status: CONNECTED");
	            log("--------------------");

	            return;
	        }

	        log("Message received: " + message.getCommand());

	        MessageStrategy strategy =
	                StrategyFactory.getStrategy(message.getCommand());

	        if (strategy != null) {

	            strategy.execute(message, client, this);

	        } else {

	            log("Unknown command: " + message.getCommand());
	        }

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

	@Override
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
		log("Host name: " + client.getInetAddress().getHostName());
		log("Status: DISCONNECTED");
		log("--------------------");
	}

	public String getConnectedClientInfo() {
	    StringBuilder sb = new StringBuilder();
	    
	    Thread[] clients = getClientConnections();
	    
	    if(clients.length == 0)
	    	return "NO CONNECTED CLIENTS!";
	    	
	    sb.append("Connected clients:\n");

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
	}
	

	public DBController getDatabase() {
		return database;
	}
}