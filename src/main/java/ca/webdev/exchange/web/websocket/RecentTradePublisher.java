package ca.webdev.exchange.web.websocket;

import ca.webdev.exchange.matching.MatchingEngine;
import ca.webdev.exchange.web.model.RecentTrade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
public class RecentTradePublisher {

    @Autowired
    private SimpMessagingTemplate template;

    public RecentTradePublisher(MatchingEngine matchingEngine) {
        matchingEngine.registerMarketTradeListener(this::handleMarketTrade);
    }

    public void handleMarketTrade(int tradeId, long tradeTimeInMillisecondEpoch, double price, int size, String buyer, String seller, boolean isTakerSideBuy) {
        template.convertAndSend("/topic/recenttrades", new RecentTrade(tradeTimeInMillisecondEpoch / 1000, price, size, isTakerSideBuy ? "B" : "S"));
    }
}
