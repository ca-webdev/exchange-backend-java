package ca.webdev.exchange.web;

import ca.webdev.exchange.matching.MatchingEngine;
import ca.webdev.exchange.matching.OrderStatus;
import ca.webdev.exchange.web.model.OrderUpdate;
import ca.webdev.exchange.web.model.UserTrade;
import ca.webdev.exchange.web.websocket.Publisher;
import org.springframework.stereotype.Component;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

import static ca.webdev.exchange.web.Constants.WEB_USER;

@Component
public class UserOrderAndTradeComponent {

    public static final double INITIAL_BALANCE = 100;

    private final Map<UUID, List<OrderUpdate>> cachedOrderUpdates = new ConcurrentHashMap<>();

    private final Publisher publisher;

    private final AtomicBoolean isCheckBalanceOnOrderInsert = new AtomicBoolean(true);

    private final AtomicReference<Double> currentBalance = new AtomicReference<>(INITIAL_BALANCE);
    private final AtomicReference<Double> freezingBalance = new AtomicReference<>(0d);
    private final AtomicLong currentPosition = new AtomicLong();
    private final AtomicLong freezingPosition = new AtomicLong();

    public UserOrderAndTradeComponent(MatchingEngine matchingEngine, Publisher publisher) {
        this.publisher = publisher;
        matchingEngine.registerTradeListener(WEB_USER, this::handleTrade);
        matchingEngine.registerOrderStateListener(WEB_USER, this::handleOrderState);
    }

    public void handleTrade(long tradeTradeInEpochMillis, boolean isBuyTrade, double price, int size, String buyer, String seller) {
        UserTrade userTrade = new UserTrade(tradeTradeInEpochMillis / 1000, isBuyTrade ? "buy" : "sell", price, size);

        if (isCheckBalanceOnOrderInsert.get()) {
            if (isBuyTrade) {
                // if buy trade, reduce the freezing balance, reduce the current balance, increase the position
                double tradeValue = price * size;
                freezingBalance.updateAndGet(v -> v - tradeValue);
                currentBalance.updateAndGet(v -> v - tradeValue);
                currentPosition.updateAndGet(v -> v + size);
            } else {
                // if sell trade, reduce the freezing position, reduce the position, increase the balance. In trade, size will be negative for the sell trade
                double tradeValue = price * -1 * size;
                freezingPosition.updateAndGet(v -> v + size);
                currentPosition.updateAndGet(v -> v + size);
                currentBalance.updateAndGet(v -> v + tradeValue);
            }
        }

        publisher.publishUserTrade(userTrade);
    }

    public void handleOrderState(UUID orderId, long orderStateTimeInMillis, boolean isBuyOrder, double price, int size, double filledPrice, int filledSize, OrderStatus orderStatus) {
        OrderUpdate orderUpdatePayload = new OrderUpdate(orderId.toString(), orderStateTimeInMillis / 1000, isBuyOrder ? "buy" : "sell", price, size, filledPrice, filledSize, orderStatus.name());
        cachedOrderUpdates.computeIfAbsent(orderId, k -> new LinkedList<>()).add(orderUpdatePayload);
        publisher.publishOrderState(orderUpdatePayload);

    }

    public Map<UUID, List<OrderUpdate>> getCachedOrderUpdates() {
        return cachedOrderUpdates;
    }

    public boolean isCheckBalanceOnOrderInsert() {
        return isCheckBalanceOnOrderInsert.get();
    }

    public void setCheckBalanceOnOrderInsert(boolean isCheckBalanceOnOrderInsert) {
        this.isCheckBalanceOnOrderInsert.set(isCheckBalanceOnOrderInsert);
    }

    public Optional<String> validateBuyOrder(double price, int size) {
        if (price * size > currentBalance.get() + freezingBalance.get()) {
            return Optional.of("Buy order rejected due to order value (price * size) is larger than the available balance.");
        }

        return Optional.empty();
    }

    public void freezeBalance(double price, int size) {
        freezingBalance.updateAndGet(b -> b + price * size);
    }

    public void unfreezeBalance(double balance) {
        freezingBalance.updateAndGet(b -> b - balance);
    }

    public Optional<String> validateSellOrder(int size) {
        if (size > currentPosition.get() + freezingPosition.get()) {
            return Optional.of("Sell order rejected due to the order size is larger than the available position.");
        }

        return Optional.empty();
    }

    public void freezePosition(int size) {
        freezingPosition.updateAndGet(p -> p + size);
    }

    public void unfreezePosition(int remainingSize) {
        freezingPosition.updateAndGet(p -> p - remainingSize);
    }

    public double getCurrentBalance() {
        return currentBalance.get();
    }

    public double getFreezingBalance() {
        return freezingBalance.get();
    }

    public long getCurrentPosition() {
        return currentPosition.get();
    }

    public long getFreezingPosition() {
        return freezingPosition.get();
    }
}
