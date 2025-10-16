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
    private Long contractId;         // null nếu là payment gói đăng tin
    private Long listingPackageId;   // null nếu là payment hợp đồng / add-on
    private Long payerId;
    private String payerName;
    private String paymentMethod;
    private BigDecimal amount;
    private String status;
    private LocalDateTime paymentDate;
}




