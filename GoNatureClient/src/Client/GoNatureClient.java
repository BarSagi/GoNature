package Client;


import Common.Message;
import javafx.application.Platform;
import OCSFUtils.AbstractClient;
import Strategy.*;

public class GoNatureClient extends AbstractClient {

	public static boolean awaitResponse = false;

	public GoNatureClient(String host, int port) {
		super(host, port);
	}

	@Override
	protected void handleMessageFromServer(Object msg) {
		if (msg instanceof Message) {
			Message message = (Message) msg;
			String command = message.getCommand();

			// 1. Ask the factory for the appropriate strategy
			MessageStrategy strategy = StrategyFactory.getStrategy(command);

			// 2. Execute it if it exists
			if (strategy != null) {
				strategy.execute(message);
			} else {
				System.out.println("Client: Received an unknown command from server: " + command);
			}
		}
	}

	// this method will show us if the connection is interupted
	@Override
	protected void connectionException(Exception exception) {

		Platform.runLater(new Runnable() {

			@Override
			public void run() {

				if (ClientUI.connectionController != null) {
					ClientUI.connectionController.showErrorInGUI("Connection failed");
				}
			}
		});
	}

	@Override
	protected void connectionEstablished() {
		try {
			String pcName = java.net.InetAddress.getLocalHost().getHostName(); // get the host name

			sendToServer(new Message("CONNECT", pcName));

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void disconnectClient() {
		try {
			if (isConnected()) {
				closeConnection();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}