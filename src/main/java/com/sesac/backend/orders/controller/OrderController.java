package com.sesac.backend.orders.controller;


import com.sesac.backend.orders.dto.request.OrderRequest;
import com.sesac.backend.orders.dto.response.OrderResponse;
import com.sesac.backend.orders.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.UUID;

@Slf4j
@RequestMapping("/orders")
@RequiredArgsConstructor
@RestController
public class OrderController {

    private final OrderService orderService;

    @PostMapping("")
    public ResponseEntity<?> createOrder(@RequestBody OrderRequest orderRequest, Authentication authentication) {

        UUID userId = UUID.fromString(authentication.getName());

        System.out.println("orderRequest: " + orderRequest);

        try {
            OrderResponse orderResponse = orderService.createOrder(orderRequest, userId);
            return ResponseEntity.ok(Map.of(
                    "userName", orderResponse.getNickname(),
                    "merchantUid", orderResponse.getMerchantUid(),
                    "totalAmount", orderResponse.getTotalAmount()
            ));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(Map.of(
                    "error", e.getMessage()
            ));
        }
    }
}
