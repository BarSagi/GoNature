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
	
	public DBController getDatabase() {
	    return database;
	}
}