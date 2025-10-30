package com.evmarket.trade.service;

import com.evmarket.trade.entity.User;
import com.evmarket.trade.response.ListingResponse;
import com.evmarket.trade.response.OrderResponse;
import com.evmarket.trade.response.common.BaseResponse;
import com.evmarket.trade.request.CreateAddOnServiceRequest;
import com.evmarket.trade.request.UpdateAddOnServiceRequest;
import com.evmarket.trade.request.CreateServicePackageRequest;
import com.evmarket.trade.request.UpdateServicePackageRequest;

import java.util.List;

public interface AdminService {

    // ========== LISTING MANAGEMENT ==========

    /**
     * Lấy tất cả listings trong hệ thống
     */
    BaseResponse<List<ListingResponse>> getAllListings(User admin);

    /**
     * Lấy tất cả orders trong hệ thống
     */
    BaseResponse<java.util.List<OrderResponse>> getAllOrders(User admin);

    /**
     * Gán staff cho order
     */
    BaseResponse<OrderResponse> assignStaffToOrder(Long orderId, Long staffId, User admin);

    /**
     * Set role cho user
     */
    BaseResponse<?> setUserRole(Long userId, String role, User admin);

    /**
     * Lấy tất cả users trong hệ thống
     */
    BaseResponse<?> getAllUsers(User admin);

    /**
     * Lấy listings theo trạng thái
     */
    BaseResponse<List<ListingResponse>> getListingsByStatus(String status, User admin);

    /**
     * Lấy chi tiết listing
     */
    BaseResponse<ListingResponse> getListingById(Long listingId, User admin);

    /**
     * Duyệt listing
     */
    BaseResponse<?> approveListing(Long listingId, User admin);

    /**
     * Từ chối listing
     */
    BaseResponse<?> rejectListing(Long listingId, String reason, User admin);

    /**
     * Tạm dừng listing
     */
    BaseResponse<?> suspendListing(Long listingId, String reason, User admin);

    /**
     * Xóa listing
     */
    BaseResponse<?> deleteListing(Long listingId, User admin);

    /**
     * Review listing bằng boolean approved=true/false
     */
    BaseResponse<?> reviewListing(Long listingId, boolean approved, User admin);

    /**
     * Set trạng thái listing (ACTIVE/REJECTED) bằng boolean
     */
    BaseResponse<?> setListingStatus(Long listingId, boolean approved, User admin);

    // ========== USER MANAGEMENT ==========

    /**
     * Cập nhật trạng thái user
     */
    BaseResponse<?> updateUserStatus(Long userId, String status, User admin);

    // ========== ADMIN ACTIONS LOG ==========

    /**
     * Lấy log admin actions
     */
    BaseResponse<?> getAdminActions(User admin);

    /**
     * Lấy thống kê hệ thống
     */
    BaseResponse<?> getSystemStatistics(User admin);

    // Add-on service management
    BaseResponse<?> createAddOnService(CreateAddOnServiceRequest request, User admin);
    BaseResponse<?> updateAddOnService(Long serviceId, UpdateAddOnServiceRequest request, User admin);
    BaseResponse<?> changeAddOnServiceStatus(Long serviceId, String status, User admin);
    BaseResponse<?> setAddOnServiceStatus(Long serviceId, boolean active, User admin);
    BaseResponse<?> getAllAddOnServices(User admin);
    BaseResponse<?> deleteAddOnService(Long serviceId, User admin);

    // Service Package management
    BaseResponse<?> createServicePackage(CreateServicePackageRequest request, User admin);
    BaseResponse<?> updateServicePackage(Long packageId, UpdateServicePackageRequest request, User admin);
    BaseResponse<?> getAllServicePackages(User admin);
    BaseResponse<?> deleteServicePackage(Long packageId, User admin);

}

