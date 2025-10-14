package com.evmarket.trade.serviceImp;

import com.evmarket.trade.entity.Order;
import com.evmarket.trade.entity.User;
import com.evmarket.trade.entity.Listing;
import com.evmarket.trade.repository.OrderRepository;
import com.evmarket.trade.repository.ListingRepository;
import com.evmarket.trade.request.CreateOrderRequest;
import com.evmarket.trade.response.OrderResponse;
import com.evmarket.trade.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderRepository orderRepository;
    
    @Autowired
    private ListingRepository listingRepository;

    @Override
    public Order createOrder(CreateOrderRequest request, User buyer) {
        Listing listing = listingRepository.findById(request.getListingId())
                .orElseThrow(() -> new RuntimeException("Listing not found with id: " + request.getListingId()));
        
        if (!"ACTIVE".equals(listing.getStatus())) {
            throw new RuntimeException("Listing is not available for ordering");
        }

        // Check if buyer is not the same as seller
        if (buyer.getUserId() == listing.getUser().getUserId()) {
            throw new RuntimeException("Cannot order your own listing");
        }

        Order order = new Order();
        order.setBuyer(buyer);
        order.setSeller(listing.getUser());
        order.setListing(listing);
        order.setOrderDate(LocalDateTime.now());
        order.setStatus("PENDING");
        
        // Calculate prices based on item type
        BigDecimal basePrice = getItemPrice(listing);
        BigDecimal commissionFee = calculateCommissionFee(basePrice);
        BigDecimal totalAmount = basePrice.add(commissionFee);
        
        order.setBasePrice(basePrice);
        order.setCommissionFee(commissionFee);
        order.setTotalAmount(totalAmount);
        
        // Update listing status to prevent multiple orders
        listing.setStatus("PENDING");
        listingRepository.save(listing);
        
        return orderRepository.save(order);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponse> getOrdersByBuyer(User buyer) {
        List<Order> orders = orderRepository.findByBuyer(buyer);
        return orders.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponse> getOrdersBySeller(User seller) {
        List<Order> orders = orderRepository.findBySeller(seller);
        return orders.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public OrderResponse getOrderById(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + orderId));
        return convertToResponse(order);
    }

    @Override
    public Order updateOrderStatus(Long orderId, String status) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + orderId));
        order.setStatus(status);
        return orderRepository.save(order);
    }

    private BigDecimal getItemPrice(Listing listing) {
        if ("vehicle".equals(listing.getItemType())) {
            // Get vehicle price from vehicle entity
            // This would require a join or separate query
            return new BigDecimal("10000000"); // Placeholder - should get from vehicle
        } else if ("battery".equals(listing.getItemType())) {
            // Get battery price from battery entity
            return new BigDecimal("5000000"); // Placeholder - should get from battery
        }
        return BigDecimal.ZERO;
    }

    private BigDecimal calculateCommissionFee(BigDecimal basePrice) {
        // 5% commission fee
        return basePrice.multiply(new BigDecimal("0.05"));
    }

    private OrderResponse convertToResponse(Order order) {
        return new OrderResponse(
                order.getOrderId(),
                order.getListing().getListingId(),
                order.getListing().getTitle(),
                order.getListing().getItemType(),
                order.getBuyer().getFullName(),
                order.getBuyer().getPhone(),
                order.getSeller().getFullName(),
                order.getSeller().getPhone(),
                order.getOrderDate(),
                order.getStatus(),
                order.getBasePrice(),
                order.getCommissionFee(),
                order.getTotalAmount()
        );
    }
}
