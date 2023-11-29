package ca.webdev.exchange.web;

import ca.webdev.exchange.matching.MatchingEngine;
import ca.webdev.exchange.web.model.OpenHighLowClose;
import ca.webdev.exchange.web.websocket.Publisher;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.SortedMap;
import java.util.concurrent.ConcurrentSkipListMap;

@Component
public class OpenHighLowCloseComponent {

    private final SortedMap<Instant, OpenHighLowClose> oneMinuteOhlcMap = new ConcurrentSkipListMap<>();

    public OpenHighLowCloseComponent(MatchingEngine matchingEngine, Publisher publisher) {
        matchingEngine.registerMarketTradeListener((tradeId, tradeTimeInEpochMillis, price, size, buyer, seller, isTakerSideBuy) -> {
            Instant truncatedToMinute = Instant.ofEpochMilli(tradeTimeInEpochMillis).truncatedTo(ChronoUnit.MINUTES);
            if (!oneMinuteOhlcMap.containsKey(truncatedToMinute)) {
                OpenHighLowClose openHighLowClose = new OpenHighLowClose(truncatedToMinute.toEpochMilli() / 1000, price, price, price, price);
                oneMinuteOhlcMap.put(truncatedToMinute, openHighLowClose);
                publisher.publish(openHighLowClose);
            } else {
                OpenHighLowClose currentOhlc = oneMinuteOhlcMap.get(truncatedToMinute);
                if (price > currentOhlc.getHigh()) {
                    currentOhlc.setHigh(price);
                }
                if (price < currentOhlc.getLow()) {
                    currentOhlc.setLow(price);
                }
                currentOhlc.setClose(price);
                publisher.publish(currentOhlc);
            }
        });
    }

    public SortedMap<Instant, OpenHighLowClose> getOneMinuteOhlcMap() {
        return oneMinuteOhlcMap;
    }
}
