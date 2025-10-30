package com.evmarket.trade.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ContractSignRequest {
    
    @NotNull(message = "Contract ID is required")
    private Long contractId;
    
    @NotBlank(message = "OTP is required")
    private String otp;
}
