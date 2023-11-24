package ca.webdev.exchange.web.rest;

import ca.webdev.exchange.matching.MatchingEngine;
import ca.webdev.exchange.web.model.OrderCancelRequest;
import ca.webdev.exchange.web.model.OrderCancelResponse;
import ca.webdev.exchange.web.model.OrderInsertRequest;
import ca.webdev.exchange.web.model.OrderInsertResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
public class OrderRestController {

    @Autowired
    private MatchingEngine matchingEngine;

    @PostMapping(value = "/orderinsert")
    public ResponseEntity<OrderInsertResponse> insertOrder(@RequestBody OrderInsertRequest orderInsertRequest) {
        if ("buy".equalsIgnoreCase(orderInsertRequest.getSide())) {
            UUID orderId = matchingEngine.insertBuyLimitOrder("web user", orderInsertRequest.getPrice(), orderInsertRequest.getSize());
            return ResponseEntity.ok(new OrderInsertResponse(orderId.toString()));
        } else {
            UUID orderId = matchingEngine.insertSellLimitOrder("web user", orderInsertRequest.getPrice(), orderInsertRequest.getSize());
            return ResponseEntity.ok(new OrderInsertResponse(orderId.toString()));
        }
    }

    @PostMapping(value = "/ordercancel")
    public ResponseEntity<Void> cancelOder(@RequestBody OrderCancelRequest orderCancelRequest) {
        matchingEngine.cancelOrder(UUID.fromString(orderCancelRequest.getOrderId()));
        return ResponseEntity.ok().build();
    }

}
