package ca.webdev.exchange.web.model;

public class PositionPnL {
    private long position;
    private double averageEntryPrice;
    private double marketPrice;
    private double unrealizedPnL;
    private double realizedPnL;
    private double totalPnL;
    private double initialBalance;
    private double portfolioValue;
    private double portfolioValueChange;

    public PositionPnL() {
    }

    public PositionPnL(long position, double averageEntryPrice, double marketPrice, double unrealizedPnL, double realizedPnL, double totalPnL, double initialBalance, double portfolioValue, double portfolioValueChange) {
        this.position = position;
        this.averageEntryPrice = averageEntryPrice;
        this.marketPrice = marketPrice;
        this.unrealizedPnL = unrealizedPnL;
        this.realizedPnL = realizedPnL;
        this.totalPnL = totalPnL;
        this.initialBalance = initialBalance;
        this.portfolioValue = portfolioValue;
        this.portfolioValueChange = portfolioValueChange;
    }

    public long getPosition() {
        return position;
    }

    public void setPosition(long position) {
        this.position = position;
    }

    public double getAverageEntryPrice() {
        return averageEntryPrice;
    }

    public void setAverageEntryPrice(double averageEntryPrice) {
        this.averageEntryPrice = averageEntryPrice;
    }

    public double getMarketPrice() {
        return marketPrice;
    }

    public void setMarketPrice(double marketPrice) {
        this.marketPrice = marketPrice;
    }

    public double getUnrealizedPnL() {
        return unrealizedPnL;
    }

    public void setUnrealizedPnL(double unrealizedPnL) {
        this.unrealizedPnL = unrealizedPnL;
    }

    public double getRealizedPnL() {
        return realizedPnL;
    }

    public void setRealizedPnL(double realizedPnL) {
        this.realizedPnL = realizedPnL;
    }

    public double getTotalPnL() {
        return totalPnL;
    }

    public void setTotalPnL(double totalPnL) {
        this.totalPnL = totalPnL;
    }

    public double getInitialBalance() {
        return initialBalance;
    }

    public void setInitialBalance(double initialBalance) {
        this.initialBalance = initialBalance;
    }

    public double getPortfolioValue() {
        return portfolioValue;
    }

    public void setPortfolioValue(double portfolioValue) {
        this.portfolioValue = portfolioValue;
    }

    public double getPortfolioValueChange() {
        return portfolioValueChange;
    }

    public void setPortfolioValueChange(double portfolioValueChange) {
        this.portfolioValueChange = portfolioValueChange;
    }
}
