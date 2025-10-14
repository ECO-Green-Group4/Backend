package com.evmarket.trade.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class ContractSignRequest {
    
    @NotNull(message = "Contract ID is required")
    private Long contractId;
    
    @NotBlank(message = "OTP is required")
    private String otp;
    
    @NotBlank(message = "User type is required")
    @Pattern(regexp = "^(buyer|seller)$", message = "User type must be 'buyer' or 'seller'")
    private String userType;
}
