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
public class PaymentResponse {
    private Long paymentId;
    private String paymentType;
    private BigDecimal amount;
    private String currency;
    private String status;
    private String paymentUrl; // URL để redirect đến VNPay
    private LocalDateTime paymentDate;
    private LocalDateTime expiryTime;
    private String gatewayTransactionId;

    // Additional info
    private Long contractId;
    private Long contractAddOnId;
    private Long listingPackageId;
}