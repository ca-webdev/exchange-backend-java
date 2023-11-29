package ca.webdev.exchange;

import ca.webdev.exchange.matching.MatchingEngine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Random;
import java.util.concurrent.atomic.AtomicReference;

@Component
public class ScheduledOrderSender {

    private final Random random = new Random();

    private final double initialPrice = 15;

    private final AtomicReference<Double> marketTradeBasedPrice = new AtomicReference<>(initialPrice);

    private double price = initialPrice;

    @Autowired
    private final MatchingEngine matchingEngine;

    private double randomTakerMin = 0.1;
    private double randomTakerMax = 25.0;

    public ScheduledOrderSender(MatchingEngine matchingEngine) {
        this.matchingEngine = matchingEngine;
        matchingEngine.registerMarketTradeListener((tradeId, tradeTimeInEpochMillis, tradePrice, size, buyer, seller, isTakerSideBuy) -> marketTradeBasedPrice.set(tradePrice));
    }

    /*@Scheduled(fixedRate = 6_000)
    public void fireOrder() {
        System.out.println("fireOrder");
        double takerPrice = randomTakerMin + (randomTakerMax - randomTakerMin) * random.nextDouble();
        if (random.nextBoolean()) {
            matchingEngine.insertBuyLimitOrder("scheduledOrderSender", Util.round(takerPrice * 10, matchingEngine.getTickSizeInPrecision()), 1);
        } else {
            matchingEngine.insertSellLimitOrder("scheduledOrderSender", Util.round(takerPrice * 10, matchingEngine.getTickSizeInPrecision()), 1);
        }
    }*/

    @Scheduled(fixedRate = 1_000)
    public void randomWalkOrder() {

        int nextSize = random.nextInt(3, 6);
        matchingEngine.insertBuyLimitOrder("scheduledOrderSender", Util.round(price + 2 * matchingEngine.getTickSize(), matchingEngine.getTickSizeInPrecision()), nextSize);
        matchingEngine.insertSellLimitOrder("scheduledOrderSender", Util.round(price - 2 * matchingEngine.getTickSize(), matchingEngine.getTickSizeInPrecision()), nextSize);

        price *= (1 + random.nextGaussian(0, 3 * matchingEngine.getTickSize()));
    }

    @Scheduled(fixedRate = 600)
    public void marketTradeBasedRandomWalkOrder() {
        if (random.nextBoolean()) {
            matchingEngine.insertBuyLimitOrder("scheduledOrderSender2", Util.round(marketTradeBasedPrice.get(), matchingEngine.getTickSizeInPrecision()), random.nextInt(1, 4));
        } else {
            matchingEngine.insertSellLimitOrder("scheduledOrderSender2", Util.round(marketTradeBasedPrice.get(), matchingEngine.getTickSizeInPrecision()), random.nextInt(1, 4));
        }

        marketTradeBasedPrice.set(marketTradeBasedPrice.get() + random.nextGaussian(0, 3 * matchingEngine.getTickSize()));
    }

}