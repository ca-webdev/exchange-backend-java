package ca.webdev.exchange.web.websocket;

import ca.webdev.exchange.web.model.RecentTrade;
import ca.webdev.exchange.listeners.MarketTradeListener;
import ca.webdev.exchange.matching.MatchingEngine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
public class RecentTradeWebSocketPublisher implements MarketTradeListener {

    @Autowired
    private SimpMessagingTemplate template;

    public RecentTradeWebSocketPublisher(MatchingEngine matchingEngine) {
        matchingEngine.registerMarketTradeListener(this);
    }

    @Override
    public void handleMarketTrade(int tradeId, long tradeTimeInMillisecondEpoch, double price, int size, String buyer, String seller, boolean isTakerSideBuy) {
        template.convertAndSend("/topic/recenttrades", new RecentTrade(tradeTimeInMillisecondEpoch, price, size, isTakerSideBuy ? "B" : "S"));
    }
}
