package ca.webdev.exchange.web.model;

public class OrderBlockingInfo {
    private double currentBalance;
    private double freezingBalance;
    private long currentPosition;
    private long freezingPosition;

    public OrderBlockingInfo() {
    }

    public OrderBlockingInfo(double currentBalance, double freezingBalance, long currentPosition, long freezingPosition) {
        this.currentBalance = currentBalance;
        this.freezingBalance = freezingBalance;
        this.currentPosition = currentPosition;
        this.freezingPosition = freezingPosition;
    }

    public double getCurrentBalance() {
        return currentBalance;
    }

    public void setCurrentBalance(double currentBalance) {
        this.currentBalance = currentBalance;
    }

    public double getFreezingBalance() {
        return freezingBalance;
    }

    public void setFreezingBalance(double freezingBalance) {
        this.freezingBalance = freezingBalance;
    }

    public long getCurrentPosition() {
        return currentPosition;
    }

    public void setCurrentPosition(long currentPosition) {
        this.currentPosition = currentPosition;
    }

    public long getFreezingPosition() {
        return freezingPosition;
    }

    public void setFreezingPosition(long freezingPosition) {
        this.freezingPosition = freezingPosition;
    }
}
