package com.evmarket.trade.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;


@Data
public class ContractAddOnRequest {
    
    @NotNull(message = "Contract ID is required")
    private Long contractId;
    
    @NotNull(message = "Service ID is required")
    private Long serviceId;
    
    // Fee will be taken from AddOnService.defaultFee
    @Pattern(regexp = "^(BUYER|SELLER)$", message = "chargedTo must be BUYER or SELLER")
    private String chargedTo;
}




