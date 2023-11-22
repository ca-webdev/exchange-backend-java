package ca.webdev.exchange.web.websocket;

import ca.webdev.exchange.web.model.OpenHighLowClose;
import ca.webdev.exchange.web.model.RecentTrade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
public class OHLCWebSocketPublisher {

    @Autowired
    private SimpMessagingTemplate template;

    public void publish(OpenHighLowClose ohlc) {
        template.convertAndSend("/topic/ohlc", ohlc);
    }


}
