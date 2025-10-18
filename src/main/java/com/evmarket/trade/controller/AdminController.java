package com.evmarket.trade.controller;

import com.evmarket.trade.entity.User;
import com.evmarket.trade.response.ListingResponse;
import com.evmarket.trade.response.OrderResponse;
import com.evmarket.trade.response.common.BaseResponse;
import com.evmarket.trade.service.AdminService;
import com.evmarket.trade.service.AuthService;
import com.evmarket.trade.request.CreateAddOnServiceRequest;
import com.evmarket.trade.request.UpdateAddOnServiceRequest;
import com.evmarket.trade.request.CreateServicePackageRequest;
import com.evmarket.trade.request.UpdateServicePackageRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*")
public class AdminController {
    
    @Autowired
    private AdminService adminService;
    
    @Autowired
    private AuthService authService;
    
    // ========== LISTING MANAGEMENT ==========
    
    /**
     * Xem tất cả listings trong hệ thống
     */
    @GetMapping("/listings")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BaseResponse<List<ListingResponse>>> getAllListings(Authentication authentication) {
        User admin = authService.getCurrentUser(authentication);
        return ResponseEntity.ok(adminService.getAllListings(admin));
    }
    
    /**
     * Xem tất cả orders trong hệ thống
     */
    @GetMapping("/orders")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BaseResponse<?>> getAllOrders(Authentication authentication) {
        User admin = authService.getCurrentUser(authentication);
        return ResponseEntity.ok(adminService.getAllOrders(admin));
    }
    
    /**
     * Gán staff cho order
     */
    @PutMapping("/orders/{orderId}/assign-staff")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BaseResponse<OrderResponse>> assignStaffToOrder(
            @PathVariable Long orderId,
            @RequestParam("staffId") Long staffId,
            Authentication authentication) {
        User admin = authService.getCurrentUser(authentication);
        return ResponseEntity.ok(adminService.assignStaffToOrder(orderId, staffId, admin));
    }
    
    /**
     * Set role cho user
     */
    @PutMapping("/users/{userId}/role")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BaseResponse<?>> setUserRole(
            @PathVariable Long userId,
            @RequestParam("role") String role,
            Authentication authentication) {
        User admin = authService.getCurrentUser(authentication);
        return ResponseEntity.ok(adminService.setUserRole(userId, role, admin));
    }
    
    /**
     * Xem tất cả users trong hệ thống
     */
    @GetMapping("/users")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BaseResponse<?>> getAllUsers(Authentication authentication) {
        User admin = authService.getCurrentUser(authentication);
        return ResponseEntity.ok(adminService.getAllUsers(admin));
    }
    
    /**
     * Set trạng thái listing (ACTIVE/REJECTED) bằng boolean
     */
    @PutMapping("/listings/{listingId}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BaseResponse<?>> setListingStatus(
            @PathVariable Long listingId,
            @RequestParam("approved") boolean approved,
            Authentication authentication) {
        User admin = authService.getCurrentUser(authentication);
        return ResponseEntity.ok(adminService.setListingStatus(listingId, approved, admin));
    }
    
    /**
     * Xem listings theo trạng thái
     */
    
    
    /**
     * Xem chi tiết listing
     */
    
    
    /**
     * Duyệt listing (chuyển status thành ACTIVE)
     */
    

    /**
     * Review listing bằng boolean approved=true/false
     * approved=true -> ACTIVE, approved=false -> REJECTED
     */
    
    
    /**
     * Từ chối listing (chuyển status thành REJECTED)
     */
    
    
    /**
     * Tạm dừng listing (chuyển status thành SUSPENDED)
     */
    
    
    /**
     * Xóa listing (chỉ admin mới được xóa)
     */
    @DeleteMapping("/listings/{listingId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BaseResponse<?>> deleteListing(
            @PathVariable Long listingId, 
            Authentication authentication) {
        User admin = authService.getCurrentUser(authentication);
        return ResponseEntity.ok(adminService.deleteListing(listingId, admin));
    }
    
    // ========== USER MANAGEMENT ==========
    
