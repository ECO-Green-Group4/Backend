package com.evmarket.trade.controller;

import com.evmarket.trade.entity.User;
import com.evmarket.trade.response.OrderResponse;
import com.evmarket.trade.response.common.BaseResponse;
import com.evmarket.trade.service.AuthService;
import com.evmarket.trade.service.StaffService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/staff")
@CrossOrigin(origins = "*")
public class StaffController {
    
    @Autowired
    private StaffService staffService;
    
    @Autowired
    private AuthService authService;
    
    /**
     * Xem các orders được gán cho staff
     */
    @GetMapping("/orders")
    @PreAuthorize("hasRole('STAFF')")
    public ResponseEntity<BaseResponse<List<OrderResponse>>> getAssignedOrders(Authentication authentication) {
        User staff = authService.getCurrentUser(authentication);
        return ResponseEntity.ok(staffService.getAssignedOrders(staff));
    }
    
    /**
     * Xem chi tiết order được gán
     */
    @GetMapping("/orders/{orderId}")
    @PreAuthorize("hasRole('STAFF')")
    public ResponseEntity<BaseResponse<OrderResponse>> getAssignedOrderById(@PathVariable Long orderId, Authentication authentication) {
        User staff = authService.getCurrentUser(authentication);
        return ResponseEntity.ok(staffService.getAssignedOrderById(orderId, staff));
    }
}
