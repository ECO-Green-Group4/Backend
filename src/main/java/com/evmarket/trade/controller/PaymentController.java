package com.evmarket.trade.controller;

import com.evmarket.trade.entity.User;
import com.evmarket.trade.response.PaymentResponse;
import com.evmarket.trade.response.common.BaseResponse;
import com.evmarket.trade.service.AuthService;
import com.evmarket.trade.service.PaymentService;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payments")
@CrossOrigin(origins = "*")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private AuthService authService;

    // Create payment for listing package
    @PostMapping("/package")
    public ResponseEntity<BaseResponse<PaymentResponse>> payListingPackage(
            @RequestParam @NotNull Long listingPackageId,
            Authentication authentication) {
        User user = authService.getCurrentUser(authentication);
        return ResponseEntity.ok(paymentService.payListingPackage(listingPackageId, user));
    }

    // Create payment for contract (car purchase)
    @PostMapping("/contract")
    public ResponseEntity<BaseResponse<PaymentResponse>> payContract(
            @RequestParam @NotNull Long contractId,
            Authentication authentication) {
        User user = authService.getCurrentUser(authentication);
        return ResponseEntity.ok(paymentService.payContract(contractId, user));
    }

    // Create payment for contract add-on
    @PostMapping("/addon")
    public ResponseEntity<BaseResponse<PaymentResponse>> payContractAddOn(
            @RequestParam @NotNull Long contractAddOnId,
            Authentication authentication) {
        User user = authService.getCurrentUser(authentication);
        return ResponseEntity.ok(paymentService.payContractAddOn(contractAddOnId, user));
    }

    // Queries
    @GetMapping("/mine")
    public ResponseEntity<BaseResponse<List<PaymentResponse>>> myPayments(Authentication authentication) {
        User user = authService.getCurrentUser(authentication);
        return ResponseEntity.ok(paymentService.getMyPayments(user));
    }

    @GetMapping("/contract/{contractId}")
    public ResponseEntity<BaseResponse<List<PaymentResponse>>> byContract(@PathVariable Long contractId, Authentication authentication) {
        User user = authService.getCurrentUser(authentication);
        return ResponseEntity.ok(paymentService.getPaymentsByContract(contractId, user));
    }

    @GetMapping("/package/{listingPackageId}")
    public ResponseEntity<BaseResponse<List<PaymentResponse>>> byPackage(@PathVariable Long listingPackageId, Authentication authentication) {
        User user = authService.getCurrentUser(authentication);
        return ResponseEntity.ok(paymentService.getPaymentsByListingPackage(listingPackageId, user));
    }
}


