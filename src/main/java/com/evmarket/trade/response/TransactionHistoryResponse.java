package com.evmarket.trade.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransactionHistoryResponse {
    private Long orderId;
    private Long listingId;
    private String listingTitle;
    private String itemType; // "vehicle" or "battery"
    
    // User information
    private UserInfoResponse buyer;
    private UserInfoResponse seller;
    
    // Order information
    private String orderStatus;
    private LocalDateTime orderDate;
    private BigDecimal basePrice;
    private BigDecimal commissionFee;
    private BigDecimal totalAmount;
    
    // Payment information
    private PaymentInfo payment;
    
    // Contract information
    private ContractInfo contract;
    
    // Review information
    private List<ReviewInfo> reviews;
    
    // Nested classes for better organization
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class PaymentInfo {
        private Long paymentId;
        private String paymentStatus;
        private String paymentGateway;
        private BigDecimal amount;
        private LocalDateTime paymentDate;
        private String paymentType; // PACKAGE, MEMBERSHIP, ADDON
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ContractInfo {
        private Long contractId;
        private String contractStatus;
        private Boolean sellerSigned;
        private Boolean buyerSigned;
        private LocalDateTime signedAt;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ReviewInfo {
        private Long reviewId;
        private UserInfoResponse reviewer;
        private UserInfoResponse targetUser;
        private Integer rating;
        private String comment;
        private LocalDateTime createdAt;
    }
}

