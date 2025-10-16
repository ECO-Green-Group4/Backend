package com.evmarket.trade.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderResponse {
    private Long orderId;
    private Long listingId;
    private UserInfoResponse buyer;
    private UserInfoResponse seller;
    private BigDecimal basePrice;
    private BigDecimal commissionFee;
    private BigDecimal totalAmount;
    private String status;
    private LocalDateTime orderDate;
    private Long assignedStaffId;
}
