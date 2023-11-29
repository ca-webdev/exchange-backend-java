package ca.webdev.exchange.web;

import ca.webdev.exchange.Util;
import ca.webdev.exchange.listeners.MarketTradeListener;
import ca.webdev.exchange.listeners.TradeListener;
import ca.webdev.exchange.matching.MatchingEngine;
import ca.webdev.exchange.web.model.PositionPnL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import static ca.webdev.exchange.web.Constants.WEB_USER;

@Component
public class PositionPnLComponent {

    private final PositionPnL positionPnL = new PositionPnL();

    private final MatchingEngine matchingEngine;

    @Autowired
    private SimpMessagingTemplate template;

    public PositionPnLComponent(MatchingEngine matchingEngine) {
        this.matchingEngine = matchingEngine;

        positionPnL.setInitialBalance(UserOrderAndTradeComponent.INITIAL_BALANCE);
        matchingEngine.registerMarketTradeListener(new MarketTradeListener() {
            @Override
            public void handleMarketTrade(int tradeId, long tradeTimeInEpochMillis, double price, int size, String buyer, String seller, boolean isTakerSideBuy) {
                positionPnL.setMarketPrice(price);
                updateUnrealizedPnL();
                publishPositionPnL();
            }
        });

        matchingEngine.registerTradeListener(WEB_USER, new TradeListener() {
            @Override
            public void handleTrade(long tradeTradeInEpochMillis, boolean isBuyTrade, double price, int size, String buyer, String seller) {
                if (positionPnL.getPosition() == 0 || Integer.signum(size) == Long.signum(positionPnL.getPosition())) {
                    positionPnL.setAverageEntryPrice((positionPnL.getAverageEntryPrice() * positionPnL.getPosition() + price * size) / (positionPnL.getPosition() + size));
                    positionPnL.setPosition(positionPnL.getPosition() + size);
                    updateUnrealizedPnL();
                    publishPositionPnL();
                    return;
                }

                // sign of the trade size is different from the sign of position, hence realized PnL. For examples,
                // position is -10, trade size is +5
                // position is -10, trade size is +15
                // position is +10, trade size is -3
                long closedPosition = Math.min(Math.abs(size), Math.abs(positionPnL.getPosition())) * Long.signum(positionPnL.getPosition());
                positionPnL.setRealizedPnL(positionPnL.getRealizedPnL() + (price - positionPnL.getAverageEntryPrice()) * closedPosition);

                if (closedPosition == positionPnL.getPosition()) {
                    positionPnL.setAverageEntryPrice(positionPnL.getPosition() + size == 0 ? 0 : price);
                }

                positionPnL.setPosition(positionPnL.getPosition() + size);
                positionPnL.setMarketPrice(price);
                updateUnrealizedPnL();
                publishPositionPnL();
            }
        });
    }

    private void updateUnrealizedPnL() {
        positionPnL.setUnrealizedPnL((positionPnL.getMarketPrice() - positionPnL.getAverageEntryPrice()) * positionPnL.getPosition());
        positionPnL.setTotalPnL(positionPnL.getUnrealizedPnL() + positionPnL.getRealizedPnL());
        positionPnL.setPortfolioValue(positionPnL.getInitialBalance() + positionPnL.getTotalPnL());
        positionPnL.setPortfolioValueChange(positionPnL.getPortfolioValue() / positionPnL.getInitialBalance() - 1);
    }

    private void publishPositionPnL() {
        positionPnL.setUnrealizedPnL(Util.round(positionPnL.getUnrealizedPnL(), matchingEngine.getTickSizeInPrecision()));
        positionPnL.setRealizedPnL(Util.round(positionPnL.getRealizedPnL(), matchingEngine.getTickSizeInPrecision()));
        positionPnL.setTotalPnL(Util.round(positionPnL.getTotalPnL(), matchingEngine.getTickSizeInPrecision()));
        positionPnL.setPortfolioValue(Util.round(positionPnL.getPortfolioValue(), matchingEngine.getTickSizeInPrecision()));
        template.convertAndSend("/topic/positionpnl", positionPnL);
    }

    public PositionPnL getPositionPnL() {
        return positionPnL;
    }
}
