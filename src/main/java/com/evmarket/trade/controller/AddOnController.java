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
    
    
    // Contract AddOn management endpoints
    @PostMapping("/contract-addon")
    public ResponseEntity<BaseResponse<?>> createContractAddOn(@Valid @RequestBody ContractAddOnRequest request, Authentication authentication) {
        User user = authService.getCurrentUser(authentication);
        return ResponseEntity.ok(addOnService.createContractAddOn(request, user));
    }

    @PostMapping("/contract-addon/batch")
    public ResponseEntity<BaseResponse<?>> createContractAddOns(@Valid @RequestBody ContractAddOnsRequest request, Authentication authentication) {
        User user = authService.getCurrentUser(authentication);
        return ResponseEntity.ok(addOnService.createContractAddOns(request, user));
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
    
    
}

