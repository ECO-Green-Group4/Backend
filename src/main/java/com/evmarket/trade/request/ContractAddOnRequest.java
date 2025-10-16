package com.evmarket.trade.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;


@Data
public class ContractAddOnRequest {
    
    @NotNull(message = "Contract ID is required")
    private Long contractId;
    
    @NotNull(message = "Service ID is required")
    private Long serviceId;
    
    // Fee will be taken from AddOnService.defaultFee
}




