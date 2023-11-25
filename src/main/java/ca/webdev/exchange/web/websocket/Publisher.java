package ca.webdev.exchange.web.websocket;

import ca.webdev.exchange.matching.MatchingEngine;
import ca.webdev.exchange.matching.OrderStatus;
import ca.webdev.exchange.web.model.OrderUpdate;
import ca.webdev.exchange.web.model.RecentTrade;
import ca.webdev.exchange.web.model.UserTrade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.util.UUID;

import static ca.webdev.exchange.web.Constants.WEB_USER;

@Component
public class Publisher {

    @Autowired
    private SimpMessagingTemplate template;

    public Publisher(MatchingEngine matchingEngine) {
        matchingEngine.registerMarketTradeListener(this::handleMarketTrade);
        matchingEngine.registerTradeListener(WEB_USER, this::handleTrade);
        matchingEngine.registerOrderStateListener(WEB_USER, this::handleOrderState);
    }

    public void handleMarketTrade(int tradeId, long tradeTimeInEpochMillis, double price, int size, String buyer, String seller, boolean isTakerSideBuy) {
        template.convertAndSend("/topic/recenttrades", new RecentTrade(tradeTimeInEpochMillis / 1000, price, size, isTakerSideBuy ? "B" : "S"));
    }

    public void handleTrade(long tradeTradeInEpochMillis, boolean isBuyOrder, double price, int size, String buyer, String seller) {
        template.convertAndSend("/topic/usertrades", new UserTrade(tradeTradeInEpochMillis / 1000, isBuyOrder ? "buy" : "sell", price, size));
    }

    public void handleOrderState(UUID orderId, long orderStateTimeInMillis, boolean isBuyOrder, double price, int size, double filledPrice, int filledSize, OrderStatus orderStatus) {
        template.convertAndSend("/topic/orderupdates", new OrderUpdate(orderId.toString(), orderStateTimeInMillis / 1000, isBuyOrder ? "buy" : "sell", price, size, filledPrice, filledSize, orderStatus.name()));
    }
}
