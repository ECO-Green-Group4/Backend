package com.evmarket.trade.controller;


import com.evmarket.trade.entity.User;
import com.evmarket.trade.response.PaymentResponse;
import com.evmarket.trade.response.common.BaseResponse;
import com.evmarket.trade.service.AuthService;
import com.evmarket.trade.service.PaymentService;
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
    private com.evmarket.trade.serviceImp.StripePaymentServiceImpl stripePaymentService;

    @PostMapping("/package")
    public ResponseEntity<BaseResponse<PaymentResponse>> payListingPackage(
            @RequestParam @NotNull Long listingPackageId,
            Authentication authentication) {
        User user = authService.getCurrentUser(authentication);
        return ResponseEntity.ok(paymentService.payListingPackage(listingPackageId, user));
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


    @PostMapping("/addon")
    public ResponseEntity<BaseResponse<PaymentResponse>> payContractAddOn(
            @RequestParam @NotNull Long contractAddOnId,
            Authentication authentication) {
        User user = authService.getCurrentUser(authentication);
        return ResponseEntity.ok(paymentService.payContractAddOn(contractAddOnId, user));
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


    // ==================== Stripe Endpoints ====================

    @PostMapping("/package/stripe")
    public ResponseEntity<BaseResponse<com.evmarket.trade.response.StripeCheckoutResponse>> payListingPackageWithStripe(
            @RequestParam @NotNull Long listingPackageId,
            Authentication authentication) {
        User user = authService.getCurrentUser(authentication);
        return ResponseEntity.ok(stripePaymentService.payListingPackageWithStripe(listingPackageId, user));
    }

    @PostMapping("/membership/stripe")
    public ResponseEntity<BaseResponse<com.evmarket.trade.response.StripeCheckoutResponse>> payMembershipWithStripe(
            @RequestParam @NotNull Long servicePackageId,
            Authentication authentication) {
        User user = authService.getCurrentUser(authentication);
        return ResponseEntity.ok(stripePaymentService.payMembershipWithStripe(servicePackageId, user));
    }

    @PostMapping("/contract/stripe")
    public ResponseEntity<BaseResponse<com.evmarket.trade.response.StripeCheckoutResponse>> payContractWithStripe(
            @RequestParam @NotNull Long contractId,
            Authentication authentication) {
        User user = authService.getCurrentUser(authentication);
        return ResponseEntity.ok(stripePaymentService.payContractWithStripe(contractId, user));
    }

    @PostMapping("/addon/stripe")
    public ResponseEntity<BaseResponse<com.evmarket.trade.response.StripeCheckoutResponse>> payContractAddOnWithStripe(
            @RequestParam @NotNull Long contractAddOnId,
            Authentication authentication) {
        User user = authService.getCurrentUser(authentication);
        return ResponseEntity.ok(stripePaymentService.payContractAddOnWithStripe(contractAddOnId, user));
    }

}