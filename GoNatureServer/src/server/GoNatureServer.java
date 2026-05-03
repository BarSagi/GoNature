package server;

import ocsf.server.AbstractServer;
import ocsf.server.ConnectionToClient;

public class GoNatureServer extends AbstractServer {

    public GoNatureServer(int port) {
        super(port);
    }

	@Override
	protected void handleMessageFromClient(Object msg, ConnectionToClient client) {
		// TODO Auto-generated method stub
		
	}
    
    
}