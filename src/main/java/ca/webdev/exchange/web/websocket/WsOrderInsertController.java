package ca.webdev.exchange.web.websocket;

import ca.webdev.exchange.matching.MatchingEngine;
import ca.webdev.exchange.web.model.WsOrderInsertInstruction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

@Controller
public class WsOrderInsertController {

    @Autowired
    private MatchingEngine matchingEngine;

    @MessageMapping("/orderinsert")
    public void handleOrderInsert(WsOrderInsertInstruction message) {
        System.out.println("received OrderInsertInstruction message " + message.getInstruction());
        String[] split = message.getInstruction().split(" ");
        String side = split[0];
        String[] sizePriceSplit = split[1].split("@");
        String size = sizePriceSplit[0];
        String price = sizePriceSplit[1].split("\\$")[1];
        if ("B".equalsIgnoreCase(side) || "Buy".equalsIgnoreCase(side)) {
            matchingEngine.insertBuyLimitOrder("web ui", Double.parseDouble(price), Integer.parseInt(size));
        } else {
            matchingEngine.insertSellLimitOrder("web ui", Double.parseDouble(price), Integer.parseInt(size));
        }
    }

}    
