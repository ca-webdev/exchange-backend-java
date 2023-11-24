package ca.webdev.exchange.web.model;

public class OrderInsertResponse {
    private String orderId;

    public OrderInsertResponse() {
    }

    public OrderInsertResponse(String orderId) {
        this.orderId = orderId;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }
}
