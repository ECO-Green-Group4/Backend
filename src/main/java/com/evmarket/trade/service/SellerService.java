package com.evmarket.trade.service;

import com.evmarket.trade.entity.*;
import com.evmarket.trade.request.*;
import com.evmarket.trade.response.common.BaseResponse;
import com.evmarket.trade.response.VehicleResponse;
import com.evmarket.trade.response.BatteryResponse;

import java.util.List;

public interface SellerService {
    
    // Vehicle management
    BaseResponse<VehicleResponse> createVehicle(VehicleRequest request, User seller);
    BaseResponse<VehicleResponse> updateVehicle(Long vehicleId, VehicleRequest request, User seller);
    BaseResponse<Void> deleteVehicle(Long vehicleId, User seller);
    BaseResponse<List<VehicleResponse>> getMyVehicles(User seller);
    BaseResponse<VehicleResponse> getVehicleById(Long vehicleId, User seller);
    
    // Battery management
    BaseResponse<BatteryResponse> createBattery(BatteryRequest request, User seller);
    BaseResponse<BatteryResponse> updateBattery(Long batteryId, BatteryRequest request, User seller);
    BaseResponse<Void> deleteBattery(Long batteryId, User seller);
    BaseResponse<List<BatteryResponse>> getMyBatteries(User seller);
    BaseResponse<BatteryResponse> getBatteryById(Long batteryId, User seller);
    
    // Listing management - using new separate APIs
    BaseResponse<Listing> updateListing(Long listingId, ListingRequest request, User seller);
    BaseResponse<Void> deleteListing(Long listingId, User seller);
    BaseResponse<List<Listing>> getMyListings(User seller);
    BaseResponse<Listing> getListingById(Long listingId, User seller);
    
    // Package management
    BaseResponse<List<ServicePackage>> getAvailablePackages();
    BaseResponse<ListingPackage> selectPackage(SelectPackageRequest request, User seller);
    BaseResponse<ListingPackage> getListingPackage(Long listingId, User seller);
    
    // Payment management
    BaseResponse<Payment> processPackagePayment(PaymentRequest request, User seller);
    BaseResponse<List<Payment>> getMyPayments(User seller);
    BaseResponse<Payment> getPaymentById(Long paymentId, User seller);
    
    // Order management
    BaseResponse<Void> confirmOrder(Long orderId, User seller);
    BaseResponse<Void> rejectOrder(Long orderId, User seller);
    BaseResponse<List<Order>> getOrdersForMyListings(User seller);
}
