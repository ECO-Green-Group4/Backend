package com.evmarket.trade.controller;

import com.evmarket.trade.entity.User;
import com.evmarket.trade.request.MomoCallbackRequest;
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

    @PostMapping("/package")
    public ResponseEntity<BaseResponse<PaymentResponse>> payListingPackage(
            @RequestParam @NotNull Long listingPackageId,
            Authentication authentication) {
        User user = authService.getCurrentUser(authentication);
        return ResponseEntity.ok(paymentService.payListingPackage(listingPackageId, user));
    }

    @PostMapping("/package/vnpay")
    public ResponseEntity<BaseResponse<PaymentResponse>> payListingPackageWithVNPay(
            @RequestParam @NotNull Long listingPackageId,
            HttpServletRequest request,
            Authentication authentication) {
        User user = authService.getCurrentUser(authentication);
        String ipAddress = vnPayService.getIpAddress(request);
        return ResponseEntity.ok(paymentService.payListingPackageWithVNPay(listingPackageId, user, ipAddress));
    }

    @PostMapping("/membership")
    public ResponseEntity<BaseResponse<PaymentResponse>> payMembership(
            @RequestParam @NotNull Long servicePackageId,
            Authentication authentication) {
        User user = authService.getCurrentUser(authentication);
        return ResponseEntity.ok(paymentService.payMembership(servicePackageId, user));
    }

    @PostMapping("/contract")
    public ResponseEntity<BaseResponse<PaymentResponse>> payContract(
            @RequestParam @NotNull Long contractId,
            Authentication authentication) {
        User user = authService.getCurrentUser(authentication);
        return ResponseEntity.ok(paymentService.payContract(contractId, user));
    }

    @PostMapping("/contract/vnpay")
    public ResponseEntity<BaseResponse<PaymentResponse>> payContractWithVNPay(
            @RequestParam @NotNull Long contractId,
            HttpServletRequest request,
            Authentication authentication) {
        User user = authService.getCurrentUser(authentication);
        String ipAddress = vnPayService.getIpAddress(request);
        return ResponseEntity.ok(paymentService.payContractWithVNPay(contractId, user, ipAddress));
    }

    @PostMapping("/addon")
    public ResponseEntity<BaseResponse<PaymentResponse>> payContractAddOn(
            @RequestParam @NotNull Long contractAddOnId,
            Authentication authentication) {
        User user = authService.getCurrentUser(authentication);
        return ResponseEntity.ok(paymentService.payContractAddOn(contractAddOnId, user));
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

    @GetMapping("/momo-callback")
    public ResponseEntity<BaseResponse<PaymentResponse>> moMoCallback(
            @ModelAttribute MomoCallbackRequest request) {
        log.info("Received MoMo callback: orderId={}, resultCode={}",
                request.getOrderId(), request.getResultCode());
        return ResponseEntity.ok(paymentService.handleMoMoCallback(request));
    }

    @GetMapping("/vnpay-callback")
    public ResponseEntity<BaseResponse<PaymentResponse>> vnPayCallback(
            @ModelAttribute VNPayCallbackRequest request) {
        log.info("Received VNPay callback: txnRef={}, responseCode={}",
                request.getVnp_TxnRef(), request.getVnp_ResponseCode());
        return ResponseEntity.ok(paymentService.handleVNPayCallback(request));
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