    /**
     * Khóa/mở khóa user
     */
    @PutMapping("/users/{userId}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BaseResponse<?>> updateUserStatus(
            @PathVariable Long userId, 
            @RequestParam String status,
            Authentication authentication) {
        User admin = authService.getCurrentUser(authentication);
        return ResponseEntity.ok(adminService.updateUserStatus(userId, status, admin));
    }
    
    // ========== ADMIN ACTIONS LOG ==========
    
    /**
     * Xem log các hành động của admin
     */
    @GetMapping("/actions")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BaseResponse<?>> getAdminActions(Authentication authentication) {
        User admin = authService.getCurrentUser(authentication);
        return ResponseEntity.ok(adminService.getAdminActions(admin));
    }
    
    /**
     * Xem thống kê hệ thống
     */
    @GetMapping("/statistics")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BaseResponse<?>> getSystemStatistics(Authentication authentication) {
        User admin = authService.getCurrentUser(authentication);
        return ResponseEntity.ok(adminService.getSystemStatistics(admin));
    }

    // ========== ADD-ON SERVICE MANAGEMENT ==========
    @PostMapping("/addon/services")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BaseResponse<?>> createAddOnService(@RequestBody CreateAddOnServiceRequest request, Authentication authentication) {
        User admin = authService.getCurrentUser(authentication);
        return ResponseEntity.ok(adminService.createAddOnService(request, admin));
    }

    @PutMapping("/addon/services/{serviceId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BaseResponse<?>> updateAddOnService(@PathVariable Long serviceId,
                                                              @RequestBody UpdateAddOnServiceRequest request,
                                                              Authentication authentication) {
        User admin = authService.getCurrentUser(authentication);
        return ResponseEntity.ok(adminService.updateAddOnService(serviceId, request, admin));
    }

    @PatchMapping("/addon/services/{serviceId}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BaseResponse<?>> changeAddOnServiceStatus(@PathVariable Long serviceId,
                                                                    @RequestParam String value,
                                                                    Authentication authentication) {
        User admin = authService.getCurrentUser(authentication);
        return ResponseEntity.ok(adminService.changeAddOnServiceStatus(serviceId, value, admin));
    }

    @GetMapping("/addon/services")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BaseResponse<?>> getAllAddOnServices(Authentication authentication) {
        User admin = authService.getCurrentUser(authentication);
        return ResponseEntity.ok(adminService.getAllAddOnServices(admin));
    }

    @DeleteMapping("/addon/services/{serviceId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BaseResponse<?>> deleteAddOnService(@PathVariable Long serviceId,
                                                              Authentication authentication) {
        User admin = authService.getCurrentUser(authentication);
        return ResponseEntity.ok(adminService.deleteAddOnService(serviceId, admin));
    }

    // Service Package (membership) CRUD
    @PostMapping("/memberships")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BaseResponse<?>> createServicePackage(@RequestBody CreateServicePackageRequest request,
                                                                Authentication authentication) {
        User admin = authService.getCurrentUser(authentication);
        return ResponseEntity.ok(adminService.createServicePackage(request, admin));
    }

    @PutMapping("/memberships/{packageId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BaseResponse<?>> updateServicePackage(@PathVariable Long packageId,
                                                                @RequestBody UpdateServicePackageRequest request,
                                                                Authentication authentication) {
        User admin = authService.getCurrentUser(authentication);
        return ResponseEntity.ok(adminService.updateServicePackage(packageId, request, admin));
    }

    @GetMapping("/memberships")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BaseResponse<?>> getAllServicePackages(Authentication authentication) {
        User admin = authService.getCurrentUser(authentication);
        return ResponseEntity.ok(adminService.getAllServicePackages(admin));
    }

    @DeleteMapping("/memberships/{packageId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BaseResponse<?>> deleteServicePackage(@PathVariable Long packageId,
                                                                Authentication authentication) {
        User admin = authService.getCurrentUser(authentication);
        return ResponseEntity.ok(adminService.deleteServicePackage(packageId, admin));
    }

}

