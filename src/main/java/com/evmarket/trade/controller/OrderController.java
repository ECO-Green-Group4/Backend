package com.evmarket.trade.controller;

import com.evmarket.trade.entity.User;
import com.evmarket.trade.response.common.BaseResponse;
import com.evmarket.trade.service.AuthService;
import com.evmarket.trade.service.SellerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
@CrossOrigin(origins = "*")
public class OrderController {
    
    @Autowired
    private SellerService sellerService;
    
    @Autowired
    private AuthService authService;
    
    // Seller can confirm orders
    @PutMapping("/{orderId}/confirm")
    public ResponseEntity<BaseResponse<?>> confirmOrder(@PathVariable Long orderId, Authentication authentication) {
        User seller = authService.getCurrentUser(authentication);
        return ResponseEntity.ok(sellerService.confirmOrder(orderId, seller));
    }
    
    // Seller can reject orders
    @PutMapping("/{orderId}/reject")
    public ResponseEntity<BaseResponse<?>> rejectOrder(@PathVariable Long orderId, Authentication authentication) {
        User seller = authService.getCurrentUser(authentication);
        return ResponseEntity.ok(sellerService.rejectOrder(orderId, seller));
    }
}

