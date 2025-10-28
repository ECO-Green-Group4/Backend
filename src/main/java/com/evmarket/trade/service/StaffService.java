package com.evmarket.trade.service;

import com.evmarket.trade.entity.User;
import com.evmarket.trade.response.OrderResponse;
import com.evmarket.trade.response.common.BaseResponse;

import java.util.List;

public interface StaffService {
    
    /**
     * Lấy các orders được gán cho staff
     */
    BaseResponse<List<OrderResponse>> getAssignedOrders(User staff);
    
}
