package ca.webdev.exchange.websocket;

import ca.webdev.exchange.websocket.model.RecentTrade;
import ca.webdev.exchange.listeners.MarketTradeListener;
import ca.webdev.exchange.matching.MatchingEngine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalTime;

@Component
public class RecentTradeWebSocketPublisher implements MarketTradeListener {

    @Autowired
    private SimpMessagingTemplate template;

    public RecentTradeWebSocketPublisher(MatchingEngine matchingEngine) {
        matchingEngine.registerMarketTradeListener(this);
    }

    @Override
    public void handleMarketTrade(int tradeId, LocalTime tradeTime, double price, int size, String buyer, String seller, boolean isTakerSideBuy) {
        template.convertAndSend("/topic/recenttrades", new RecentTrade(tradeTime, price, size, isTakerSideBuy ? "B" : "S"));
    }
}
