package Strategy;

import Common.Message;
import OCSFUtils.ConnectionToClient;
import Server.EchoServer;

public interface MessageStrategy {

	void execute(Message message, ConnectionToClient client, EchoServer server) throws Exception;
}