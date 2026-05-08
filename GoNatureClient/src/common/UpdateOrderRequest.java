package common;

import java.io.Serializable;

public class UpdateOrderRequest implements Serializable {
    private static final long serialVersionUID = 1L;

    private int orderNumber;
    private String orderDate;
    private int numberOfVisitors;

    public UpdateOrderRequest(int orderNumber, String orderDate, int numberOfVisitors) {
        this.orderNumber = orderNumber;
        this.orderDate = orderDate;
        this.numberOfVisitors = numberOfVisitors;
    }

    public int getOrderNumber() {
        return orderNumber;
    }

    public String getOrderDate() {
        return orderDate;
    }

    public int getNumberOfVisitors() {
        return numberOfVisitors;
    }

    @Override
    public String toString() {
        return "UpdateOrderRequest{" +
                "orderNumber=" + orderNumber +
                ", orderDate='" + orderDate + '\'' +
                ", numberOfVisitors=" + numberOfVisitors +
                '}';
    }
}