package ca.webdev.exchange.web.websocket;

import ca.webdev.exchange.matching.MatchingEngine;
import ca.webdev.exchange.web.model.OrderUpdate;
import ca.webdev.exchange.web.model.RecentTrade;
import ca.webdev.exchange.web.model.UserTrade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
public class Publisher {

    @Autowired
    private SimpMessagingTemplate template;

    public Publisher(MatchingEngine matchingEngine) {
        matchingEngine.registerMarketTradeListener(this::handleMarketTrade);
    }

    public void handleMarketTrade(int tradeId, long tradeTimeInEpochMillis, double price, int size, String buyer, String seller, boolean isTakerSideBuy) {
        template.convertAndSend("/topic/recenttrades", new RecentTrade(tradeTimeInEpochMillis / 1000, price, size, isTakerSideBuy ? "B" : "S"));
    }

    public void publishUserTrade(UserTrade userTrade) {
        template.convertAndSend("/topic/recenttrades", userTrade);
    }

    public void publishOrderState(OrderUpdate orderUpdatePayload) {
        template.convertAndSend("/topic/orderupdates", orderUpdatePayload);
    }
}
