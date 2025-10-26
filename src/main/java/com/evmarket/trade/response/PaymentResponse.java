package com.evmarket.trade.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentResponse {
    private Long id;
    private Long paymentId;
    private String paymentType;
    private String paymentMethod;
    private BigDecimal amount;
    private String currency;
    private String paymentStatus;
    private String status;
    private String paymentUrl; // URL để redirect đến VNPay
    private LocalDateTime paymentDate;
    private LocalDateTime createdAt;
    private LocalDateTime expiryTime;
    private String gatewayTransactionId;
    private String transactionId;

    // Additional info
    private Long contractId;
    private Long contractAddOnId;
    private Long listingPackageId;

    // Các field cho MoMo integration
    private String deeplink;          // Deeplink cho mobile app (mở app MoMo trực tiếp)
    private String qrCodeUrl;         // URL QR code (nếu MoMo cung cấp)

    // Toàn bộ response từ gateway
    private Map<String, Object> gatewayResponse;
}