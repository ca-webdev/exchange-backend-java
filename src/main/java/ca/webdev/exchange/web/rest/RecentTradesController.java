package ca.webdev.exchange.web.rest;

import ca.webdev.exchange.matching.MatchingEngine;
import ca.webdev.exchange.web.model.OpenHighLowClose;
import ca.webdev.exchange.web.model.RecentTrade;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.SortedMap;
import java.util.concurrent.ConcurrentSkipListMap;

@CrossOrigin(origins = "*")
@RestController
public class RecentTradesController {

    private final List<RecentTrade> recentTrades = new LinkedList<>();

    private final SortedMap<Instant, OpenHighLowClose> oneMinuteOhlcMap = new ConcurrentSkipListMap<>();

    public RecentTradesController(MatchingEngine matchingEngine) {
        matchingEngine.registerMarketTradeListener((tradeId, tradeTimeInMillisecondEpoch, price, size, buyer, seller, isTakerSideBuy) -> {
            recentTrades.add(new RecentTrade(tradeTimeInMillisecondEpoch, price, size, isTakerSideBuy ? "B" : "S"));
            Instant truncatedToMinute = Instant.ofEpochMilli(tradeTimeInMillisecondEpoch).truncatedTo(ChronoUnit.MINUTES);
            if (!oneMinuteOhlcMap.containsKey(truncatedToMinute)) {
                oneMinuteOhlcMap.put(truncatedToMinute, new OpenHighLowClose(truncatedToMinute.toEpochMilli(), price, price, price, price));
            } else {
                OpenHighLowClose currentOhlc = oneMinuteOhlcMap.get(truncatedToMinute);
                if (price > currentOhlc.getHigh()) {
                    currentOhlc.setHigh(price);
                }
                if (price < currentOhlc.getLow()) {
                    currentOhlc.setLow(price);
                }
                currentOhlc.setClose(price);
            }
        });
    }

    @GetMapping("/recenttrades")
    public List<RecentTrade> recentTrades() {
        return recentTrades;
    }

    @GetMapping("/ohlc")
    public Collection<OpenHighLowClose> ohlc() {
        return oneMinuteOhlcMap.values();
    }


}
