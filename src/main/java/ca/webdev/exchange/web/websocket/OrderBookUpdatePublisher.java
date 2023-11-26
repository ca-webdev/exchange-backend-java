package ca.webdev.exchange.web.websocket;

import ca.webdev.exchange.matching.MatchingEngine;
import ca.webdev.exchange.matching.Order;
import ca.webdev.exchange.web.model.OrderBookUpdate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.Queue;
import java.util.SortedMap;
import java.util.TreeMap;

import static ca.webdev.exchange.web.OrderBookUtil.sumSizes;

@Component
public class OrderBookUpdatePublisher {

    @Autowired
    private SimpMessagingTemplate template;

    public OrderBookUpdatePublisher(MatchingEngine matchingEngine) {
        System.out.println("order book websocket publisher created");
        matchingEngine.registerOrderBookListener(this::handleOrderBook);
    }

    public void handleOrderBook(SortedMap<Double, Queue<Order>> bidOrderBook, SortedMap<Double, Queue<Order>> askOrderBook) {
        template.convertAndSend("/topic/orderbookupdates", new OrderBookUpdate(sumSizes(new TreeMap<>(Comparator.reverseOrder()), bidOrderBook), sumSizes(new TreeMap<>(), askOrderBook)));
        template.convertAndSend("/topic/bidorderbook", sumSizes(new TreeMap<>(Comparator.reverseOrder()), bidOrderBook));
        template.convertAndSend("/topic/askorderbook", sumSizes(new TreeMap<>(), askOrderBook));
    }

}