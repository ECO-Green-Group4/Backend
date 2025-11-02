package com.evmarket.trade.service;

import com.evmarket.trade.entity.Listing;
import com.evmarket.trade.entity.User;
import com.evmarket.trade.request.CreateVehicleListingRequest;
import com.evmarket.trade.request.CreateBatteryListingRequest;
import com.evmarket.trade.response.ListingResponse;

import java.util.List;

public interface ListingService {
    // Vehicle listing methods
    ListingResponse createVehicleListing(CreateVehicleListingRequest request, User user);
    
    // Battery listing methods
    ListingResponse createBatteryListing(CreateBatteryListingRequest request, User user);
    
    // General listing methods
    List<ListingResponse> getListingsByUser(User user);
    List<ListingResponse> getAllAvailableListings();
    List<ListingResponse> getAllListings(); // Admin method - get all listings regardless of status
    List<ListingResponse> getAllListingsWithPhone(); // Get all listings with phone number (for admin)
    List<ListingResponse> getListingsByItemType(String itemType);
    List<ListingResponse> getBatteryListingsWithPhone(); // Get battery listings with phone number
    ListingResponse getListingById(Long listingId);
    ListingResponse updateListingStatus(Long listingId, String status);
    
}
