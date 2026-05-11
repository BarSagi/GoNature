package client;

import ocsf.client.AbstractClient;

/*Handle the client-side connection to the server using OCSF*/

public class ClientConsole extends AbstractClient {
	
    public ClientConsole(String host, int port) {
        super(host, port);
    }

	@Override
	protected void handleMessageFromServer(Object msg) {
		System.out.println("Message received from server: " + msg);
		
	}
	
	@Override
	protected void connectionEstablished() {
		System.out.println("Connected to server.");
	}

	@Override
	protected void connectionClosed() {
		System.out.println("Connection closed.");
	}

	@Override
	protected void connectionException(Exception exception) {
		System.out.println("Connection error: " + exception.getMessage());
	}
}