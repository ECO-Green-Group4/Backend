package com.evmarket.trade.serviceImp;

import com.evmarket.trade.entity.Order;
import com.evmarket.trade.entity.User;
import com.evmarket.trade.exception.AppException;
import com.evmarket.trade.repository.OrderRepository;
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
    public BaseResponse<OrderResponse> getAssignedOrderById(Long orderId, User staff) {
        try {
            Order order = orderRepository.findById(orderId)
                    .orElseThrow(() -> new AppException("Không tìm thấy order với ID: " + orderId));
            
            // Kiểm tra order có được gán cho staff này không
            if (order.getAssignedStaffId() == null || !order.getAssignedStaffId().equals((long) staff.getUserId())) {
                throw new AppException("Order này không được gán cho bạn");
            }
            
            OrderResponse orderResponse = convertToResponse(order);
            return BaseResponse.success(orderResponse, "Chi tiết order được gán");
        } catch (Exception e) {
            throw new AppException("Lỗi khi lấy chi tiết order: " + e.getMessage());
        }
    }
    
    private OrderResponse convertToResponse(Order order) {
        return OrderResponse.builder()
                .orderId(order.getOrderId())
                .listingId(order.getListing() != null ? order.getListing().getListingId() : null)
                .buyer(convertUserToUserInfo(order.getBuyer()))
                .seller(convertUserToUserInfo(order.getSeller()))
                .basePrice(order.getBasePrice())
                .commissionFee(order.getCommissionFee())
                .totalAmount(order.getTotalAmount())
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
}
