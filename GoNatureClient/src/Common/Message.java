package Common;

import java.io.Serializable;

/**
 * A generic message wrapper used for communication between the client and the server.
 * This class encapsulates a command string and a corresponding data object, 
 * allowing for flexible data exchange across the network.
 */
@SuppressWarnings("serial")
public class Message implements Serializable {


    /**
     * The command string indicating the action to be performed (e.g., "GET_ORDERS").
     */
    private String command;
    
    /**
     * The data associated with the command (e.g., an ArrayList or a specific entity object).
     */
    private Object data;

    /**
     * Constructs a new Message instance.
     *
     * @param command The command string to be executed.
     * @param data    The data to be processed with the command.
     */
    public Message(String command, Object data) {
        this.command = command;
        this.data = data;
    }

    /**
     * Retrieves the command string.
     *
     * @return The command string.
     */
    public String getCommand() {
        return command;
    }

    /**
     * Retrieves the data associated with the command.
     *
     * @return The data object.
     */
    public Object getData() {
        return data;
    }

    /**
     * Returns a string representation of the message, detailing the command and its data.
     *
     * @return A string containing the command and data.
     */
    @Override
    public String toString() {
        return "command=" + command + " data=" + data;
    }
}