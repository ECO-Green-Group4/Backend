package com.evmarket.trade.controller;

import com.evmarket.trade.entity.User;
import com.evmarket.trade.request.OrderRequest;
import com.evmarket.trade.response.common.BaseResponse;
import com.evmarket.trade.service.BuyerService;
import com.evmarket.trade.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/buyer")
@CrossOrigin(origins = "*")
public class BuyerController {
    
    @Autowired
    private BuyerService buyerService;
    
    @Autowired
    private AuthService authService;
    
    // Order management endpoints
    @PostMapping("/orders")
    public ResponseEntity<BaseResponse<?>> createOrder(@Valid @RequestBody OrderRequest request, Authentication authentication) {
        User buyer = authService.getCurrentUser(authentication);
        return ResponseEntity.ok(buyerService.createOrder(request, buyer));
    }
    
    @GetMapping("/orders")
    public ResponseEntity<BaseResponse<?>> getMyOrders(Authentication authentication) {
        User buyer = authService.getCurrentUser(authentication);
        return ResponseEntity.ok(buyerService.getMyOrders(buyer));
    }
    
    
    @PutMapping("/orders/{orderId}/cancel")
    public ResponseEntity<BaseResponse<?>> cancelOrder(@PathVariable Long orderId, Authentication authentication) {
        User buyer = authService.getCurrentUser(authentication);
        return ResponseEntity.ok(buyerService.cancelOrder(orderId, buyer));
    }
    
    // Contact information endpoint (only for battery purchases)
    @GetMapping("/orders/{orderId}/contact")
    public ResponseEntity<BaseResponse<?>> getSellerContactInfo(@PathVariable Long orderId, Authentication authentication) {
        User buyer = authService.getCurrentUser(authentication);
        return ResponseEntity.ok(buyerService.getSellerContactInfo(orderId, buyer));
    }
}




