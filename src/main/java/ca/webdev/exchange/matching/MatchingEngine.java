package ca.webdev.exchange.matching;

import ca.webdev.exchange.listeners.MarketTradeListener;
import ca.webdev.exchange.listeners.OrderBookListener;
import ca.webdev.exchange.listeners.OrderStateListener;
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
    private final Map<String, OrderStateListener> orderStateListeners = new HashMap<>();

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

    public void registerTradeListener(String userName, TradeListener tradeListener) {
        tradeListeners.put(userName, tradeListener);
    }

    public void registerMarketTradeListener(MarketTradeListener marketTradeListener) {
        marketTradeListeners.add(marketTradeListener);
    }

    public void registerOrderStateListener(String userName, OrderStateListener orderStateListener) {
        orderStateListeners.put(userName, orderStateListener);
    }

    public SortedMap<Double, Queue<Order>> getReadOnlyBidOrderBook() {
        return readOnlyBidOrderBook;
    }

    public SortedMap<Double, Queue<Order>> getReadOnlyAskOrderBook() {
        return readOnlyAskOrderBook;
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
        orderIdToPriceLevelMap.put(orderId, price);
        publishOrderState(orderId, owner, isBuyOrder, price, size, Double.NaN, 0, OrderStatus.InsertAccepted);
        int remainingSize = match(orderId, isBuyOrder, owner, price, size, matchingOrderBook);

        if (remainingSize == 0) {
            publishOrderBook();
            return;
        }
        orderBook.computeIfAbsent(price, k -> new ConcurrentLinkedQueue<>()).add(new Order(orderId, owner, size, remainingSize));
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
            if (!bidOrderBook.isEmpty() && priceLevel > bidOrderBook.firstKey() && askOrderBook.containsKey(priceLevel)) {
                askOrderBook.get(priceLevel).removeIf(o -> orderId.equals(o.getOrderId()));
                if (askOrderBook.get(priceLevel).isEmpty()) {
                    askOrderBook.remove(priceLevel);
                }
            } else if (bidOrderBook.containsKey(priceLevel)) {
                bidOrderBook.get(priceLevel).removeIf(o -> orderId.equals(o.getOrderId()));
                if (bidOrderBook.get(priceLevel).isEmpty()) {
                    bidOrderBook.remove(priceLevel);
                }
            }
            orderIdToPriceLevelMap.remove(orderId);

            publishOrderBook();
        });
    }

    private int match(UUID orderId, boolean isBuyOrder, String owner, double orderPrice, int initialSize, SortedMap<Double, Queue<Order>> matchingOrderBook) {
        int remainingSize = initialSize;
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
                publishOrderState(order.getOrderId(), order.getOwner(), !isBuyOrder, priceLevel, order.getInitialSize(), priceLevel, order.getInitialSize() - order.getRemainingSize(), order.getRemainingSize() == 0 ? OrderStatus.FullyFilled : OrderStatus.PartiallyFilled);
                publishOrderState(orderId, owner, isBuyOrder, orderPrice, initialSize, priceLevel, initialSize - remainingSize, remainingSize == 0 ? OrderStatus.FullyFilled : OrderStatus.PartiallyFilled);
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

    private void publishOrderState(UUID orderId, String owner, boolean isBuyOrder, double price, int size, double filledPrice, int filledSize, OrderStatus orderStatus) {
        orderStateListeners.computeIfPresent(owner, (user, listener) -> {
            listener.handleOrderState(orderId, System.currentTimeMillis(), isBuyOrder, price, size, filledPrice, filledSize, orderStatus);
            return listener;
        });
    }

    private void publishOrderBook() {
        orderBookListeners.forEach(l -> l.handleOrderBook(readOnlyBidOrderBook, readOnlyAskOrderBook));
    }

    private void publishTrade(double price, int size, String buyer, String seller, boolean isTakerSideBuy) {
        long tradeTime = System.currentTimeMillis();
        if (tradeListeners.containsKey(buyer)) {
            tradeListeners.get(buyer).handleTrade(tradeTime, true, price, size, buyer, seller);
        }
        if (tradeListeners.containsKey(seller)) {
            tradeListeners.get(seller).handleTrade(tradeTime, false, price, -size, buyer, seller);
        }
        marketTradeListeners.forEach(l -> l.handleMarketTrade(incrementingTradeId, tradeTime, price, size, buyer, seller, isTakerSideBuy));
        incrementingTradeId++;
    }

}
