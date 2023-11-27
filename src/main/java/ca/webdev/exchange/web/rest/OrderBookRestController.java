package ca.webdev.exchange.web.rest;

import ca.webdev.exchange.matching.MatchingEngine;
import ca.webdev.exchange.matching.Order;
import ca.webdev.exchange.web.model.OrderBookUpdate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.Comparator;
import java.util.Map;
import java.util.Queue;
import java.util.SortedMap;
import java.util.TreeMap;

import static ca.webdev.exchange.web.OrderBookUtil.sumSizes;

@CrossOrigin
@RestController
public class OrderBookRestController {

    private final SortedMap<Double, Queue<Order>> bidOrderBook;
    private final SortedMap<Double, Queue<Order>> askOrderBook;

    @Autowired
    private MatchingEngine matchingEngine;

    public OrderBookRestController(MatchingEngine matchingEngine) {
        this.bidOrderBook = matchingEngine.getReadOnlyBidOrderBook();
        this.askOrderBook = matchingEngine.getReadOnlyAskOrderBook();
    }

    @GetMapping(value = "/orderbook")
    public OrderBookUpdate getOrderBook() {
        return new OrderBookUpdate(sumSizes(new TreeMap<>(Collections.reverseOrder()), bidOrderBook), sumSizes(new TreeMap<>(), askOrderBook));
    }

    @GetMapping(value = "/bidorderbook")
    public Map<Double, Integer> getBidOrderBook() {
        return sumSizes(new TreeMap<>(Comparator.reverseOrder()), bidOrderBook);
    }

    @GetMapping(value = "/askorderbook")
    public Map<Double, Integer> getAskOrderBook() {
        return sumSizes(new TreeMap<>(), askOrderBook);
    }



}
