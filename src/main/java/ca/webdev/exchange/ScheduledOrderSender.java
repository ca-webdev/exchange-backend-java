package ca.webdev.exchange;

import ca.webdev.exchange.matching.MatchingEngine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Random;

@Component
public class ScheduledOrderSender {

    private final Random random = new Random();

    private double price = 15.0;

    @Autowired
    private MatchingEngine matchingEngine;

    /*@Scheduled(fixedRate = 2_000)
    public void fireOrder() {
        System.out.println("fireOrder");
        matchingEngine.insertBuyLimitOrder("scheduledOrderSender", Util.round(random.nextDouble() * 10, matchingEngine.getTickSizeInPrecision()), 1);
        matchingEngine.insertSellLimitOrder("scheduledOrderSender", Util.round(random.nextDouble() * 10, matchingEngine.getTickSizeInPrecision()), 1);
    }*/

    @Scheduled(fixedRate = 2_000)
    public void fireRandomWalkOrder() {
        System.out.println("fire random walk orders");
        if (random.nextBoolean()) {
            matchingEngine.insertBuyLimitOrder("scheduledOrderSender", Util.round(price, matchingEngine.getTickSizeInPrecision()), 1);
        } else {
            matchingEngine.insertSellLimitOrder("scheduledOrderSender", Util.round(price, matchingEngine.getTickSizeInPrecision()), 1);
        }

        price = price + random.nextGaussian(0, 3 * matchingEngine.getTickSize());
    }
}