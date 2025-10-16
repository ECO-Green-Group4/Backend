package com.evmarket.trade.service;

import com.evmarket.trade.entity.Listing;
import com.evmarket.trade.entity.User;
import com.evmarket.trade.request.CreateVehicleListingRequest;
import com.evmarket.trade.request.CreateBatteryListingRequest;
import com.evmarket.trade.response.ListingResponse;

import java.util.List;

public interface ListingService {
    // Vehicle listing methods
    Listing createVehicleListing(CreateVehicleListingRequest request, User user);
    
    // Battery listing methods
    Listing createBatteryListing(CreateBatteryListingRequest request, User user);
    
    // General listing methods
    List<ListingResponse> getListingsByUser(User user);
    List<ListingResponse> getAllAvailableListings();
    List<ListingResponse> getAllListings(); // Admin method - get all listings regardless of status
    List<ListingResponse> getListingsByItemType(String itemType);
    ListingResponse getListingById(Long listingId);
    Listing updateListingStatus(Long listingId, String status);
}
