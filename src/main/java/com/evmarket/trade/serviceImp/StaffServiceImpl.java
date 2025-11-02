package com.evmarket.trade.serviceImp;

import com.evmarket.trade.entity.Listing;
import com.evmarket.trade.entity.Order;
import com.evmarket.trade.entity.User;
import com.evmarket.trade.exception.AppException;
import com.evmarket.trade.repository.OrderRepository;
import com.evmarket.trade.response.ListingResponse;
import com.evmarket.trade.response.OrderResponse;
import com.evmarket.trade.response.UserInfoResponse;
import com.evmarket.trade.response.common.BaseResponse;
import com.evmarket.trade.service.StaffService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class StaffServiceImpl implements StaffService {

    @Autowired
    private OrderRepository orderRepository;

    @Override
    @Transactional(readOnly = true)
    public BaseResponse<List<OrderResponse>> getAssignedOrders(User staff) {
        try {
            List<Order> orders = orderRepository.findByAssignedStaffId((long) staff.getUserId());
            List<OrderResponse> orderResponses = orders.stream()
                    .map(this::convertToResponse)
                    .collect(Collectors.toList());
            return BaseResponse.success(orderResponses, "Danh sách orders được gán cho staff");
        } catch (Exception e) {
            throw new AppException("Lỗi khi lấy danh sách orders được gán: " + e.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public BaseResponse<ListingResponse> getOrderListing(Long orderId, User staff) {
        try {
            // Tìm order
            Order order = orderRepository.findById(orderId)
                    .orElseThrow(() -> new AppException("Không tìm thấy order với ID: " + orderId));

            // Kiểm tra xem order có được gán cho staff này không
            if (order.getAssignedStaffId() == null || !order.getAssignedStaffId().equals((long) staff.getUserId())) {
                throw new AppException("Bạn không được gán cho order này");
            }

            // Lấy listing từ order
            Listing listing = order.getListing();
            if (listing == null) {
                throw new AppException("Order không có listing liên quan");
            }

            // Convert listing to response
            ListingResponse listingResponse = convertListingToResponse(listing);

            return BaseResponse.success(listingResponse, "Thông tin bài đăng được lấy thành công");
        } catch (AppException e) {
            throw e;
        } catch (Exception e) {
            throw new AppException("Lỗi khi lấy thông tin bài đăng: " + e.getMessage());
        }
    }


    private OrderResponse convertToResponse(Order order) {
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

    private ListingResponse convertListingToResponse(Listing listing) {
        if (listing == null) return null;

        ListingResponse response = ListingResponse.builder()
                .listingId(listing.getListingId())
                .user(convertUserToUserInfo(listing.getUser()))
                .itemType(listing.getItemType())
                .title(listing.getTitle())
                .description(listing.getDescription())
                .images(listing.getImages())
                .location(listing.getLocation())
                .price(listing.getPrice())
                .status(listing.getStatus())
                .createdAt(listing.getCreatedAt())
                .postType(listing.getPostType())
                .build();

        // Set vehicle specific fields
        if ("vehicle".equals(listing.getItemType())) {
            response.setBrand(listing.getBrand());
            response.setModel(listing.getModel());
            response.setYear(listing.getYear());
            response.setBatteryCapacity(listing.getBatteryCapacity());
            response.setMileage(listing.getMileage());
            response.setCondition(listing.getCondition());
            response.setBodyType(listing.getBodyType());
            response.setColor(listing.getColor());
            response.setInspection(listing.getInspection());
            response.setOrigin(listing.getOrigin());
            response.setNumberOfSeats(listing.getNumberOfSeats());
            response.setLicensePlate(listing.getLicensePlate());
            response.setAccessories(listing.getAccessories());
        }

        // Set battery specific fields
        if ("battery".equals(listing.getItemType())) {
            response.setBatteryBrand(listing.getBatteryBrand());
            response.setVoltage(listing.getVoltage());
            response.setType(listing.getType());
            response.setCapacity(listing.getCapacity());
            response.setHealthPercent(listing.getHealthPercent());
            response.setManufactureYear(listing.getManufactureYear());
            response.setChargeCycles(listing.getChargeCycles());
            response.setOrigin(listing.getOrigin());
        }

        return response;
    }
}
