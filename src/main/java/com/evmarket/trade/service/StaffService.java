package com.evmarket.trade.service;

import com.evmarket.trade.entity.User;
import com.evmarket.trade.response.ListingResponse;
import com.evmarket.trade.response.OrderResponse;
import com.evmarket.trade.response.common.BaseResponse;

import java.util.List;

public interface StaffService {
    
    /**
     * Lấy các orders được gán cho staff
     */
    BaseResponse<List<OrderResponse>> getAssignedOrders(User staff);
    
    /**
     * Lấy thông tin bài đăng (listing) của order mà staff được gán
     */
    BaseResponse<ListingResponse> getOrderListing(Long orderId, User staff);
    
}
