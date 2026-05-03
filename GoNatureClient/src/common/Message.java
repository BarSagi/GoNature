package common;

import java.io.Serializable;

public class Message implements Serializable {
    private static final long serialVersionUID = 1L;

    private MessageType type;
    private Object data;

    public Message(MessageType type, Object data) {
        this.type = type;
        this.data = data;
    }

    public MessageType getType() {
        return type;
    }

    public Object getData() {
        return data;
    }

    public void setType(MessageType type) {
        this.type = type;
    }

    public void setData(Object data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "Message{type=" + type + ", data=" + data + "}";
    }
}