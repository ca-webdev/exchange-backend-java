package ca.webdev.exchange.web.websocket.testingtool;

import ca.webdev.exchange.web.model.OrderBookUpdate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.Map;
import java.util.Random;

//@Component
public class ScheduledOrderBookUpdates {

    @Autowired
    private SimpMessagingTemplate template;

    @Scheduled(fixedRate = 2000)
    public void fireOrderBookUpdates() {
        System.out.println("fireOrderBookUpdates is called");
        this.template.convertAndSend("/topic/orderbookupdates", new OrderBookUpdate(Map.of(new Random().nextDouble() * 10, 2), Map.of(new Random().nextDouble() * 10, 2)));
    }
}