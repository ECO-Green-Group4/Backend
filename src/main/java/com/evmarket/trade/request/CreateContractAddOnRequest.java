package com.evmarket.trade.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public class CreateContractAddOnRequest {
    
    @NotNull(message = "Contract ID is required")
    @Positive(message = "Contract ID must be positive")
    private Long contractId;
    
    @NotNull(message = "Service ID is required")
    @Positive(message = "Service ID must be positive")
    private Long serviceId;
    
    @NotNull(message = "Fee is required")
    @Positive(message = "Fee must be positive")
    private BigDecimal fee;

    // Getters and Setters
    public Long getContractId() { return contractId; }
    public void setContractId(Long contractId) { this.contractId = contractId; }
    
    public Long getServiceId() { return serviceId; }
    public void setServiceId(Long serviceId) { this.serviceId = serviceId; }
    
    public BigDecimal getFee() { return fee; }
    public void setFee(BigDecimal fee) { this.fee = fee; }
}
