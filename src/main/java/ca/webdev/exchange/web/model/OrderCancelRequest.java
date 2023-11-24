package ca.webdev.exchange.web.model;

public class OrderCancelRequest {
    private String orderId;

    public OrderCancelRequest() {
    }

    public OrderCancelRequest(String orderId) {
        this.orderId = orderId;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }
}
