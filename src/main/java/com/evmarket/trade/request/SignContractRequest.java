package com.evmarket.trade.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class SignContractRequest {
    
    @NotNull(message = "Contract ID is required")
    @Positive(message = "Contract ID must be positive")
    private Long contractId;
    
    @NotBlank(message = "OTP is required")
    private String otp;

    // Getters and Setters
    public Long getContractId() { return contractId; }
    public void setContractId(Long contractId) { this.contractId = contractId; }
    
    public String getOtp() { return otp; }
    public void setOtp(String otp) { this.otp = otp; }
}
