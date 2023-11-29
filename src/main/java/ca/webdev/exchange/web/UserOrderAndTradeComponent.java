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
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import static ca.webdev.exchange.web.Constants.WEB_USER;

@Component
public class UserOrderAndTradeComponent {

    public static final double INITIAL_BALANCE = 100;

    private final Map<UUID, List<OrderUpdate>> cachedOrderUpdates = new ConcurrentHashMap<>();

    private final Publisher publisher;

    public UserOrderAndTradeComponent(MatchingEngine matchingEngine, Publisher publisher) {
        this.publisher = publisher;
        matchingEngine.registerTradeListener(WEB_USER, this::handleTrade);
        matchingEngine.registerOrderStateListener(WEB_USER, this::handleOrderState);
    }

    public void handleTrade(long tradeTradeInEpochMillis, boolean isBuyOrder, double price, int size, String buyer, String seller) {
        UserTrade userTrade = new UserTrade(tradeTradeInEpochMillis / 1000, isBuyOrder ? "buy" : "sell", price, size);
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
}
