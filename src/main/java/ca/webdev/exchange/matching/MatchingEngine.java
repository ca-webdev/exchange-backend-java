package ca.webdev.exchange.matching;

import ca.webdev.exchange.listeners.MarketTradeListener;
import ca.webdev.exchange.listeners.OrderBookListener;
import ca.webdev.exchange.listeners.TradeListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.SortedMap;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MatchingEngine {

    private static final Logger LOGGER = LoggerFactory.getLogger(MatchingEngine.class);
    private final double tickSize;
    private final int tickSizeInPrecision;

    private final SortedMap<Double, Queue<Order>> bidOrderBook = new ConcurrentSkipListMap<>(Collections.reverseOrder());
    private final SortedMap<Double, Queue<Order>> askOrderBook = new ConcurrentSkipListMap<>();
    private final SortedMap<Double, Queue<Order>> readOnlyBidOrderBook = Collections.unmodifiableSortedMap(bidOrderBook);
    private final SortedMap<Double, Queue<Order>> readOnlyAskOrderBook = Collections.unmodifiableSortedMap(askOrderBook);
    private final Map<UUID, Double> orderIdToPriceLevelMap = new HashMap<>();

    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    private final List<OrderBookListener> orderBookListeners = new ArrayList<>();
    private final Map<String, TradeListener> tradeListeners = new HashMap<>();
    private final List<MarketTradeListener> marketTradeListeners = new ArrayList<>();

    private int incrementingTradeId = 1;

    public MatchingEngine(double tickSize, int tickSizeInPrecision) {
        this.tickSize = tickSize;
        this.tickSizeInPrecision = tickSizeInPrecision;
    }

    public double getTickSize() {
        return tickSize;
    }

    public int getTickSizeInPrecision() {
        return tickSizeInPrecision;
    }

    public void registerOrderBookListener(OrderBookListener orderBookListener) {
        orderBookListeners.add(orderBookListener);
    }

    public void registerTradeListener(String agentName, TradeListener listener) {
        tradeListeners.put(agentName, listener);
    }

    public void registerMarketTradeListener(MarketTradeListener marketTradeListener) {
        marketTradeListeners.add(marketTradeListener);
    }

    public UUID insertBuyLimitOrder(String owner, double price, int size) {
        UUID orderId = UUID.randomUUID();
        executor.execute(() -> handlerLimitOrder(true, owner, price, size, orderId, askOrderBook, bidOrderBook));
        return orderId;
    }

    public UUID insertSellLimitOrder(String owner, double price, int size) {
        UUID orderId = UUID.randomUUID();
        executor.execute(() -> handlerLimitOrder(false, owner, price, size, orderId, bidOrderBook, askOrderBook));
        return orderId;
    }

    private void handlerLimitOrder(boolean isBuyOrder, String owner, double price, int size, UUID orderId, SortedMap<Double, Queue<Order>> matchingOrderBook, SortedMap<Double, Queue<Order>> orderBook) {
        int remainingSize = match(isBuyOrder, owner, price, size, matchingOrderBook);

        // add remaining sizes to the bidQueue
        if (remainingSize == 0) {
            publishOrderBook();
            return;
        }
        orderBook.computeIfAbsent(price, k -> new ConcurrentLinkedQueue<>()).add(new Order(orderId, owner, remainingSize));
        orderIdToPriceLevelMap.put(orderId, price);
        LOGGER.info((isBuyOrder ? "bid" : "ask") + "OrderBook={}", orderBook);
        publishOrderBook();
    }

    public void cancelOrder(UUID orderId) {
        executor.execute(() -> {
            if (!orderIdToPriceLevelMap.containsKey(orderId)) {
                LOGGER.warn("invalid order cancel with orderId={}", orderId);
                return;
            }
            double priceLevel = orderIdToPriceLevelMap.get(orderId);
            if (priceLevel > bidOrderBook.firstKey()) {
                askOrderBook.get(priceLevel).removeIf(o -> orderId.equals(o.getOrderId()));
                if (askOrderBook.get(priceLevel).isEmpty()) {
                    askOrderBook.remove(priceLevel);
                }
            } else {
                bidOrderBook.get(priceLevel).removeIf(o -> orderId.equals(o.getOrderId()));
                if (bidOrderBook.get(priceLevel).isEmpty()) {
                    bidOrderBook.remove(priceLevel);
                }
            }
            orderIdToPriceLevelMap.remove(orderId);
            publishOrderBook();
        });
    }

    private int match(boolean isBuyOrder, String owner, double orderPrice, int remainingSize, SortedMap<Double, Queue<Order>> matchingOrderBook) {
        for (double priceLevel : matchingOrderBook.keySet()) {
            if (isBuyOrder && orderPrice < priceLevel) {
                break;
            }
            if (!isBuyOrder && orderPrice > priceLevel) {
                break;
            }
            for (Order order : matchingOrderBook.get(priceLevel)) {
                if (remainingSize == 0) {
                    break;
                }
                int orderRemainingSize = order.getRemainingSize();
                int tradedSize = Math.min(remainingSize, orderRemainingSize);
                remainingSize -= tradedSize;
                order.setRemainingSize(orderRemainingSize - tradedSize);
                String buyer = isBuyOrder ? owner : order.getOwner();
                String seller = isBuyOrder ? order.getOwner() : owner;
                publishTrade(priceLevel, tradedSize, buyer, seller, isBuyOrder);
                LOGGER.info("matched " + tradedSize + "@$" + priceLevel + " Buyer: " + buyer + " Seller: " + seller);
            }
            matchingOrderBook.get(priceLevel).removeIf(o -> o.getRemainingSize() == 0);
            if (remainingSize == 0) {
                break;
            }
        }
        matchingOrderBook.values().removeIf(Queue::isEmpty);
        return remainingSize;
    }

    private void publishOrderBook() {
        orderBookListeners.forEach(l -> l.handleOrderBook(readOnlyBidOrderBook, readOnlyAskOrderBook));
    }

    private void publishTrade(double price, int size, String buyer, String seller, boolean isTakerSideBuy) {
        if (tradeListeners.containsKey(buyer)) {
            tradeListeners.get(buyer).handleTrade(price, size, buyer, seller);
        }
        if (tradeListeners.containsKey(seller)) {
            tradeListeners.get(seller).handleTrade(price, -size, buyer, seller);
        }
        marketTradeListeners.forEach(l -> l.handleMarketTrade(incrementingTradeId, System.currentTimeMillis(), price, size, buyer, seller, isTakerSideBuy));
        incrementingTradeId++;
    }

}
