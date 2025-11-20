package com.evmarket.trade.controller;


import com.evmarket.trade.entity.User;
import com.evmarket.trade.request.VNPayCallbackRequest;
import com.evmarket.trade.response.PaymentResponse;
import com.evmarket.trade.response.common.BaseResponse;
import com.evmarket.trade.service.AuthService;
import com.evmarket.trade.service.PaymentService;
import com.evmarket.trade.service.VNPayService;
import jakarta.servlet.http.HttpServletRequest;
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

    @Autowired
    private VNPayService vnPayService;

    @PostMapping("/package/vnpay")
    public ResponseEntity<BaseResponse<PaymentResponse>> payListingPackageWithVNPay(
            @RequestParam @NotNull Long listingPackageId,
            HttpServletRequest request,
            Authentication authentication) {
        User user = authService.getCurrentUser(authentication);
        String ipAddress = vnPayService.getIpAddress(request);
        return ResponseEntity.ok(paymentService.payListingPackageWithVNPay(listingPackageId, user, ipAddress));
    }
    @PostMapping("/membership/vnpay")
    public ResponseEntity<BaseResponse<PaymentResponse>> payMembershipWithVNPay(
            @RequestParam @NotNull Long servicePackageId,
            HttpServletRequest request,
            Authentication authentication) {
        User user = authService.getCurrentUser(authentication);
        String ipAddress = vnPayService.getIpAddress(request);
        return ResponseEntity.ok(paymentService.payMembershipWithVNPay(servicePackageId, user, ipAddress));
    }

    @PostMapping("/addon/vnpay")
    public ResponseEntity<BaseResponse<PaymentResponse>> payContractAddOnWithVNPay(
            @RequestParam @NotNull Long contractAddOnId,
            HttpServletRequest request,
            Authentication authentication) {
        User user = authService.getCurrentUser(authentication);
        String ipAddress = vnPayService.getIpAddress(request);
        return ResponseEntity.ok(paymentService.payContractAddOnWithVNPay(contractAddOnId, user, ipAddress));
    }

    @PostMapping("/contract/{contractId}/addons/vnpay")
    public ResponseEntity<BaseResponse<PaymentResponse>> payContractAddonsWithVNPay(
            @PathVariable @NotNull Long contractId,
            HttpServletRequest request,
            Authentication authentication) {
        User user = authService.getCurrentUser(authentication);
        String ipAddress = vnPayService.getIpAddress(request);
        return ResponseEntity.ok(paymentService.payContractAddonsWithVNPay(contractId, user, ipAddress));
    }


    @GetMapping("/vnpay-callback")
    public ResponseEntity<BaseResponse<PaymentResponse>> vnPayCallback(
            @ModelAttribute VNPayCallbackRequest request) {
        log.info("Received VNPay callback: txnRef={}, responseCode={}",
                request.getVnp_TxnRef(), request.getVnp_ResponseCode());
        return ResponseEntity.ok(paymentService.handleVNPayCallback(request));
    }

    @GetMapping("/vnpay-frontend-callback")
    public ResponseEntity<BaseResponse<PaymentResponse>> vnPayFrontendCallback(
            @ModelAttribute VNPayCallbackRequest request) {
        log.info("Received VNPay frontend callback: txnRef={}, responseCode={}",
                request.getVnp_TxnRef(), request.getVnp_ResponseCode());
        
        // Process the callback and update payment status
        BaseResponse<PaymentResponse> result = paymentService.handleVNPayCallback(request);

        // Build redirect target based on payment type
        String status = request.isSuccess() ? "success" : "failed";
        String paymentId = request.getVnp_TxnRef().contains("_")
                ? request.getVnp_TxnRef().substring(0, request.getVnp_TxnRef().indexOf("_"))
                : request.getVnp_TxnRef();

        String frontendUrl;
        PaymentResponse data = result.getData();
        if (data != null && data.getPaymentType() != null && data.getPaymentType().equalsIgnoreCase("ADDON")) {
            // For ADDON payments, redirect straight to Home
            frontendUrl = "http://localhost:5173/";
        } else {
            // Default behavior (e.g., PACKAGE/MEMBERSHIP): redirect to waiting page
            frontendUrl = "http://localhost:5173/waiting?status=" + status + "&paymentId=" + paymentId;
        }

        return ResponseEntity.status(302)
                .header("Location", frontendUrl)
                .body(result);
    }

    @GetMapping("/history")
    public ResponseEntity<BaseResponse<List<PaymentResponse>>> getPaymentHistory(
            Authentication authentication) {
        User user = authService.getCurrentUser(authentication);
        return ResponseEntity.ok(paymentService.getMyPayments(user));
    }

    @GetMapping("/membership/packages")
    public ResponseEntity<BaseResponse<List<com.evmarket.trade.entity.ServicePackage>>> getMembershipPackages() {
        return ResponseEntity.ok(paymentService.getMembershipPackages());
    }

    @GetMapping("/vip/packages")
    public ResponseEntity<BaseResponse<List<com.evmarket.trade.entity.ServicePackage>>> getVipPackages() {
        return ResponseEntity.ok(paymentService.getListingVipPackages());
    }

    @GetMapping("/{paymentId}")
    public ResponseEntity<BaseResponse<PaymentResponse>> getPaymentDetail(
            @PathVariable Long paymentId,
            Authentication authentication) {
        User user = authService.getCurrentUser(authentication);
        return ResponseEntity.ok(paymentService.getPaymentById(paymentId, user));
    }

}