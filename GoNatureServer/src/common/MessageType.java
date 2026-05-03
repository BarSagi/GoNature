package common;

import java.io.Serializable;

public enum MessageType implements Serializable {
    GET_ORDER,
    UPDATE_ORDER,
    ORDER_RESULT,
    UPDATE_RESULT,
    ERROR
}