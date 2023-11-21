package ca.webdev.exchange.web.model;

public class OpenHighLowClose {
    private long time;
    private double open;
    private double high;
    private double low;
    private double close;

    public OpenHighLowClose() {
    }

    public OpenHighLowClose(long time, double open, double high, double low, double close) {
        this.time = time;
        this.open = open;
        this.high = high;
        this.low = low;
        this.close = close;
    }

    public long getTime() {
        return time;
    }

    public double getOpen() {
        return open;
    }

    public double getHigh() {
        return high;
    }

    public double getLow() {
        return low;
    }

    public double getClose() {
        return close;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public void setOpen(double open) {
        this.open = open;
    }

    public void setHigh(double high) {
        this.high = high;
    }

    public void setLow(double low) {
        this.low = low;
    }

    public void setClose(double close) {
        this.close = close;
    }
}
