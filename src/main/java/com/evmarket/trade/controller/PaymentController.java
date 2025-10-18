package com.evmarket.trade.controller;

import com.evmarket.trade.entity.User;
import com.evmarket.trade.request.VNPayCallbackRequest;
import com.evmarket.trade.response.PaymentResponse;
import com.evmarket.trade.response.common.BaseResponse;
import com.evmarket.trade.service.AuthService;
import com.evmarket.trade.service.PaymentService;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payments")
@CrossOrigin(origins = "*")
@Slf4j
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private AuthService authService;

    // 1. THANH TOÁN GÓI TIN VIP
    @PostMapping("/package")
    public ResponseEntity<BaseResponse<PaymentResponse>> payListingPackage(
            @RequestParam @NotNull Long listingPackageId,
            Authentication authentication) {
        User user = authService.getCurrentUser(authentication);
        return ResponseEntity.ok(paymentService.payListingPackage(listingPackageId, user));
    }

    // 2. THANH TOÁN MEMBERSHIP
    @PostMapping("/membership")
    public ResponseEntity<BaseResponse<PaymentResponse>> payMembership(
            @RequestParam @NotNull Long servicePackageId,
            Authentication authentication) {
        User user = authService.getCurrentUser(authentication);
        return ResponseEntity.ok(paymentService.payMembership(servicePackageId, user));
    }

    // 3. THANH TOÁN HỢP ĐỒNG
    @PostMapping("/contract")
    public ResponseEntity<BaseResponse<PaymentResponse>> payContract(
            @RequestParam @NotNull Long contractId,
            Authentication authentication) {
        User user = authService.getCurrentUser(authentication);
        return ResponseEntity.ok(paymentService.payContract(contractId, user));
    }

    // 4. THANH TOÁN ADDON
    @PostMapping("/addon")
    public ResponseEntity<BaseResponse<PaymentResponse>> payContractAddOn(
            @RequestParam @NotNull Long contractAddOnId,
            Authentication authentication) {
        User user = authService.getCurrentUser(authentication);
        return ResponseEntity.ok(paymentService.payContractAddOn(contractAddOnId, user));
    }

    // CALLBACK TỪ VNPAY
    @GetMapping("/vnpay/callback")
    public ResponseEntity<BaseResponse<PaymentResponse>> vnPayCallback(
            @ModelAttribute VNPayCallbackRequest request) {
        log.info("Received VNPay callback: vnp_TxnRef={}, vnp_ResponseCode={}",
                request.getVnp_TxnRef(), request.getVnp_ResponseCode());
        return ResponseEntity.ok(paymentService.handleVNPayCallback(request));
    }

    // LỊCH SỬ THANH TOÁN
    @GetMapping("/history")
    public ResponseEntity<BaseResponse<List<PaymentResponse>>> getPaymentHistory(
            Authentication authentication) {
        User user = authService.getCurrentUser(authentication);
        return ResponseEntity.ok(paymentService.getMyPayments(user));
    }

    // LẤY DANH SÁCH GÓI MEMBERSHIP
    @GetMapping("/membership/packages")
    public ResponseEntity<BaseResponse<List<com.evmarket.trade.entity.ServicePackage>>> getMembershipPackages() {
        return ResponseEntity.ok(paymentService.getMembershipPackages());
    }

    // LẤY DANH SÁCH GÓI TIN VIP
    @GetMapping("/vip/packages")
    public ResponseEntity<BaseResponse<List<com.evmarket.trade.entity.ServicePackage>>> getVipPackages() {
        return ResponseEntity.ok(paymentService.getListingVipPackages());
    }

    // LẤY CHI TIẾT THANH TOÁN
    @GetMapping("/{paymentId}")
    public ResponseEntity<BaseResponse<PaymentResponse>> getPaymentDetail(
            @PathVariable Long paymentId,
            Authentication authentication) {
        User user = authService.getCurrentUser(authentication);
        return ResponseEntity.ok(paymentService.getPaymentById(paymentId, user));
    }
}