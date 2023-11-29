package ca.webdev.exchange.web.rest;

import ca.webdev.exchange.matching.MatchingEngine;
import ca.webdev.exchange.web.UserOrderAndTradeComponent;
import ca.webdev.exchange.web.model.OrderCancelRequest;
import ca.webdev.exchange.web.model.OrderCancelResponse;
import ca.webdev.exchange.web.model.OrderInsertRequest;
import ca.webdev.exchange.web.model.OrderInsertResponse;
import ca.webdev.exchange.web.model.OrderUpdate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static ca.webdev.exchange.web.Constants.WEB_USER;

@CrossOrigin
@RestController
public class OrderRestController {

    @Autowired
    private MatchingEngine matchingEngine;

    @Autowired
    private UserOrderAndTradeComponent userOrderAndTradeComponent;

    @PostMapping(value = "/orderinsert")
    public ResponseEntity<OrderInsertResponse> insertOrder(@RequestBody OrderInsertRequest orderInsertRequest) {
        if ("buy".equalsIgnoreCase(orderInsertRequest.getSide())) {
            UUID orderId = matchingEngine.insertBuyLimitOrder(WEB_USER, orderInsertRequest.getPrice(), orderInsertRequest.getSize());
            return ResponseEntity.ok(new OrderInsertResponse(orderId.toString(), getInsertMessage(orderInsertRequest)));
        } else {
            UUID orderId = matchingEngine.insertSellLimitOrder(WEB_USER, orderInsertRequest.getPrice(), orderInsertRequest.getSize());
            return ResponseEntity.ok(new OrderInsertResponse(orderId.toString(), getInsertMessage(orderInsertRequest)));
        }
    }

    private static String getInsertMessage(OrderInsertRequest orderInsertRequest) {
        return String.format("%s order (price %.2f and size %d) is successfully inserted", orderInsertRequest.getSide(), orderInsertRequest.getPrice(), orderInsertRequest.getSize());
    }

    @PostMapping(value = "/ordercancel")
    public ResponseEntity<OrderCancelResponse> cancelOder(@RequestBody OrderCancelRequest orderCancelRequest) throws ExecutionException, InterruptedException {
        CompletableFuture<String> message = matchingEngine.cancelOrder(UUID.fromString(orderCancelRequest.getOrderId()));
        return ResponseEntity.ok(new OrderCancelResponse(orderCancelRequest.getOrderId(), message.get()));
    }

    @GetMapping("/orderupdates")
    public Map<UUID, List<OrderUpdate>> getOrderUpdates() {
        return userOrderAndTradeComponent.getCachedOrderUpdates();
    }

}
