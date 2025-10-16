package com.evmarket.trade.serviceImp;

import com.evmarket.trade.entity.*;
import com.evmarket.trade.exception.AppException;
import com.evmarket.trade.repository.*;
import com.evmarket.trade.request.OrderRequest;
import com.evmarket.trade.response.common.BaseResponse;
import com.evmarket.trade.response.OrderResponse;
import com.evmarket.trade.response.ContactResponse;
import com.evmarket.trade.response.UserInfoResponse;
import com.evmarket.trade.service.BuyerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class BuyerServiceImpl implements BuyerService {
    
    @Autowired
    private ListingRepository listingRepository;
    
    @Autowired
    private VehicleRepository vehicleRepository;
    
    @Autowired
    private BatteryRepository batteryRepository;
    
    @Autowired
    private OrderRepository orderRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    // Browse listings
    @Override
    public BaseResponse<List<Listing>> getAllActiveListings() {
        try {
            List<Listing> listings = listingRepository.findByStatus("ACTIVE");
            return BaseResponse.success(listings, "Active listings retrieved successfully");
        } catch (Exception e) {
            throw new AppException("Failed to retrieve listings: " + e.getMessage());
        }
    }
    
    @Override
    public BaseResponse<List<Listing>> getListingsByType(String itemType) {
        try {
            if (!"vehicle".equals(itemType) && !"battery".equals(itemType)) {
                throw new AppException("Invalid item type. Must be 'vehicle' or 'battery'");
            }
            List<Listing> listings = listingRepository.findActiveByItemType(itemType);
            return BaseResponse.success(listings, "Listings retrieved successfully");
        } catch (Exception e) {
            throw new AppException("Failed to retrieve listings: " + e.getMessage());
        }
    }
    
    @Override
    public BaseResponse<List<Listing>> searchListings(String keyword) {
        try {
            List<Listing> listings = listingRepository.findByTitleContaining(keyword);
            return BaseResponse.success(listings, "Search results retrieved successfully");
        } catch (Exception e) {
            throw new AppException("Failed to search listings: " + e.getMessage());
        }
    }
    
    @Override
    public BaseResponse<List<Listing>> getListingsByLocation(String location) {
        try {
            List<Listing> listings = listingRepository.findByLocationContaining(location);
            return BaseResponse.success(listings, "Listings by location retrieved successfully");
        } catch (Exception e) {
            throw new AppException("Failed to retrieve listings by location: " + e.getMessage());
        }
    }
    
    @Override
    public BaseResponse<Listing> getListingDetails(Long listingId) {
        try {
            Listing listing = listingRepository.findById(listingId)
                .orElseThrow(() -> new AppException("Listing not found"));
            
            if (!"ACTIVE".equals(listing.getStatus())) {
                throw new AppException("Listing is not active");
            }
            
            return BaseResponse.success(listing, "Listing details retrieved successfully");
        } catch (Exception e) {
            throw new AppException("Failed to retrieve listing details: " + e.getMessage());
        }
    }
    
    // Get item details
    @Override
    public BaseResponse<Vehicle> getVehicleDetails(Long vehicleId) {
        try {
            Vehicle vehicle = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new AppException("Vehicle not found"));
            
            return BaseResponse.success(vehicle, "Vehicle details retrieved successfully");
        } catch (Exception e) {
            throw new AppException("Failed to retrieve vehicle details: " + e.getMessage());
        }
    }
    
    @Override
    public BaseResponse<Battery> getBatteryDetails(Long batteryId) {
        try {
            Battery battery = batteryRepository.findById(batteryId)
                .orElseThrow(() -> new AppException("Battery not found"));
            
            return BaseResponse.success(battery, "Battery details retrieved successfully");
        } catch (Exception e) {
            throw new AppException("Failed to retrieve battery details: " + e.getMessage());
        }
    }
    
    // Order management
    @Override
    public BaseResponse<OrderResponse> createOrder(OrderRequest request, User buyer) {
        try {
            Listing listing = listingRepository.findById(request.getListingId())
                .orElseThrow(() -> new AppException("Listing not found"));
            
            if (!"ACTIVE".equals(listing.getStatus())) {
                throw new AppException("Listing is not active");
            }
            
            // Check if buyer is not the seller
            if (listing.getUser().getUserId() == buyer.getUserId()) {
                throw new AppException("You cannot order your own listing");
            }
            
            // Check if there are any pending orders for this listing
            List<Order> pendingOrders = orderRepository.findPendingByListing(listing);
            if (!pendingOrders.isEmpty()) {
                throw new AppException("This listing already has pending orders");
            }
            
            // Calculate commission fee (5% of base price)
            BigDecimal commissionFee = request.getBasePrice().multiply(new BigDecimal("0.05"));
            BigDecimal totalAmount = request.getBasePrice().add(commissionFee);
            
            Order order = new Order();
            order.setBuyer(buyer);
            order.setSeller(listing.getUser());
            order.setListing(listing);
            order.setOrderDate(LocalDateTime.now());
            order.setStatus("PENDING");
            order.setBasePrice(request.getBasePrice());
            order.setCommissionFee(commissionFee);
            order.setTotalAmount(totalAmount);
            
            Order savedOrder = orderRepository.save(order);
            return BaseResponse.success(convertToOrderResponse(savedOrder), "Order created successfully");
        } catch (Exception e) {
            throw new AppException("Failed to create order: " + e.getMessage());
        }
    }
    
    @Override
    public BaseResponse<List<OrderResponse>> getMyOrders(User buyer) {
        try {
            List<Order> orders = orderRepository.findByBuyer(buyer);
            return BaseResponse.success(orders.stream().map(this::convertToOrderResponse).toList(), "Orders retrieved successfully");
        } catch (Exception e) {
            throw new AppException("Failed to retrieve orders: " + e.getMessage());
        }
    }
    
    @Override
    public BaseResponse<OrderResponse> getOrderById(Long orderId, User buyer) {
        try {
            Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new AppException("Order not found"));
            
            if (order.getBuyer().getUserId() != buyer.getUserId()) {
                throw new AppException("You can only view your own orders");
            }
            
            return BaseResponse.success(convertToOrderResponse(order), "Order retrieved successfully");
        } catch (Exception e) {
            throw new AppException("Failed to retrieve order: " + e.getMessage());
        }
    }
    
    @Override
    public BaseResponse<Void> cancelOrder(Long orderId, User buyer) {
        try {
            Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new AppException("Order not found"));
            
            if (order.getBuyer().getUserId() != buyer.getUserId()) {
                throw new AppException("You can only cancel your own orders");
            }
            
            if (!"PENDING".equals(order.getStatus())) {
                throw new AppException("Only pending orders can be cancelled");
            }
            
            order.setStatus("CANCELLED");
            orderRepository.save(order);
            
            return BaseResponse.success(null, "Order cancelled successfully");
        } catch (Exception e) {
            throw new AppException("Failed to cancel order: " + e.getMessage());
        }
    }
    
    // Contact information (only for battery purchases)
    @Override
    public BaseResponse<ContactResponse> getSellerContactInfo(Long orderId, User buyer) {
        try {
            Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new AppException("Order not found"));
            
            if (order.getBuyer().getUserId() != buyer.getUserId()) {
                throw new AppException("You can only view contact info for your own orders");
            }
            
            // Only allow contact info for battery purchases
            if (!"battery".equals(order.getListing().getItemType())) {
                throw new AppException("Contact information is only available for battery purchases");
            }
            
            // Only show contact info after order is confirmed
            if (!"CONFIRMED".equals(order.getStatus()) && !"COMPLETED".equals(order.getStatus())) {
                throw new AppException("Contact information is only available for confirmed orders");
            }
            
            User seller = order.getSeller();
            ContactResponse contact = ContactResponse.builder()
                    .fullName(seller.getFullName())
                    .phone(seller.getPhone())
                    .email(seller.getEmail())
                    .build();
            return BaseResponse.success(contact, "Seller contact information retrieved successfully");
        } catch (Exception e) {
            throw new AppException("Failed to retrieve contact information: " + e.getMessage());
        }
    }

    private OrderResponse convertToOrderResponse(Order order) {
        return OrderResponse.builder()
                .orderId(order.getOrderId())
                .listingId(order.getListing() != null ? order.getListing().getListingId() : null)
                .buyer(convertUser(order.getBuyer()))
                .seller(convertUser(order.getSeller()))
                .basePrice(order.getBasePrice())
                .commissionFee(order.getCommissionFee())
                .totalAmount(order.getTotalAmount())
                .status(order.getStatus())
                .orderDate(order.getOrderDate())
                .build();
    }

    private UserInfoResponse convertUser(User user) {
        if (user == null) return null;
        return UserInfoResponse.builder()
                .userId((long) user.getUserId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .username(user.getUsername())
                .phone(user.getPhone())
                .status(user.getStatus())
                .dateOfBirth(user.getDateOfBirth() != null ? user.getDateOfBirth().toString() : null)
                .gender(user.getGender())
                .identityCard(user.getIdentityCard())
                .address(user.getAddress())
                .createdAt(user.getCreatedAt())
                .build();
    }
}

