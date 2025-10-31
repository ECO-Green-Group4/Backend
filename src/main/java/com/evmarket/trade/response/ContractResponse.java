package com.evmarket.trade.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ContractResponse {
    private Long contractId;
    private Long orderId;
    private String status;
    private Boolean sellerSigned;
    private Boolean buyerSigned;
    private LocalDateTime signedAt;
    private LocalDateTime createdAt;
}
