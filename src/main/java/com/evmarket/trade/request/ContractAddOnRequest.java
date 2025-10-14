package com.evmarket.trade.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ContractAddOnRequest {
    
    @NotNull(message = "Contract ID is required")
    private Long contractId;
    
    @NotNull(message = "Service ID is required")
    private Long serviceId;
    
    @NotNull(message = "Fee is required")
    private BigDecimal fee;
}




