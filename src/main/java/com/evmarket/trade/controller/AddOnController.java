package com.evmarket.trade.controller;

import com.evmarket.trade.entity.User;
import com.evmarket.trade.request.*;
import com.evmarket.trade.response.common.BaseResponse;
import com.evmarket.trade.service.AddOnServiceInterface;
import com.evmarket.trade.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/addon")
@CrossOrigin(origins = "*")
public class AddOnController {
    
    @Autowired
    private AddOnServiceInterface addOnService;
    
    @Autowired
    private AuthService authService;
    
    // AddOn service management endpoints
    @GetMapping("/services")
    public ResponseEntity<BaseResponse<?>> getAvailableAddOnServices() {
        return ResponseEntity.ok(addOnService.getAvailableAddOnServices());
    }
    
    @GetMapping("/services/{serviceId}")
    public ResponseEntity<BaseResponse<?>> getAddOnServiceById(@PathVariable Long serviceId) {
        return ResponseEntity.ok(addOnService.getAddOnServiceById(serviceId));
    }
    
    // Contract AddOn management endpoints
    @PostMapping("/contract-addon")
    public ResponseEntity<BaseResponse<?>> createContractAddOn(@Valid @RequestBody ContractAddOnRequest request, Authentication authentication) {
        User user = authService.getCurrentUser(authentication);
        return ResponseEntity.ok(addOnService.createContractAddOn(request, user));
    }
    
    @GetMapping("/contract/{contractId}/addons")
    public ResponseEntity<BaseResponse<?>> getContractAddOns(@PathVariable Long contractId, Authentication authentication) {
        User user = authService.getCurrentUser(authentication);
        return ResponseEntity.ok(addOnService.getContractAddOns(contractId, user));
    }
    
    @GetMapping("/contract-addon/{contractAddOnId}")
    public ResponseEntity<BaseResponse<?>> getContractAddOnById(@PathVariable Long contractAddOnId, Authentication authentication) {
        User user = authService.getCurrentUser(authentication);
        return ResponseEntity.ok(addOnService.getContractAddOnById(contractAddOnId, user));
    }
    
    @DeleteMapping("/contract-addon/{contractAddOnId}")
    public ResponseEntity<BaseResponse<?>> deleteContractAddOn(@PathVariable Long contractAddOnId, Authentication authentication) {
        User user = authService.getCurrentUser(authentication);
        return ResponseEntity.ok(addOnService.deleteContractAddOn(contractAddOnId, user));
    }
    
    // AddOn payment management endpoints
    @PostMapping("/payment")
    public ResponseEntity<BaseResponse<?>> processAddOnPayment(@Valid @RequestBody AddOnPaymentRequest request, Authentication authentication) {
        User user = authService.getCurrentUser(authentication);
        return ResponseEntity.ok(addOnService.processAddOnPayment(request, user));
    }
    
    @GetMapping("/contract/{contractId}/payments")
    public ResponseEntity<BaseResponse<?>> getAddOnPayments(@PathVariable Long contractId, Authentication authentication) {
        User user = authService.getCurrentUser(authentication);
        return ResponseEntity.ok(addOnService.getAddOnPayments(contractId, user));
    }
}

