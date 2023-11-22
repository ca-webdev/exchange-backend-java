package ca.webdev.exchange.web.websocket;

import ca.webdev.exchange.listeners.OrderBookListener;
import ca.webdev.exchange.matching.MatchingEngine;
import ca.webdev.exchange.matching.Order;
import ca.webdev.exchange.web.model.OrderBookUpdate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Queue;
import java.util.SortedMap;
import java.util.TreeMap;

@Component
public class OrderBookUpdateWebSocketPublisher implements OrderBookListener {

    @Autowired
    private SimpMessagingTemplate template;

    public OrderBookUpdateWebSocketPublisher(MatchingEngine matchingEngine) {
        System.out.println("order book websocket publisher created");
        matchingEngine.registerOrderBookListener(this);
    }

    @Override
    public void handleOrderBook(SortedMap<Double, Queue<Order>> bidOrderBook, SortedMap<Double, Queue<Order>> askOrderBook) {
        template.convertAndSend("/topic/orderbookupdates", new OrderBookUpdate(sumSizes(bidOrderBook), sumSizes(askOrderBook)));
    }

    private Map<Double, Integer> sumSizes(Map<Double, Queue<Order>> orderBook) {
        SortedMap<Double, Integer> sizeSummedOrderBook = new TreeMap<>();

        orderBook.forEach((priceLevel, orders) -> {
            int sum = orders.stream().mapToInt(Order::getRemainingSize).sum();
            sizeSummedOrderBook.put(priceLevel, sum);
        });

        return sizeSummedOrderBook;
    }
}