package com.evmarket.trade.controller;

import com.evmarket.trade.entity.User;
import com.evmarket.trade.request.CreateVehicleListingRequest;
import com.evmarket.trade.request.CreateBatteryListingRequest;
import com.evmarket.trade.response.common.BaseResponse;
import com.evmarket.trade.response.ListingResponse;
import com.evmarket.trade.service.ListingService;
import com.evmarket.trade.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/seller")
@CrossOrigin(origins = "*")
public class ListingController {
    
    @Autowired
    private ListingService listingService;
    
    @Autowired
    private AuthService authService;
    
    // Vehicle listing endpoints
    @PostMapping("/listings/vehicle")
    public ResponseEntity<BaseResponse<?>> createVehicleListing(@Valid @RequestBody CreateVehicleListingRequest request, Authentication authentication) {
        User user = authService.getCurrentUser(authentication);
        return ResponseEntity.ok(BaseResponse.success(listingService.createVehicleListing(request, user), "Vehicle listing created successfully"));
    }
    
    // Battery listing endpoints
    @PostMapping("/listings/battery")
    public ResponseEntity<BaseResponse<?>> createBatteryListing(@Valid @RequestBody CreateBatteryListingRequest request, Authentication authentication) {
        User user = authService.getCurrentUser(authentication);
        return ResponseEntity.ok(BaseResponse.success(listingService.createBatteryListing(request, user), "Battery listing created successfully"));
    }
    
    // General listing endpoints
    @GetMapping("/listings/{listingId}")
    public ResponseEntity<BaseResponse<ListingResponse>> getListingById(@PathVariable Long listingId, Authentication authentication) {
        User user = authService.getCurrentUser(authentication);
        return ResponseEntity.ok(BaseResponse.success(listingService.getListingById(listingId), "Listing retrieved successfully"));
    }
    
    @PutMapping("/listings/{listingId}/status")
    public ResponseEntity<BaseResponse<?>> updateListingStatus(@PathVariable Long listingId, @RequestParam String status, Authentication authentication) {
        User user = authService.getCurrentUser(authentication);
        return ResponseEntity.ok(BaseResponse.success(listingService.updateListingStatus(listingId, status), "Listing status updated successfully"));
    }

    // Lấy toàn bộ vehicle listings (cho người bán hoặc public)
    @GetMapping("/listings/vehicle")
    public ResponseEntity<BaseResponse<List<ListingResponse>>> getAllVehicleListings(Authentication authentication) {
        User user = authService.getCurrentUser(authentication);
        return ResponseEntity.ok(
                BaseResponse.success(listingService.getListingsByItemType("VEHICLE"), "Vehicle listings retrieved successfully")
        );
    }

    // Lấy toàn bộ battery listings
    @GetMapping("/listings/battery")
    public ResponseEntity<BaseResponse<List<ListingResponse>>> getAllBatteryListings(Authentication authentication) {
        User user = authService.getCurrentUser(authentication);
        return ResponseEntity.ok(
                BaseResponse.success(listingService.getListingsByItemType("BATTERY"), "Battery listings retrieved successfully")
        );
    }
}
