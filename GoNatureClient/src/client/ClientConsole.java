package client;

import ocsf.client.AbstractClient;

public class ClientConsole extends AbstractClient {

    public ClientConsole(String host, int port) {
        super(host, port);
    }

	@Override
	protected void handleMessageFromServer(Object msg) {
		// TODO Auto-generated method stub
		
	}
}