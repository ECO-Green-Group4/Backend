package com.evmarket.trade.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ContractDetailsResponse {
    private Long contractId;
    private Long orderId;
    private String status;
    private Boolean sellerSigned;
    private Boolean buyerSigned;
    private LocalDateTime signedAt;
    private LocalDateTime createdAt;
    private List<ContractAddOnResponse> addons;
}



