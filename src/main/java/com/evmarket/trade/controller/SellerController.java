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
}




