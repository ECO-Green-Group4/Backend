package com.evmarket.trade.service;

import com.evmarket.trade.entity.Order;
import com.evmarket.trade.entity.User;
import com.evmarket.trade.request.CreateOrderRequest;
import com.evmarket.trade.response.OrderResponse;

import java.util.List;

public interface OrderService {
    Order createOrder(CreateOrderRequest request, User buyer);
    List<OrderResponse> getOrdersByBuyer(User buyer);
    List<OrderResponse> getOrdersBySeller(User seller);
    OrderResponse getOrderById(Long orderId);
    Order updateOrderStatus(Long orderId, String status);
}
