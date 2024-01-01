package ca.webdev.exchange;

import ca.webdev.exchange.matching.MatchingEngine;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Random;
import java.util.concurrent.atomic.AtomicReference;

@Component
public class ScheduledOrderSender {

    private static final double DURATION_CONSTANT = 1.0 / 7257600;

    private final Random random = new Random();

    private final double initialPrice = 250;

    private final AtomicReference<Double> marketTradeBasedPrice = new AtomicReference<>(initialPrice);

    private double price = initialPrice;

    private final MatchingEngine matchingEngine;

    public ScheduledOrderSender(MatchingEngine matchingEngine) {
        this.matchingEngine = matchingEngine;
        matchingEngine.registerMarketTradeListener((tradeId, tradeTimeInEpochMillis, tradePrice, size, buyer, seller, isTakerSideBuy) -> marketTradeBasedPrice.set(tradePrice));
    }

    @Scheduled(fixedRate = 1_000)
    public void randomWalkOrder() {

        int nextSize = random.nextInt(1, 4) * 10;
        matchingEngine.insertBuyLimitOrder("scheduledOrderSender", Util.round(price - 2 * matchingEngine.getTickSize(), matchingEngine.getTickSizeInPrecision()), nextSize);
        matchingEngine.insertSellLimitOrder("scheduledOrderSender", Util.round(price + 2 * matchingEngine.getTickSize(), matchingEngine.getTickSizeInPrecision()), nextSize);

        price *= 1 + (2 * DURATION_CONSTANT + 2.5 * random.nextGaussian() * Math.sqrt(DURATION_CONSTANT));
    }

    @Scheduled(fixedRate = 600)
    public void marketTradeBasedRandomTakingOrder() {
        if (random.nextBoolean()) {
            matchingEngine.insertBuyLimitOrder("scheduledOrderSender2", Util.round(marketTradeBasedPrice.get() + (random.nextBoolean() ? 1 : -1) * 2 * matchingEngine.getTickSize(), matchingEngine.getTickSizeInPrecision()), random.nextInt(1, 4) * 10);
        } else {
            matchingEngine.insertSellLimitOrder("scheduledOrderSender2", Util.round(marketTradeBasedPrice.get() + (random.nextBoolean() ? 1 : -1) * 2 * matchingEngine.getTickSize(), matchingEngine.getTickSizeInPrecision()), random.nextInt(1, 4) * 10);
        }
    }

}