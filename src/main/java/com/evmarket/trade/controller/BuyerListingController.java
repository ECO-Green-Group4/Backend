package com.evmarket.trade.controller;

import com.evmarket.trade.response.common.BaseResponse;
import com.evmarket.trade.response.ListingResponse;
import com.evmarket.trade.service.ListingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/buyer")
@CrossOrigin(origins = "*")
public class BuyerListingController {
    
    @Autowired
    private ListingService listingService;
    
    // Browse listings
    @GetMapping("/listings")
    public ResponseEntity<BaseResponse<List<ListingResponse>>> getAllListings() {
        return ResponseEntity.ok(BaseResponse.success(listingService.getAllAvailableListings(), "Listings retrieved successfully"));
    }
    
}
