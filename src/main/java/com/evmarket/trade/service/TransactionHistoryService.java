package com.evmarket.trade.service;

import com.evmarket.trade.entity.User;
import com.evmarket.trade.response.TransactionHistoryResponse;
import com.evmarket.trade.response.common.BaseResponse;

import java.time.LocalDateTime;
import java.util.List;

public interface TransactionHistoryService {
    BaseResponse<List<TransactionHistoryResponse>> getMyTransactionHistory(User user);
    BaseResponse<List<TransactionHistoryResponse>> getMyTransactionHistory(
            User user, 
            String status, 
            LocalDateTime fromDate, 
            LocalDateTime toDate);
    BaseResponse<TransactionHistoryResponse> getTransactionDetail(Long orderId, User user);
}

