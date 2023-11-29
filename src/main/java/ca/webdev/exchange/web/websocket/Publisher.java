package ca.webdev.exchange.web.websocket;

import ca.webdev.exchange.matching.MatchingEngine;
import ca.webdev.exchange.matching.Order;
import ca.webdev.exchange.web.model.OpenHighLowClose;
import ca.webdev.exchange.web.model.OrderBookUpdate;
import ca.webdev.exchange.web.model.OrderUpdate;
import ca.webdev.exchange.web.model.RecentTrade;
import ca.webdev.exchange.web.model.UserTrade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.Queue;
import java.util.SortedMap;
import java.util.TreeMap;

import static ca.webdev.exchange.web.OrderBookUtil.sumSizes;

@Component
public class Publisher {

    @Autowired
    private SimpMessagingTemplate template;

    public Publisher(MatchingEngine matchingEngine) {
        matchingEngine.registerOrderBookListener(this::handleOrderBook);
        matchingEngine.registerMarketTradeListener(this::handleMarketTrade);
    }

    public void handleOrderBook(SortedMap<Double, Queue<Order>> bidOrderBook, SortedMap<Double, Queue<Order>> askOrderBook) {
        template.convertAndSend("/topic/orderbookupdates", new OrderBookUpdate(sumSizes(new TreeMap<>(Comparator.reverseOrder()), bidOrderBook), sumSizes(new TreeMap<>(), askOrderBook)));
        template.convertAndSend("/topic/bidorderbook", sumSizes(new TreeMap<>(Comparator.reverseOrder()), bidOrderBook));
        template.convertAndSend("/topic/askorderbook", sumSizes(new TreeMap<>(), askOrderBook));
    }

    public void handleMarketTrade(int tradeId, long tradeTimeInEpochMillis, double price, int size, String buyer, String seller, boolean isTakerSideBuy) {
        template.convertAndSend("/topic/recenttrades", new RecentTrade(tradeTimeInEpochMillis / 1000, price, size, isTakerSideBuy ? "B" : "S"));
    }

    public void publish(OpenHighLowClose ohlc) {
        template.convertAndSend("/topic/ohlc", ohlc);
    }

    public void publishUserTrade(UserTrade userTrade) {
        template.convertAndSend("/topic/usertrades", userTrade);
    }

    public void publishOrderState(OrderUpdate orderUpdatePayload) {
        template.convertAndSend("/topic/orderupdates", orderUpdatePayload);
    }
}
