package Common;

import java.io.Serializable;

//this class will handle command types
@SuppressWarnings("serial")
public class Message implements Serializable {

    private String command;
    private Object data;

    public Message(String command, Object data) {

        this.command = command;
        this.data = data;
    }

    public String getCommand() {
        return command;
    }

    public Object getData() {
        return data;
    }
    @Override
    public String toString() {
        return "command=" + command + " data=" + data;
    }
}