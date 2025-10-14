package com.evmarket.trade.controller;

import com.evmarket.trade.entity.User;
import com.evmarket.trade.request.*;
import com.evmarket.trade.response.common.BaseResponse;
import com.evmarket.trade.service.SellerService;
import com.evmarket.trade.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/seller")
@CrossOrigin(origins = "*")
public class SellerController {
    
    @Autowired
    private SellerService sellerService;
    
    @Autowired
    private AuthService authService;
    
    // Vehicle management endpoints
    @PostMapping("/vehicles")
    public ResponseEntity<BaseResponse<?>> createVehicle(@Valid @RequestBody VehicleRequest request, Authentication authentication) {
        User seller = authService.getCurrentUser(authentication);
        return ResponseEntity.ok(sellerService.createVehicle(request, seller));
    }
    
    @PutMapping("/vehicles/{vehicleId}")
    public ResponseEntity<BaseResponse<?>> updateVehicle(@PathVariable Long vehicleId, 
                                                        @Valid @RequestBody VehicleRequest request, 
                                                        Authentication authentication) {
        User seller = authService.getCurrentUser(authentication);
        return ResponseEntity.ok(sellerService.updateVehicle(vehicleId, request, seller));
    }
    
    @DeleteMapping("/vehicles/{vehicleId}")
    public ResponseEntity<BaseResponse<?>> deleteVehicle(@PathVariable Long vehicleId, Authentication authentication) {
        User seller = authService.getCurrentUser(authentication);
        return ResponseEntity.ok(sellerService.deleteVehicle(vehicleId, seller));
    }
    
    @GetMapping("/vehicles")
    public ResponseEntity<BaseResponse<?>> getMyVehicles(Authentication authentication) {
        User seller = authService.getCurrentUser(authentication);
        return ResponseEntity.ok(sellerService.getMyVehicles(seller));
    }
    
    @GetMapping("/vehicles/{vehicleId}")
    public ResponseEntity<BaseResponse<?>> getVehicleById(@PathVariable Long vehicleId, Authentication authentication) {
        User seller = authService.getCurrentUser(authentication);
        return ResponseEntity.ok(sellerService.getVehicleById(vehicleId, seller));
    }
    
    // Battery management endpoints
    @PostMapping("/batteries")
    public ResponseEntity<BaseResponse<?>> createBattery(@Valid @RequestBody BatteryRequest request, Authentication authentication) {
        User seller = authService.getCurrentUser(authentication);
        return ResponseEntity.ok(sellerService.createBattery(request, seller));
    }
    
    @PutMapping("/batteries/{batteryId}")
    public ResponseEntity<BaseResponse<?>> updateBattery(@PathVariable Long batteryId, 
                                                        @Valid @RequestBody BatteryRequest request, 
                                                        Authentication authentication) {
        User seller = authService.getCurrentUser(authentication);
        return ResponseEntity.ok(sellerService.updateBattery(batteryId, request, seller));
    }
    
    @DeleteMapping("/batteries/{batteryId}")
    public ResponseEntity<BaseResponse<?>> deleteBattery(@PathVariable Long batteryId, Authentication authentication) {
        User seller = authService.getCurrentUser(authentication);
        return ResponseEntity.ok(sellerService.deleteBattery(batteryId, seller));
    }
    
    @GetMapping("/batteries")
    public ResponseEntity<BaseResponse<?>> getMyBatteries(Authentication authentication) {
        User seller = authService.getCurrentUser(authentication);
        return ResponseEntity.ok(sellerService.getMyBatteries(seller));
    }
    
    @GetMapping("/batteries/{batteryId}")
    public ResponseEntity<BaseResponse<?>> getBatteryById(@PathVariable Long batteryId, Authentication authentication) {
        User seller = authService.getCurrentUser(authentication);
        return ResponseEntity.ok(sellerService.getBatteryById(batteryId, seller));
    }
    
    // Listing management endpoints - using new separate APIs
    @PutMapping("/listings/{listingId}")
    public ResponseEntity<BaseResponse<?>> updateListing(@PathVariable Long listingId,  
                                                        @Valid @RequestBody ListingRequest request, 
                                                        Authentication authentication) {
        User seller = authService.getCurrentUser(authentication);
        return ResponseEntity.ok(sellerService.updateListing(listingId, request, seller));
    }
    
    @DeleteMapping("/listings/{listingId}")
    public ResponseEntity<BaseResponse<?>> deleteListing(@PathVariable Long listingId, Authentication authentication) {
        User seller = authService.getCurrentUser(authentication);
        return ResponseEntity.ok(sellerService.deleteListing(listingId, seller));
    }
    
    @GetMapping("/listings")
    public ResponseEntity<BaseResponse<?>> getMyListings(Authentication authentication) {
        User seller = authService.getCurrentUser(authentication);
        return ResponseEntity.ok(sellerService.getMyListings(seller));
    }
    
    // Package management endpoints
    @GetMapping("/packages")
    public ResponseEntity<BaseResponse<?>> getAvailablePackages() {
        return ResponseEntity.ok(sellerService.getAvailablePackages());
    }
    
    @PostMapping("/packages/select")
    public ResponseEntity<BaseResponse<?>> selectPackage(@Valid @RequestBody SelectPackageRequest request, Authentication authentication) {
        User seller = authService.getCurrentUser(authentication);
        return ResponseEntity.ok(sellerService.selectPackage(request, seller));
    }
    
    @GetMapping("/listings/{listingId}/package")
    public ResponseEntity<BaseResponse<?>> getListingPackage(@PathVariable Long listingId, Authentication authentication) {
        User seller = authService.getCurrentUser(authentication);
        return ResponseEntity.ok(sellerService.getListingPackage(listingId, seller));
    }
    
    // Payment management endpoints
    @PostMapping("/payments")
    public ResponseEntity<BaseResponse<?>> processPackagePayment(@Valid @RequestBody PaymentRequest request, Authentication authentication) {
        User seller = authService.getCurrentUser(authentication);
        return ResponseEntity.ok(sellerService.processPackagePayment(request, seller));
    }
    
    @GetMapping("/payments")
    public ResponseEntity<BaseResponse<?>> getMyPayments(Authentication authentication) {
        User seller = authService.getCurrentUser(authentication);
        return ResponseEntity.ok(sellerService.getMyPayments(seller));
    }
    
    @GetMapping("/payments/{paymentId}")
    public ResponseEntity<BaseResponse<?>> getPaymentById(@PathVariable Long paymentId, Authentication authentication) {
        User seller = authService.getCurrentUser(authentication);
        return ResponseEntity.ok(sellerService.getPaymentById(paymentId, seller));
    }
}




