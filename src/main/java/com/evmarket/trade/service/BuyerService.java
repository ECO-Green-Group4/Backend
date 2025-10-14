package com.evmarket.trade.service;

import com.evmarket.trade.entity.*;
import com.evmarket.trade.request.OrderRequest;
import com.evmarket.trade.response.common.BaseResponse;

import java.util.List;

public interface BuyerService {
    
    // Browse listings
    BaseResponse<List<Listing>> getAllActiveListings();
    BaseResponse<List<Listing>> getListingsByType(String itemType);
    BaseResponse<List<Listing>> searchListings(String keyword);
    BaseResponse<List<Listing>> getListingsByLocation(String location);
    BaseResponse<Listing> getListingDetails(Long listingId);
    
    // Get item details (vehicle or battery)
    BaseResponse<Vehicle> getVehicleDetails(Long vehicleId);
    BaseResponse<Battery> getBatteryDetails(Long batteryId);
    
    // Order management
    BaseResponse<Order> createOrder(OrderRequest request, User buyer);
    BaseResponse<List<Order>> getMyOrders(User buyer);
    BaseResponse<Order> getOrderById(Long orderId, User buyer);
    BaseResponse<Void> cancelOrder(Long orderId, User buyer);
    
    // Contact information (only for battery purchases)
    BaseResponse<User> getSellerContactInfo(Long orderId, User buyer);
}




