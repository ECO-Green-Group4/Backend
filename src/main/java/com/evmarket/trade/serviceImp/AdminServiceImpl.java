package com.evmarket.trade.serviceImp;

import com.evmarket.trade.entity.*;
import com.evmarket.trade.exception.AppException;
import com.evmarket.trade.repository.*;
import com.evmarket.trade.response.ListingResponse;
import com.evmarket.trade.response.OrderResponse;
import com.evmarket.trade.response.UserInfoResponse;
import com.evmarket.trade.response.common.BaseResponse;
import com.evmarket.trade.service.AdminService;
import com.evmarket.trade.service.ListingService;
import com.evmarket.trade.request.CreateAddOnServiceRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
public class AdminServiceImpl implements AdminService {

    @Autowired
    private ListingRepository listingRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AdminActionRepository adminActionRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private ListingService listingService;
    @Autowired
    private AddOnServiceRepository addOnServiceRepository;
    @Autowired
    private ServicePackageRepository servicePackageRepository;
    
    @Autowired
    private ListingPackageRepository listingPackageRepository;

    // ========== LISTING MANAGEMENT ==========

    @Override
    @Transactional(readOnly = true)
    public BaseResponse<List<ListingResponse>> getAllListings(User admin) {
        try {
            // Admin cần thấy TẤT CẢ listings với số điện thoại của người đăng
            List<ListingResponse> responses = listingService.getAllListingsWithPhone();

            logAdminAction(admin, "LISTING", null, "VIEW_ALL_LISTINGS");
            return BaseResponse.success(responses, "Tất cả listings được lấy thành công");
        } catch (Exception e) {
            throw new AppException("Lỗi khi lấy danh sách listings: " + e.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public BaseResponse<List<OrderResponse>> getAllOrders(User admin) {
        try {
            List<Order> orders = orderRepository.findAll();
            List<OrderResponse> responses = orders.stream()
                    .map(this::convertOrderToResponse)
                    .collect(Collectors.toList());
            logAdminAction(admin, "ORDER", null, "VIEW_ALL_ORDERS");
            return BaseResponse.success(responses, "Tất cả orders được lấy thành công");
        } catch (Exception e) {
            throw new AppException("Lỗi khi lấy danh sách orders: " + e.getMessage());
        }
    }

    @Override
    public BaseResponse<OrderResponse> assignStaffToOrder(Long orderId, Long staffId, User admin) {
        try {
            Order order = orderRepository.findById(orderId)
                    .orElseThrow(() -> new AppException("Không tìm thấy order với ID: " + orderId));

            // Kiểm tra staff có tồn tại không
            User staff = userRepository.findById(staffId)
                    .orElseThrow(() -> new AppException("Không tìm thấy staff với ID: " + staffId));

            // Kiểm tra user có phải staff không (có thể thêm role check)
            if (!"STAFF".equals(staff.getRole())) {
                throw new AppException("User không phải là staff");
            }

            order.setAssignedStaffId(staffId);
            orderRepository.save(order);

            logAdminAction(admin, "ORDER", orderId, "ASSIGN_STAFF");

            // Convert to DTO
            OrderResponse orderResponse = convertOrderToResponse(order);
            return BaseResponse.success(orderResponse, "Staff đã được gán cho order thành công");
        } catch (Exception e) {
            throw new AppException("Lỗi khi gán staff cho order: " + e.getMessage());
        }
    }

    @Override
    public BaseResponse<?> setUserRole(Long userId, String role, User admin) {
        try {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new AppException("Không tìm thấy user với ID: " + userId));

            // Validate role
            if (!isValidRole(role)) {
                throw new AppException("Role không hợp lệ. Các role hợp lệ: USER, STAFF, ADMIN");
            }

            // Không cho phép admin tự đổi role của mình
            if (user.getUserId() == admin.getUserId()) {
                throw new AppException("Không thể thay đổi role của chính mình");
            }

            String oldRole = user.getRole();
            user.setRole(role.toUpperCase());
            userRepository.save(user);

            logAdminAction(admin, "USER", userId, "SET_ROLE");
            return BaseResponse.success(user, String.format("Role của user đã được thay đổi từ %s thành %s", oldRole, role.toUpperCase()));
        } catch (Exception e) {
            throw new AppException("Lỗi khi set role cho user: " + e.getMessage());
        }
    }

    private boolean isValidRole(String role) {
        return role != null && (role.equalsIgnoreCase("USER") ||
                role.equalsIgnoreCase("STAFF") ||
                role.equalsIgnoreCase("ADMIN"));
    }

    @Override
    @Transactional(readOnly = true)
    public BaseResponse<?> getAllUsers(User admin) {
        try {
            List<User> users = userRepository.findAll();
            logAdminAction(admin, "USER", null, "VIEW_ALL_USERS");
            return BaseResponse.success(users, "Tất cả users được lấy thành công");
        } catch (Exception e) {
            throw new AppException("Lỗi khi lấy danh sách users: " + e.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public BaseResponse<List<ListingResponse>> getListingsByStatus(String status, User admin) {
        try {
            List<Listing> listings = listingRepository.findByStatus(status);
            List<ListingResponse> responses = listings.stream()
                    .map(listing -> listingService.getListingById(listing.getListingId()))
                    .collect(Collectors.toList());

            logAdminAction(admin, "LISTING", null, "VIEW_LISTINGS_BY_STATUS");
            return BaseResponse.success(responses, "Listings theo trạng thái " + status + " được lấy thành công");
        } catch (Exception e) {
            throw new AppException("Lỗi khi lấy listings theo trạng thái: " + e.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public BaseResponse<ListingResponse> getListingById(Long listingId, User admin) {
        try {
            ListingResponse response = listingService.getListingById(listingId);
            logAdminAction(admin, "LISTING", listingId, "VIEW_LISTING_DETAIL");
            return BaseResponse.success(response, "Chi tiết listing được lấy thành công");
        } catch (Exception e) {
            throw new AppException("Lỗi khi lấy chi tiết listing: " + e.getMessage());
        }
    }

    @Override
    public BaseResponse<?> approveListing(Long listingId, User admin) {
        try {
            Listing listing = listingRepository.findById(listingId)
                    .orElseThrow(() -> new AppException("Không tìm thấy listing với ID: " + listingId));

            if ("ACTIVE".equals(listing.getStatus())) {
                return BaseResponse.error("Listing đã được duyệt rồi");
            }

            // Kiểm tra thanh toán nếu listing có sử dụng package
            if (!validateListingPayment(listing)) {
                return BaseResponse.error("Không thể duyệt listing: Seller chưa thanh toán cho gói dịch vụ đã chọn");
            }

            listing.setStatus("ACTIVE");
            listing.setUpdatedAt(LocalDateTime.now());
            listingRepository.save(listing);

            logAdminAction(admin, "LISTING", listingId, "APPROVE_LISTING");
            // Trả về DTO để tránh lỗi serialize entity (LazyInitialization/infinite recursion)
            ListingResponse dto = listingService.getListingById(listingId);
            return BaseResponse.success(dto, "Listing đã được duyệt thành công");
        } catch (Exception e) {
            throw new AppException("Lỗi khi duyệt listing: " + e.getMessage());
        }
    }

    @Override
    public BaseResponse<?> rejectListing(Long listingId, String reason, User admin) {
        try {
            Listing listing = listingRepository.findById(listingId)
                    .orElseThrow(() -> new AppException("Không tìm thấy listing với ID: " + listingId));

            listing.setStatus("REJECTED");
            listing.setUpdatedAt(LocalDateTime.now());
            listingRepository.save(listing);

            logAdminAction(admin, "LISTING", listingId, "REJECT_LISTING");
            ListingResponse dto = listingService.getListingById(listingId);
            return BaseResponse.success(dto, "Listing đã bị từ chối thành công");
        } catch (Exception e) {
            throw new AppException("Lỗi khi từ chối listing: " + e.getMessage());
        }
    }

    @Override
    public BaseResponse<?> suspendListing(Long listingId, String reason, User admin) {
        try {
            Listing listing = listingRepository.findById(listingId)
                    .orElseThrow(() -> new AppException("Không tìm thấy listing với ID: " + listingId));

            listing.setStatus("SUSPENDED");
            listing.setUpdatedAt(LocalDateTime.now());
            listingRepository.save(listing);

            logAdminAction(admin, "LISTING", listingId, "SUSPEND_LISTING");
            ListingResponse dto = listingService.getListingById(listingId);
            return BaseResponse.success(dto, "Listing đã bị tạm dừng thành công");
        } catch (Exception e) {
            throw new AppException("Lỗi khi tạm dừng listing: " + e.getMessage());
        }
    }

    @Override
    public BaseResponse<?> deleteListing(Long listingId, User admin) {
        try {
            Listing listing = listingRepository.findById(listingId)
                    .orElseThrow(() -> new AppException("Không tìm thấy listing với ID: " + listingId));

            // Log trước khi xóa
            logAdminAction(admin, "LISTING", listingId, "DELETE_LISTING");

            listingRepository.delete(listing);
            return BaseResponse.success(null, "Listing đã được xóa thành công");
        } catch (Exception e) {
            throw new AppException("Lỗi khi xóa listing: " + e.getMessage());
        }
    }

    @Override
    public BaseResponse<?> reviewListing(Long listingId, boolean approved, User admin) {
        try {
            Listing listing = listingRepository.findById(listingId)
                    .orElseThrow(() -> new AppException("Không tìm thấy listing với ID: " + listingId));

            // Nếu đang duyệt (approved = true), kiểm tra thanh toán
            if (approved && !validateListingPayment(listing)) {
                return BaseResponse.error("Không thể duyệt listing: Seller chưa thanh toán cho gói dịch vụ đã chọn");
            }

            String newStatus = approved ? "ACTIVE" : "REJECTED";
            listing.setStatus(newStatus);
            listing.setUpdatedAt(LocalDateTime.now());
            listingRepository.save(listing);

            logAdminAction(admin, "LISTING", listingId, approved ? "APPROVE_LISTING" : "REJECT_LISTING");
            ListingResponse dto = listingService.getListingById(listingId);
            return BaseResponse.success(dto, approved ? "Listing đã được duyệt" : "Listing đã bị từ chối");
        } catch (Exception e) {
            throw new AppException("Lỗi khi review listing: " + e.getMessage());
        }
    }

    @Override
    public BaseResponse<?> setListingStatus(Long listingId, boolean approved, User admin) {
        try {
            Listing listing = listingRepository.findById(listingId)
                    .orElseThrow(() -> new AppException("Không tìm thấy listing với ID: " + listingId));

            // Nếu đang set ACTIVE (approved = true), kiểm tra thanh toán
            if (approved && !validateListingPayment(listing)) {
                return BaseResponse.error("Không thể set ACTIVE: Seller chưa thanh toán cho gói dịch vụ đã chọn");
            }

            String newStatus = approved ? "ACTIVE" : "REJECTED";
            listing.setStatus(newStatus);
            listing.setUpdatedAt(LocalDateTime.now());
            listingRepository.save(listing);

            logAdminAction(admin, "LISTING", listingId, approved ? "SET_ACTIVE" : "SET_REJECTED");
            ListingResponse dto = listingService.getListingById(listingId);
            return BaseResponse.success(dto, approved ? "Listing đã được set ACTIVE" : "Listing đã được set REJECTED");
        } catch (Exception e) {
            throw new AppException("Lỗi khi set trạng thái listing: " + e.getMessage());
        }
    }

    // ========== PAYMENT VALIDATION ==========
    
    /**
     * Kiểm tra xem listing có packageId và đã thanh toán chưa
     * @param listing Listing cần kiểm tra
     * @return true nếu không có packageId hoặc đã thanh toán, false nếu có packageId nhưng chưa thanh toán
     */
    private boolean validateListingPayment(Listing listing) {
        // Tìm ListingPackage liên kết với listing này
        List<ListingPackage> listingPackages = listingPackageRepository.findByListing(listing);
        
        // Nếu không có ListingPackage nào, có nghĩa là listing không sử dụng package
        if (listingPackages.isEmpty()) {
            return true;
        }
        
        // Kiểm tra xem có ListingPackage nào đã được thanh toán thành công không
        for (ListingPackage listingPackage : listingPackages) {
            // Tìm payment thành công cho ListingPackage này
            List<Payment> payments = paymentRepository.findByListingPackageIdAndPaymentStatus(
                listingPackage.getListingPackageId(), "SUCCESS");
            
            if (!payments.isEmpty()) {
                // Có ít nhất 1 payment thành công
                return true;
            }
        }
        
        // Có ListingPackage nhưng chưa có payment thành công nào
        return false;
    }

    // ========== USER MANAGEMENT ==========

    @Override
    public BaseResponse<?> updateUserStatus(Long userId, String status, User admin) {
        try {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new AppException("Không tìm thấy user với ID: " + userId));

            String oldStatus = user.getStatus();
            user.setStatus(status);
            userRepository.save(user);

            logAdminAction(admin, "USER", userId, "UPDATE_USER_STATUS");
            return BaseResponse.success(user, "Trạng thái user đã được cập nhật từ " + oldStatus + " thành " + status);
        } catch (Exception e) {
            throw new AppException("Lỗi khi cập nhật trạng thái user: " + e.getMessage());
        }
    }

    // ========== ADMIN ACTIONS LOG ==========

    @Override
    @Transactional(readOnly = true)
    public BaseResponse<?> getAdminActions(User admin) {
        try {
            List<AdminAction> actions = adminActionRepository.findByAdminOrderByCreatedAtDesc(admin);
            logAdminAction(admin, "ADMIN_ACTION", null, "VIEW_ADMIN_ACTIONS");
            return BaseResponse.success(actions, "Log admin actions được lấy thành công");
        } catch (Exception e) {
            throw new AppException("Lỗi khi lấy log admin actions: " + e.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public BaseResponse<?> getSystemStatistics(User admin) {
        try {
            Map<String, Object> statistics = new HashMap<>();

            // Thống kê listings
            statistics.put("totalListings", listingRepository.count());
            statistics.put("activeListings", listingRepository.findByStatus("ACTIVE").size());
            statistics.put("pendingListings", listingRepository.findByStatus("PENDING").size());
            statistics.put("rejectedListings", listingRepository.findByStatus("REJECTED").size());
            statistics.put("suspendedListings", listingRepository.findByStatus("SUSPENDED").size());

            // Thống kê users
            statistics.put("totalUsers", userRepository.count());
            statistics.put("activeUsers", userRepository.findByStatus("ACTIVE").size());
            statistics.put("inactiveUsers", userRepository.findByStatus("INACTIVE").size());

            // Thống kê orders
            statistics.put("totalOrders", orderRepository.count());

            // Thống kê payments
            statistics.put("totalPayments", paymentRepository.count());

            logAdminAction(admin, "STATISTICS", null, "VIEW_SYSTEM_STATISTICS");
            return BaseResponse.success(statistics, "Thống kê hệ thống được lấy thành công");
        } catch (Exception e) {
            throw new AppException("Lỗi khi lấy thống kê hệ thống: " + e.getMessage());
        }
    }

    // ========== HELPER METHODS ==========

    private OrderResponse convertOrderToResponse(Order order) {
        return OrderResponse.builder()
                .orderId(order.getOrderId())
                .listingId(order.getListing() != null ? order.getListing().getListingId() : null)
                .buyer(convertUserToUserInfo(order.getBuyer()))
                .seller(convertUserToUserInfo(order.getSeller()))
                .status(order.getStatus())
                .orderDate(order.getOrderDate())
                .assignedStaffId(order.getAssignedStaffId())
                .build();
    }

    private UserInfoResponse convertUserToUserInfo(User user) {
        if (user == null) return null;

        return UserInfoResponse.builder()
                .userId((long) user.getUserId())
                .username(user.getUsername())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .dateOfBirth(user.getDateOfBirth() != null ? user.getDateOfBirth().toString() : null)
                .gender(user.getGender())
                .identityCard(user.getIdentityCard())
                .address(user.getAddress())
                .phone(user.getPhone())
                .status(user.getStatus())
                .createdAt(user.getCreatedAt())
                .build();
    }

    private void logAdminAction(User admin, String targetType, Long targetId, String action) {
        try {
            AdminAction adminAction = new AdminAction();
            adminAction.setAdmin(admin);
            adminAction.setTargetType(targetType);
            adminAction.setTargetId(targetId);
            adminAction.setAction(action);
            adminAction.setCreatedAt(LocalDateTime.now());
            adminActionRepository.save(adminAction);
        } catch (Exception e) {
            // Log lỗi nhưng không throw exception để không ảnh hưởng đến business logic
            System.err.println("Lỗi khi log admin action: " + e.getMessage());
        }
    }

    // ========== ADD-ON SERVICE MANAGEMENT ==========
    @Override
    public BaseResponse<?> createAddOnService(CreateAddOnServiceRequest request, User admin) {
        try {
            AddOnService service = new AddOnService();
            service.setName(request.getName());
            service.setDescription(request.getDescription());
            service.setDefaultFee(request.getFee());
            service.setStatus("ACTIVE"); // Automatically set status to ACTIVE
            AddOnService saved = addOnServiceRepository.save(service);
            logAdminAction(admin, "ADDON_SERVICE", saved.getServiceId(), "CREATE_ADDON_SERVICE");
            return BaseResponse.success(saved, "Add-on service created successfully");
        } catch (Exception e) {
            throw new AppException("Failed to create add-on service: " + e.getMessage());
        }
    }

    @Override
    public BaseResponse<?> updateAddOnService(Long serviceId, com.evmarket.trade.request.UpdateAddOnServiceRequest request, User admin) {
        try {
            AddOnService service = addOnServiceRepository.findById(serviceId)
                    .orElseThrow(() -> new AppException("Add-on service not found"));
            if (request.getName() != null) service.setName(request.getName());
            if (request.getDescription() != null) service.setDescription(request.getDescription());
            if (request.getFee() != null) service.setDefaultFee(request.getFee());
            AddOnService saved = addOnServiceRepository.save(service);
            logAdminAction(admin, "ADDON_SERVICE", saved.getServiceId(), "UPDATE_ADDON_SERVICE");
            return BaseResponse.success(saved, "Add-on service updated successfully");
        } catch (Exception e) {
            throw new AppException("Failed to update add-on service: " + e.getMessage());
        }
    }

    @Override
    public BaseResponse<?> changeAddOnServiceStatus(Long serviceId, String status, User admin) {
        try {
            AddOnService service = addOnServiceRepository.findById(serviceId)
                    .orElseThrow(() -> new AppException("Add-on service not found"));
            service.setStatus(status);
            AddOnService saved = addOnServiceRepository.save(service);
            logAdminAction(admin, "ADDON_SERVICE", saved.getServiceId(), "CHANGE_ADDON_SERVICE_STATUS");
            return BaseResponse.success(saved, "Add-on service status updated successfully");
        } catch (Exception e) {
            throw new AppException("Failed to change add-on service status: " + e.getMessage());
        }
    }

    @Override
    public BaseResponse<?> getAllAddOnServices(User admin) {
        try {
            List<AddOnService> services = addOnServiceRepository.findAll();
            logAdminAction(admin, "ADDON_SERVICE", null, "LIST_ADDON_SERVICES");
            return BaseResponse.success(services, "All add-on services retrieved successfully");
        } catch (Exception e) {
            throw new AppException("Failed to list add-on services: " + e.getMessage());
        }
    }

    @Override
    public BaseResponse<?> deleteAddOnService(Long serviceId, User admin) {
        try {
            AddOnService service = addOnServiceRepository.findById(serviceId)
                    .orElseThrow(() -> new AppException("Add-on service not found"));
            addOnServiceRepository.delete(service);
            logAdminAction(admin, "ADDON_SERVICE", serviceId, "DELETE_ADDON_SERVICE");
            return BaseResponse.success(null, "Add-on service deleted successfully");
        } catch (Exception e) {
            throw new AppException("Failed to delete add-on service: " + e.getMessage());
        }
    }

    @Override
    public BaseResponse<?> createServicePackage(com.evmarket.trade.request.CreateServicePackageRequest request, User admin) {
        try {
            ServicePackage sp = new ServicePackage();
            sp.setName(request.getName());
            sp.setPackageType(ServicePackage.PackageType.valueOf(request.getPackageType()));
            sp.setListingLimit(request.getListingLimit());
            sp.setListingFee(request.getListingFee());
            sp.setHighlight(request.getHighlight());
            sp.setDurationDays(request.getDurationDays());
            sp.setCommissionDiscount(request.getCommissionDiscount());
            sp.setStatus(request.getStatus());
            ServicePackage saved = servicePackageRepository.save(sp);
            logAdminAction(admin, "SERVICE_PACKAGE", saved.getPackageId(), "CREATE_SERVICE_PACKAGE");
            return BaseResponse.success(saved, "Service package created successfully");
        } catch (Exception e) {
            throw new AppException("Failed to create service package: " + e.getMessage());
        }
    }

    @Override
    public BaseResponse<?> updateServicePackage(Long packageId, com.evmarket.trade.request.UpdateServicePackageRequest request, User admin) {
        try {
            ServicePackage sp = servicePackageRepository.findById(packageId)
                    .orElseThrow(() -> new AppException("Service package not found"));
            if (request.getName() != null) sp.setName(request.getName());
            if (request.getPackageType() != null) sp.setPackageType(ServicePackage.PackageType.valueOf(request.getPackageType()));
            if (request.getListingLimit() != null) sp.setListingLimit(request.getListingLimit());
            if (request.getListingFee() != null) sp.setListingFee(request.getListingFee());
            if (request.getHighlight() != null) sp.setHighlight(request.getHighlight());
            if (request.getDurationDays() != null) sp.setDurationDays(request.getDurationDays());
            if (request.getCommissionDiscount() != null) sp.setCommissionDiscount(request.getCommissionDiscount());
            if (request.getStatus() != null) sp.setStatus(request.getStatus());
            ServicePackage saved = servicePackageRepository.save(sp);
            logAdminAction(admin, "SERVICE_PACKAGE", saved.getPackageId(), "UPDATE_SERVICE_PACKAGE");
            return BaseResponse.success(saved, "Service package updated successfully");
        } catch (Exception e) {
            throw new AppException("Failed to update service package: " + e.getMessage());
        }
    }

    @Override
    public BaseResponse<?> getAllServicePackages(User admin) {
        try {
            java.util.List<ServicePackage> list = servicePackageRepository.findAll();
            logAdminAction(admin, "SERVICE_PACKAGE", null, "LIST_SERVICE_PACKAGES");
            return BaseResponse.success(list, "All service packages retrieved successfully");
        } catch (Exception e) {
            throw new AppException("Failed to list service packages: " + e.getMessage());
        }
    }

    @Override
    public BaseResponse<?> deleteServicePackage(Long packageId, User admin) {
        try {
            ServicePackage sp = servicePackageRepository.findById(packageId)
                    .orElseThrow(() -> new AppException("Service package not found"));
            servicePackageRepository.delete(sp);
            logAdminAction(admin, "SERVICE_PACKAGE", packageId, "DELETE_SERVICE_PACKAGE");
            return BaseResponse.success(null, "Service package deleted successfully");
        } catch (Exception e) {
            throw new AppException("Failed to delete service package: " + e.getMessage());
        }
    }

    @Override
    public BaseResponse<?> setAddOnServiceStatus(Long serviceId, boolean active, User admin) {
        try {
            AddOnService service = addOnServiceRepository.findById(serviceId)
                    .orElseThrow(() -> new AppException("Add-on service not found"));

            String newStatus = active ? "ACTIVE" : "INACTIVE";
            service.setStatus(newStatus);
            AddOnService saved = addOnServiceRepository.save(service);

            logAdminAction(admin, "ADDON_SERVICE", saved.getServiceId(), active ? "SET_ADDON_SERVICE_ACTIVE" : "SET_ADDON_SERVICE_INACTIVE");
            return BaseResponse.success(saved, active ? "Add-on service đã được set ACTIVE" : "Add-on service đã được set INACTIVE");
        } catch (Exception e) {
            throw new AppException("Failed to set add-on service status: " + e.getMessage());
        }
    }

}

