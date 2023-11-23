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

    private final AtomicReference<Double> price = new AtomicReference<>(15.0);

    @Autowired
    private final MatchingEngine matchingEngine;

    private double randomTakerMin = 0.1;
    private double randomTakerMax = 25.0;

    public ScheduledOrderSender(MatchingEngine matchingEngine) {
        this.matchingEngine = matchingEngine;
        matchingEngine.registerMarketTradeListener((tradeId, tradeTimeInMillisecondEpoch, tradePrice, size, buyer, seller, isTakerSideBuy) -> price.set(tradePrice));
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

    @Scheduled(fixedRate = 2_000)
    public void fireRandomWalkOrder() {
        System.out.println("fire random walk orders");
        if (random.nextBoolean()) {
            matchingEngine.insertBuyLimitOrder("scheduledOrderSender", Util.round(price.get(), matchingEngine.getTickSizeInPrecision()), random.nextInt(1, 6));
        } else {
            matchingEngine.insertSellLimitOrder("scheduledOrderSender", Util.round(price.get(), matchingEngine.getTickSizeInPrecision()), random.nextInt(1, 6));
        }

        price.set(price.get() + random.nextGaussian(0, 3 * matchingEngine.getTickSize()));
    }

}