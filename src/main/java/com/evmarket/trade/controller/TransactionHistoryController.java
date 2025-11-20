package com.evmarket.trade.controller;

import com.evmarket.trade.entity.User;
import com.evmarket.trade.response.common.BaseResponse;
import com.evmarket.trade.service.AuthService;
import com.evmarket.trade.service.TransactionHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/transactions")
@CrossOrigin(origins = "*")
public class TransactionHistoryController {

    @Autowired
    private TransactionHistoryService transactionHistoryService;

    @Autowired
    private AuthService authService;

    // Lấy lịch sử giao dịch của tôi (tất cả)
    @GetMapping("/history")
    public ResponseEntity<BaseResponse<?>> getMyTransactionHistory(Authentication authentication) {
        User user = authService.getCurrentUser(authentication);
        return ResponseEntity.ok(transactionHistoryService.getMyTransactionHistory(user));
    }

    // Lấy lịch sử giao dịch với filter
    @GetMapping("/history/filter")
    public ResponseEntity<BaseResponse<?>> getMyTransactionHistoryWithFilter(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to,
            Authentication authentication) {
        User user = authService.getCurrentUser(authentication);
        return ResponseEntity.ok(transactionHistoryService.getMyTransactionHistory(user, status, from, to));
    }

    // Lấy chi tiết một giao dịch cụ thể
    @GetMapping("/{orderId}/detail")
    public ResponseEntity<BaseResponse<?>> getTransactionDetail(
            @PathVariable Long orderId,
            Authentication authentication) {
        User user = authService.getCurrentUser(authentication);
        return ResponseEntity.ok(transactionHistoryService.getTransactionDetail(orderId, user));
    }
}

