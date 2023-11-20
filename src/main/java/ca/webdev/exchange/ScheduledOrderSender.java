package ca.webdev.exchange;

import ca.webdev.exchange.matching.MatchingEngine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Random;

@Component
public class ScheduledOrderSender {

    private final Random random = new Random();

    @Autowired
    private MatchingEngine matchingEngine;

    @Autowired
    private SimpMessagingTemplate template;

    @Scheduled(fixedRate = 2000)
    public void fireOrder() {
        System.out.println(Thread.currentThread().getName() + " fireOrder is called");
        matchingEngine.insertBuyLimitOrder("scheduledOrderSender", Util.round(random.nextDouble() * 10, matchingEngine.getTickSizeInPrecision()), 1);
        matchingEngine.insertSellLimitOrder("scheduledOrderSender", Util.round(random.nextDouble() * 10, matchingEngine.getTickSizeInPrecision()), 1);
    }
}