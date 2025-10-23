package com.evmarket.trade.controller;

import com.evmarket.trade.entity.User;
import com.evmarket.trade.request.ContractSignRequest;
import com.evmarket.trade.response.common.BaseResponse;
import com.evmarket.trade.service.ContractService;
import com.evmarket.trade.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/contract")
@CrossOrigin(origins = "*")
public class ContractController {
    
    @Autowired
    private ContractService contractService;
    
    @Autowired
    private AuthService authService;
    
    // Contract generation endpoints
    @PostMapping("/generate/{orderId}")
    public ResponseEntity<BaseResponse<?>> generateContract(@PathVariable Long orderId, Authentication authentication) {
        User user = authService.getCurrentUser(authentication);
        return ResponseEntity.ok(contractService.generateContract(orderId, user));
    }
    
    @GetMapping("/orders")
    public ResponseEntity<BaseResponse<?>> getContractsOfMyOrders(Authentication authentication) {
        User user = authService.getCurrentUser(authentication);
        return ResponseEntity.ok(contractService.getContractsOfMyOrders(user));
    }
    
    
    // Contract signing endpoints
    @PostMapping("/sign")
    public ResponseEntity<BaseResponse<?>> signContract(@Valid @RequestBody ContractSignRequest request, Authentication authentication) {
        User user = authService.getCurrentUser(authentication);
        return ResponseEntity.ok(contractService.signContract(request, user));
    }
    
    @PostMapping("/{contractId}/otp")
    public ResponseEntity<BaseResponse<?>> sendOTP(@PathVariable Long contractId, Authentication authentication) {
        User user = authService.getCurrentUser(authentication);
        return ResponseEntity.ok(contractService.sendOTP(contractId, user));
    }
    
    // Contract management endpoints
    @GetMapping("/my-contracts")
    public ResponseEntity<BaseResponse<?>> getMyContracts(Authentication authentication) {
        User user = authService.getCurrentUser(authentication);
        return ResponseEntity.ok(contractService.getMyContracts(user));
    }
    
    @PutMapping("/{contractId}/cancel")
    public ResponseEntity<BaseResponse<?>> cancelContract(@PathVariable Long contractId, Authentication authentication) {
        User user = authService.getCurrentUser(authentication);
        return ResponseEntity.ok(contractService.cancelContract(contractId, user));
    }
}




